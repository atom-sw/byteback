package byteback.analysis;

import byteback.analysis.transformer.CallCheckTransformer;
import byteback.analysis.transformer.DynamicToStaticTransformer;
import byteback.analysis.transformer.ExceptionAssumptionTransformer;
import byteback.analysis.transformer.ExpressionFolder;
import byteback.analysis.transformer.GuardTransformer;
import byteback.analysis.transformer.IndexCheckTransformer;
import byteback.analysis.transformer.InvariantExpander;
import byteback.analysis.transformer.LogicUnitTransformer;
import byteback.analysis.transformer.LogicValueTransformer;
import byteback.analysis.transformer.NullCheckTransformer;
import byteback.analysis.transformer.PureTransformer;
import byteback.analysis.transformer.QuantifierValueTransformer;
import byteback.analysis.util.AnnotationElems;
import byteback.analysis.util.SootAnnotations;
import byteback.analysis.util.SootBodies;
import byteback.analysis.util.SootClasses;
import byteback.analysis.util.SootHosts;
import byteback.analysis.util.SootMethods;
import byteback.util.Lazy;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import soot.ArrayType;
import soot.Body;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Trap;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Grimp;
import soot.jimple.FieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.NewExpr;
import soot.tagkit.AbstractHost;
import soot.toolkits.scalar.UnusedLocalEliminator;

public class RootResolver {

	private static final Lazy<RootResolver> instance = Lazy.from(RootResolver::new);

	public void transformMethod(final SootMethod method) {
		if (SootMethods.hasBody(method)) {
			if (SootHosts.hasAnnotation(method, Namespace.PRELUDE_ANNOTATION)) {
				return;
			}

			SootBodies.validateCalls(method.retrieveActiveBody());
			final Body body = Grimp.v().newBody(method.getActiveBody(), "");

			LogicUnitTransformer.v().transform(body);
			new LogicValueTransformer(body.getMethod().getReturnType()).transform(body);

			if (!Namespace.isPureMethod(method) && !Namespace.isPredicateMethod(method)) {
				CallCheckTransformer.v().transform(body);
			} else {
				PureTransformer.v().transform(body);
			}

			new ExpressionFolder().transform(body);

			if (!Namespace.isPureMethod(method) && !Namespace.isPredicateMethod(method)) {
				if (checkArrayDereference || SootHosts.hasAnnotation(method, Namespace.MODEL_IOBE_ANNOTATION)) {
					IndexCheckTransformer.v().transform(body);
				}

				if (checkNullDereference || SootHosts.hasAnnotation(method, Namespace.MODEL_NPE_ANNOTATION)) {
					NullCheckTransformer.v().transform(body);
				}
			}

			UnusedLocalEliminator.v().transform(body);
			QuantifierValueTransformer.v().transform(body);

			ExceptionAssumptionTransformer.v().transform(body);
			DynamicToStaticTransformer.v().transform(body);

			if (!Namespace.isPureMethod(method) && !Namespace.isPredicateMethod(method)) {
				GuardTransformer.v().transform(body);
			}

			InvariantExpander.v().transform(body);

			method.setActiveBody(body);
		}
	}

	private final Deque<AbstractHost> next;

	private final Set<AbstractHost> visited;

	private boolean checkArrayDereference;

	private boolean checkNullDereference;

	public void setCheckArrayDereference(boolean f) {
		checkArrayDereference = f;
	}

	public void setCheckNullDereference(boolean f) {
		checkNullDereference = f;
	}

	public static RootResolver v() {
		return instance.get();
	}

	private RootResolver() {
		this.next = new LinkedList<>();
		this.visited = new HashSet<>();
		this.checkArrayDereference = false;
		this.checkNullDereference = false;
	}

	public boolean classIsValid(final SootClass clazz) {
		return !Namespace.isAnnotationClass(clazz);
	}

	public void addNext(final AbstractHost host) {
		if (!visited.contains(host)) {
			visited.add(host);
			next.add(host);
		}
	}

	public void addType(final Type type) {
		if (type instanceof ArrayType arrayType) {
			addType(arrayType.getElementType());
		}

		if (type instanceof RefType refType) {
			final SootClass clazz = refType.getSootClass();
			addClass(clazz);
		}
	}

