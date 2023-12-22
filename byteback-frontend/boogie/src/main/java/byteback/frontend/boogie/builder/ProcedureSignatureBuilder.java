package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.*;

public class ProcedureSignatureBuilder extends SignatureBuilder {

	private List<BoundedBinding> inputBindings;

	private List<BoundedBinding> outputBindings;

	public ProcedureSignatureBuilder() {
		this.inputBindings = new List<>();
		this.outputBindings = new List<>();
	}

	public ProcedureSignatureBuilder addInputBinding(final BoundedBinding inputBinding) {
		this.inputBindings.add(inputBinding);

		return this;
	}

	public ProcedureSignatureBuilder addOutputBinding(final BoundedBinding outputBinding) {
		this.outputBindings.add(outputBinding);

		return this;
	}

	public ProcedureSignature build() {
		return new ProcedureSignature(typeParameters, inputBindings, outputBindings);
	}

}
