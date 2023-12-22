package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.*;

public class FunctionSignatureBuilder extends SignatureBuilder {

	private List<OptionalBinding> inputBindings;

	private OptionalBinding outputBinding;

	public FunctionSignatureBuilder() {
		this.inputBindings = new List<>();
	}

	public FunctionSignatureBuilder addInputBinding(final OptionalBinding inputBinding) {
		this.inputBindings.add(inputBinding);

		return this;
	}

	public FunctionSignatureBuilder outputBinding(final OptionalBinding outputBinding) {
		this.outputBinding = outputBinding;

		return this;
	}

	public FunctionSignature build() {
		if (outputBinding == null) {
			throw new IllegalArgumentException("A function signature must define an output binding");
		}

		return new FunctionSignature(typeParameters, inputBindings, outputBinding);
	}

}
