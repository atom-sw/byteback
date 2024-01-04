package byteback.converter.soottoboogie.method.procedure;

import static byteback.converter.soottoboogie.expression.PureExpressionExtractor.sanitizeName;

import byteback.analysis.Namespace;
import byteback.analysis.RootResolver;
import byteback.analysis.util.AnnotationElems.ClassElemExtractor;
import byteback.analysis.util.AnnotationElems.StringElemExtractor;
import byteback.analysis.util.SootAnnotations;
import byteback.analysis.util.SootBodies;
import byteback.analysis.util.SootHosts;
import byteback.analysis.util.SootMethods;
import byteback.converter.soottoboogie.Convention;
import byteback.converter.soottoboogie.ConversionException;
import byteback.converter.soottoboogie.Prelude;
import byteback.converter.soottoboogie.expression.PureExpressionExtractor;
import byteback.converter.soottoboogie.method.MethodConverter;
import byteback.converter.soottoboogie.method.function.FunctionManager;
import byteback.converter.soottoboogie.type.TypeAccessExtractor;
import byteback.converter.soottoboogie.type.TypeReferenceExtractor;
import byteback.frontend.boogie.ast.*;
import byteback.frontend.boogie.builder.BoundedBindingBuilder;
import byteback.frontend.boogie.builder.ProcedureDeclarationBuilder;
import byteback.frontend.boogie.builder.ProcedureSignatureBuilder;
import byteback.frontend.boogie.builder.VariableDeclarationBuilder;
import byteback.util.Lazy;
import java.util.ArrayList;
import java.util.function.Function;
import soot.BooleanType;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.tagkit.AnnotationElem;

public class ProcedureConverter extends MethodConverter {

	public static final String PARAMETER_PREFIX = "?";

	private static final Lazy<ProcedureConverter> instance = Lazy.from(ProcedureConverter::new);

	public static ProcedureConverter v() {
		return instance.get();
	}

	public static String parameterName(final Local local) {
		return PARAMETER_PREFIX + sanitizeName(local.getName());
	}

	public static BoundedBinding makeBinding(final String name, final Type type) {
		final var bindingBuilder = new BoundedBindingBuilder();
		final SymbolicReference typeReference = new TypeReferenceExtractor().visit(type);
		final TypeAccess typeAccess = new TypeAccessExtractor().visit(type);
		bindingBuilder.addName(name).typeAccess(typeAccess);

		if (typeReference != null) {
			final FunctionReference instanceOfReference = Prelude.v().getInstanceOfFunction().makeFunctionReference();
			final ValueReference heapReference = Prelude.v().getHeapVariable().makeValueReference();
			instanceOfReference.addArgument(heapReference);
			instanceOfReference.addArgument(ValueReference.of(name));
			instanceOfReference.addArgument(typeReference);
			bindingBuilder.whereClause(instanceOfReference);
		}

		return bindingBuilder.build();
	}

	public static BoundedBinding makeBinding(final Local local, final String name) {
		final Type type = local.getType();

		return makeBinding(name, type);
	}

	public static BoundedBinding makeBinding(final Local local) {
		final String name = PureExpressionExtractor.localName(local);

		return makeBinding(local, name);
	}

	public static Iterable<Local> getParameterLocals(final SootMethod method) {
		if (method.hasActiveBody()) {
			return SootBodies.getParameterLocals(method.getActiveBody());
		} else {
			return SootMethods.makeFakeParameterLocals(method);
		}
	}

	public static void buildReturnParameter(final ProcedureSignatureBuilder builder, final SootMethod method) {
		final TypeAccess typeAccess = new TypeAccessExtractor().visit(method.getReturnType());
		final BoundedBinding binding = Convention.makeReturnBinding(typeAccess);
		builder.addOutputBinding(binding);
	}

	public static void buildExceptionParameter(final ProcedureSignatureBuilder builder, final SootMethod method) {
		final TypeAccess typeAccess = new TypeAccessExtractor().visit(RefType.v());
		final BoundedBinding binding = Convention.makeExceptionBinding(typeAccess);
		builder.addOutputBinding(binding);
	}

	public static void buildSignature(final ProcedureDeclarationBuilder builder, final SootMethod method) {
		final var signatureBuilder = new ProcedureSignatureBuilder();

		for (Local local : getParameterLocals(method)) {
			final String parameterName = parameterName(local);
			signatureBuilder.addInputBinding(makeBinding(local, parameterName));
		}

		if (method.getReturnType() != VoidType.v()) {
			buildReturnParameter(signatureBuilder, method);
		}

		buildExceptionParameter(signatureBuilder, method);
		builder.signature(signatureBuilder.build());
	}

	public static void buildSpecification(final ProcedureDeclarationBuilder builder, final SootMethod method) {
		SootHosts.getAnnotations(method).forEach((tag) -> {
			SootAnnotations.getAnnotations(tag).forEach((subTag) -> {
				final Specification specification;

				switch (subTag.getType()) {
					case Namespace.REQUIRE_ANNOTATION :
						specification = new RequireConverter(method).convert(subTag);
						break;
					case Namespace.ENSURE_ANNOTATION :
						specification = new EnsureConverter(method).convert(subTag);
						break;
					case Namespace.RAISE_ANNOTATION :
						specification = new RaiseConverter(method).convert(subTag);
						break;
					case Namespace.RETURN_ANNOTATION :
						specification = new ReturnConverter(method).convert(subTag);
						break;
					default :
						return;
				}

				builder.addSpecification(specification);
			});
		});

		buildDefaultHeapInvariant(builder);
	}

	public static void buildDefaultHeapInvariant(final ProcedureDeclarationBuilder builder) {
		final Expression expression = Prelude.v().makeDefaultHeapInvariant();
		builder.addSpecification(new PostCondition(new List<>(), true, expression));
	}

	public static void buildBody(final ProcedureDeclarationBuilder builder, final SootMethod method) {
		final var bodyExtractor = new ProcedureBodyExtractor();
		final Body body = bodyExtractor.visit(method.getActiveBody());

		for (Local local : SootBodies.getLocals(method.getActiveBody())) {
			final var variableBuilder = new VariableDeclarationBuilder();
			body.addLocalDeclaration(variableBuilder.addBinding(makeBinding(local)).build());
		}

		builder.body(body);
	}

	public static void buildFrameInvariant(final ProcedureDeclarationBuilder builder) {
		builder.addSpecification(Prelude.v().makeHeapFrameCondition());
	}

	public ProcedureDeclaration convert(final SootMethod method) {
		final var builder = new ProcedureDeclarationBuilder();

		try {
			builder.name(methodName(method));
			buildSignature(builder, method);
			buildSpecification(builder, method);

			if (SootMethods.hasBody(method)) {
				if (!SootHosts.hasAnnotation(method, Namespace.LEMMA_ANNOTATION)) {
					buildBody(builder, method);
				}
			}

			if (!SootHosts.hasAnnotation(method, Namespace.INVARIANT_ANNOTATION)) {
				buildFrameInvariant(builder);
			}
		} catch (final ConversionException exception) {
			throw new ProcedureConversionException(method, exception);
		}

		return builder.build();
	}

}
