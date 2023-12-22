package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.*;

public class SignatureBuilder extends DeclarationBuilder {

	final protected List<TypeParameter> typeParameters;

	public SignatureBuilder() {
		this.typeParameters = new List<>();
	}

	public SignatureBuilder addTypeParameter(final TypeParameter typeParameter) {
		this.typeParameters.add(typeParameter);

		return this;
	}

}