	public void addClass(final SootClass clazz) {
		if (classIsValid(clazz)) {
			addNext(clazz);
		}
	}

	public void addMethod(final SootMethod method) {
		if (classIsValid(method.getDeclaringClass())) {
			addNext(method);
		}
	}

	public void addField(final SootField field) {
		if (classIsValid(field.getDeclaringClass())) {
			addNext(field);
		}
	}

	public void scanField(final SootField field) {
		final SootClass declaringClass = field.getDeclaringClass();
		addType(field.getType());
		addClass(declaringClass);
	}

	public void scanSignature(final SootMethod method) {
		for (final Type type : method.getParameterTypes()) {
			addType(type);
		}

		SootHosts.getAnnotations(method).forEach((tag) -> {
			SootAnnotations.getAnnotations(tag).forEach((sub) -> {
				sub.getElems().stream().forEach((elem) -> {
					final String classDescriptor = new AnnotationElems.ClassElemExtractor().visit(elem);

					if (classDescriptor != null) {
						final String className = Namespace.stripDescriptor(classDescriptor);
						addType(Scene.v().getType(className));
					}
				});
			});
		});

		addType(method.getReturnType());
	}

	public void scanMethod(final SootMethod method) {
		if (!SootMethods.hasBody(method)) {
			scanSignature(method);
			return;
		}

		transformMethod(method);
		final Body body = method.retrieveActiveBody();

		for (final ValueBox useDefBox : body.getUseAndDefBoxes()) {
			final Value useDef = useDefBox.getValue();
			addType(useDef.getType());

			if (useDef instanceof InvokeExpr invoke) {
				final SootMethod usedMethod = invoke.getMethod();
				final SootClass declaringClass = usedMethod.getDeclaringClass();
				addMethod(usedMethod);
				addClass(declaringClass);
			}

			if (useDef instanceof InstanceOfExpr instanceOfExpr) {
				addType(instanceOfExpr.getCheckType());
			}

			if (useDef instanceof NewExpr newExpr) {
				addType(newExpr.getBaseType());
			}

			if (useDef instanceof FieldRef fieldRef) {
				addField(fieldRef.getField());
			}
		}

		for (final Trap trap : body.getTraps()) {
			addClass(trap.getException());
		}
	}

	public void scanClass(final SootClass clazz) {
		if (clazz.hasSuperclass()) {
			addClass(clazz.getSuperclass());
		}

		for (final SootClass intf : clazz.getInterfaces()) {
			addClass(intf);
		}

		if (!SootClasses.isBasicClass(clazz)) {
			for (final SootMethod method : clazz.getMethods()) {
				addMethod(method);
			}

			for (final SootField field : clazz.getFields()) {
				addField(field);
			}
		}
	}

	public Iterable<SootClass> getUsedClasses() {
		return visited.stream().filter((v) -> v instanceof SootClass).map((v) -> (SootClass) v)::iterator;
	}

	public Iterable<SootMethod> getUsedMethods() {
		return visited.stream().filter((v) -> v instanceof SootMethod).map((v) -> (SootMethod) v)::iterator;
	}

	public Iterable<SootField> getUsedFields() {
		return visited.stream().filter((v) -> v instanceof SootField).map((v) -> (SootField) v)::iterator;
	}

	public void resolve(final Collection<SootClass> initials) {
		for (final SootClass initial : initials) {
			addClass(initial);

			for (SootMethod method : initial.getMethods()) {
				addMethod(method);
			}
		}

		resolveAll();
	}

	public void resolveAll() {
		while (!next.isEmpty()) {
			final AbstractHost current = next.pollFirst();
			scan(current);
		}
	}

	public void scan(final AbstractHost host) {
		if (host instanceof SootClass clazz) {
			scanClass(clazz);
		} else if (host instanceof SootMethod method) {
			scanMethod(method);
		} else if (host instanceof SootField field) {
			scanField(field);
		}
	}

	public List<SootClass> getVisibleSubclassesOf(final SootClass clazz) {
		final Collection<SootClass> subclasses = Scene.v().getOrMakeFastHierarchy().getSubclassesOf(clazz);

		return subclasses.stream().filter((c) -> visited.contains(c)).toList();
	}

	public void ensureResolved(final AbstractHost host) {
		if (!visited.contains(host)) {
			scan(host);
			resolveAll();
		}
	}

}
