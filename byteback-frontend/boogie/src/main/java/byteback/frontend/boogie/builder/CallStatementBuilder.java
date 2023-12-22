package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.Accessor;

class CallStatementBuilder {

	protected Accessor accessor;

	public CallStatementBuilder name(final String name) {
		this.accessor = new Accessor(name);

		return this;
	}

}
