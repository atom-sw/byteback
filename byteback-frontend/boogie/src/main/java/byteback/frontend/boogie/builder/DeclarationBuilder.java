package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.Attribute;
import byteback.frontend.boogie.ast.List;

public abstract class DeclarationBuilder {

	final protected List<Attribute> attributes;

	public DeclarationBuilder() {
		this.attributes = new List<>();
	}

	public DeclarationBuilder addAttribute(final Attribute attribute) {
		this.attributes.add(attribute);

		return this;
	}

	public DeclarationBuilder addAttributes(final Iterable<Attribute> attributes) {
		this.attributes.addAll(attributes);

		return this;
	}

}
