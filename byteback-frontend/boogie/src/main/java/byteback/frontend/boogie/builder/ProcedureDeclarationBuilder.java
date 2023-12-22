package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.*;

public class ProcedureDeclarationBuilder extends DeclarationBuilder {

	private Declarator declarator;

	private ProcedureSignature signature;

	private Opt<Body> body;

	private List<Specification> specifications;

	public ProcedureDeclarationBuilder() {
		body = new Opt<>();
		specifications = new List<>();
	}

	public ProcedureDeclarationBuilder name(final String name) {
		this.declarator = new Declarator(name);

		return this;
	}

	public ProcedureDeclarationBuilder signature(final ProcedureSignature signature) {
		this.signature = signature;

		return this;
	}

	public ProcedureDeclarationBuilder addSpecification(final Specification specification) {
		this.specifications.add(specification);

		return this;
	}

	public ProcedureDeclarationBuilder addSpecifications(final Iterable<Specification> specification) {
		this.specifications.addAll(specification);

		return this;
	}

	public ProcedureDeclarationBuilder body(final Body body) {
		this.body = new Opt<>(body);

		return this;
	}

	public ProcedureDeclaration build() {
		if (declarator == null) {
			throw new IllegalArgumentException("Procedure declaration must include a name");
		}

		if (signature == null) {
			throw new IllegalArgumentException("Procedure declaration must include a signature");
		}

		return new ProcedureDeclaration(attributes, declarator, signature, specifications, body);
	}

}
