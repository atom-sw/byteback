package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.*;

public class OrderSpecificationBuilder {

	private List<ParentEdge> parentEdges;

	private boolean complete;

	public OrderSpecificationBuilder() {
		this.parentEdges = new List<>();
	}

	public OrderSpecificationBuilder addParentEdge(final ParentEdge parentEdge) {
		this.parentEdges.add(parentEdge);

		return this;
	}

	public OrderSpecificationBuilder parentEdges(final List<ParentEdge> parentEdges) {
		this.parentEdges = parentEdges;

		return this;
	}

	public OrderSpecification build() {
		if (parentEdges.getNumChild() == 0) {
			throw new IllegalArgumentException("Order specification must declare at least one parent edge");
		}

		return new OrderSpecification(parentEdges, complete);
	}

}
