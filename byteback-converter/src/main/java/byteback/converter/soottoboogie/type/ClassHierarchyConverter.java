package byteback.converter.soottoboogie.type;

import byteback.analysis.RootResolver;
import byteback.converter.soottoboogie.Prelude;
import byteback.frontend.boogie.ast.AndOperation;
import byteback.frontend.boogie.ast.AxiomDeclaration;
import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.ImplicationOperation;
import byteback.frontend.boogie.ast.List;
import byteback.frontend.boogie.ast.NegationOperation;
import byteback.frontend.boogie.ast.PartialOrderOperation;
import byteback.frontend.boogie.ast.SetBinding;
import byteback.frontend.boogie.ast.UniversalQuantifier;
import byteback.frontend.boogie.ast.ValueReference;
import byteback.frontend.boogie.builder.QuantifierExpressionBuilder;
import byteback.frontend.boogie.builder.SetBindingBuilder;
import byteback.util.Cons;
import byteback.util.Lazy;
import byteback.util.Stacks;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import soot.SootClass;

public class ClassHierarchyConverter {

	private static final Lazy<ClassHierarchyConverter> instance = Lazy.from(ClassHierarchyConverter::new);

	public static ClassHierarchyConverter v() {
		return instance.get();
	}

	public static String makeQuantifiedTypeVariableName(final int index) {
		return "t" + index;
	}

	public static Expression reduceConjunction(Stack<Expression> expressions) {
		if (expressions.size() > 1) {
			return Stacks.reduce(expressions, AndOperation::new);
		} else {
			return expressions.pop();
		}
	}

	public static <T> Set<Cons<T, T>> computePairs(final Iterable<T> xs) {
		final Set<Cons<T, T>> r = new HashSet<>();

		final Iterator<T> leftIterator = xs.iterator();

		while (leftIterator.hasNext()) {
			final T left = leftIterator.next();
			final Iterator<T> rightIterator = xs.iterator();

			while (rightIterator.hasNext()) {
				final T right = rightIterator.next();

				if (left == right) {
					break;
				}

				r.add(new Cons<T, T>(left, right));
			}
		}

		return r;
	}

	public static SetBinding makeBinding(final int i) {
		final String parameterName = makeQuantifiedTypeVariableName(i);
		final var bindingBuilder = new SetBindingBuilder();
		bindingBuilder.typeAccess(Prelude.v().getTypeType().makeTypeAccess());
		bindingBuilder.name(parameterName);

		return bindingBuilder.build();
	}

	public static List<AxiomDeclaration> makeDisjointAxioms(final Collection<SootClass> classes) {
		List<AxiomDeclaration> axioms = new List<>();

		for (final Cons<SootClass, SootClass> classPair : computePairs(classes)) {
			final var quantifierBuilder = new QuantifierExpressionBuilder();
			quantifierBuilder.quantifier(new UniversalQuantifier());
			quantifierBuilder.addBinding(makeBinding(1));
			quantifierBuilder.addBinding(makeBinding(2));

			final ValueReference left = ValueReference.of(makeQuantifiedTypeVariableName(1));
			final ValueReference right = ValueReference.of(makeQuantifiedTypeVariableName(2));
			final ValueReference leftType = ValueReference.of(ReferenceTypeConverter.typeName(classPair.car));
			final ValueReference rightType = ValueReference.of(ReferenceTypeConverter.typeName(classPair.cdr));

			final Expression l = new PartialOrderOperation(left, right);
			final Expression r = new PartialOrderOperation(right, left);
			final Expression e = new ImplicationOperation(
					new AndOperation(new PartialOrderOperation(left, leftType),
							new PartialOrderOperation(right, rightType)),
					new AndOperation(new NegationOperation(l), new NegationOperation(r)));

			quantifierBuilder.addTrigger(new PartialOrderOperation(left, leftType));
			quantifierBuilder.addTrigger(new PartialOrderOperation(right, rightType));
			quantifierBuilder.operand(e);

			axioms.add(new AxiomDeclaration(new List<>(), quantifierBuilder.build()));
		}

		return axioms;
	}

	public static List<AxiomDeclaration> makeDisjointAxiom(final SootClass clazz, final RootResolver resolver) {
		final Collection<SootClass> subclasses = resolver.getVisibleSubclassesOf(clazz);

		if (subclasses.size() > 1) {
			return makeDisjointAxioms(subclasses);
		}

		return new List<>();
	}

	public static AxiomDeclaration makeExtendsAxiom(final SootClass clazz, final SootClass superClazz) {
		final var axiomDeclaration = new AxiomDeclaration();
		final Expression bT1 = new TypeReferenceExtractor().visit(clazz.getType());
		final Expression bT2 = new TypeReferenceExtractor().visit(superClazz.getType());
		axiomDeclaration.setExpression(new PartialOrderOperation(bT1, bT2));

		return axiomDeclaration;
	}

	public List<AxiomDeclaration> convert(final SootClass clazz, final RootResolver resolver) {
		final var axioms = new List<AxiomDeclaration>();
		final var superClasses = new ArrayList<SootClass>();
		final SootClass superClass = clazz.getSuperclassUnsafe();

		if (superClass != null) {
			superClasses.add(clazz.getSuperclassUnsafe());
		}

		superClasses.addAll(clazz.getInterfaces());

		for (final SootClass superType : superClasses) {
			axioms.add(makeExtendsAxiom(clazz, superType));
		}

		axioms.addAll(makeDisjointAxiom(clazz, resolver));

		return axioms;
	}

}
