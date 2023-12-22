package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.*;

public class FunctionDeclarationBuilder extends DeclarationBuilder {

	private Declarator declarator;

	private FunctionSignature signature;

	private Opt<Expression> expression;

	public FunctionDeclarationBuilder() {
		this.expression = new Opt<>();
	}

	public FunctionDeclarationBuilder name(final String name) {
		this.declarator = new Declarator(name);

		return this;
	}

	public FunctionDeclarationBuilder signature(final FunctionSignature signature) {
		this.signature = signature;

		return this;
	}

	public FunctionDeclarationBuilder expression(final Expression expression) {
		this.expression = new Opt<>(expression);

		return this;
	}

	@Override
	public FunctionDeclarationBuilder addAttribute(final Attribute attribute) {
		super.addAttribute(attribute);

		return this;
	}

	public FunctionDeclaration build() {
		if (declarator == null) {
			throw new IllegalArgumentException("Function declaration must include a name");
		}

		if (signature == null) {
			throw new IllegalArgumentException("Function declaration must include a signature");
		}

		return new FunctionDeclaration(attributes, declarator, signature, expression);
	}

}
