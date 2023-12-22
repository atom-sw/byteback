package byteback.converter.soottoboogie.method.function;

import byteback.analysis.TypeSwitch;
import byteback.analysis.util.SootBodies;
import byteback.converter.soottoboogie.ConversionException;
import byteback.converter.soottoboogie.Prelude;
import byteback.converter.soottoboogie.expression.PureExpressionExtractor;
import byteback.converter.soottoboogie.method.MethodConverter;
import byteback.converter.soottoboogie.type.TypeAccessExtractor;
import byteback.frontend.boogie.ast.FunctionDeclaration;
import byteback.frontend.boogie.ast.OptionalBinding;
import byteback.frontend.boogie.ast.TypeAccess;
import byteback.frontend.boogie.builder.FunctionDeclarationBuilder;
import byteback.frontend.boogie.builder.FunctionSignatureBuilder;
import byteback.frontend.boogie.builder.OptionalBindingBuilder;
import byteback.util.Lazy;
import soot.Local;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;

public class FunctionConverter extends MethodConverter {

	private static final Lazy<FunctionConverter> instance = Lazy.from(FunctionConverter::new);

	public static FunctionConverter v() {
		return instance.get();
	}

	public static OptionalBinding makeBinding(final Local local) {
		final Type type = local.getType();
		final TypeAccess typeAccess = new TypeAccessExtractor().visit(type);
		final OptionalBindingBuilder bindingBuilder = new OptionalBindingBuilder();
		bindingBuilder.name(PureExpressionExtractor.localName(local)).typeAccess(typeAccess);

		return bindingBuilder.build();
	}

	public static void buildSignature(final FunctionDeclarationBuilder functionBuilder, final SootMethod method) {
		final var signatureBuilder = new FunctionSignatureBuilder();
		signatureBuilder.addInputBinding(Prelude.v().getHeapVariable().makeOptionalBinding());

		for (Local local : SootBodies.getParameterLocals(method.retrieveActiveBody())) {
			signatureBuilder.addInputBinding(makeBinding(local));
		}

		method.getReturnType().apply(new TypeSwitch<>() {

			@Override
			public void caseVoidType(final VoidType type) {
				throw new ConversionException("A pure function cannot be void");
			}

			@Override
			public void caseDefault(final Type type) {
				final TypeAccess boogieTypeAccess = new TypeAccessExtractor().visit(method.getReturnType());
				final OptionalBinding boogieBinding = new OptionalBindingBuilder().typeAccess(boogieTypeAccess).build();
				signatureBuilder.outputBinding(boogieBinding);
			}

		});

		functionBuilder.signature(signatureBuilder.build());
	}

	public static void buildExpression(final FunctionDeclarationBuilder functionBuilder, final SootMethod method) {
		functionBuilder.expression(new FunctionBodyExtractor().visit(method.retrieveActiveBody()));
	}

	public FunctionDeclaration convert(final SootMethod method) {
		final var builder = new FunctionDeclarationBuilder();

		try {
			builder.name(methodName(method));
			buildSignature(builder, method);
			buildExpression(builder, method);
		} catch (final ConversionException exception) {
			throw new FunctionConversionException(method, exception);
		}

		return builder.build();
	}

}
