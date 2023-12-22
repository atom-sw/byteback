package byteback.frontend.boogie.builder;

import byteback.frontend.boogie.ast.BlockStatement;
import byteback.frontend.boogie.ast.IfStatement;
import byteback.frontend.boogie.ast.List;
import byteback.frontend.boogie.ast.Meta;
import byteback.frontend.boogie.ast.Opt;
import byteback.frontend.boogie.ast.Statement;

public class IfStatementBuilder {

	protected Meta condition;

	protected BlockStatement thenBlock;

	protected Opt<BlockStatement> elseBlock;

	public IfStatementBuilder() {
		this.thenBlock = new BlockStatement();
		this.elseBlock = new Opt<>();
	}

	public IfStatementBuilder condition(final Meta condition) {
		this.condition = condition;

		return this;
	}

	public IfStatementBuilder thenBlock(final BlockStatement thenBlock) {
		this.thenBlock = thenBlock;

		return this;
	}

	public IfStatementBuilder thenStatement(final Statement statement) {
		this.thenBlock = new BlockStatement(new List<>(statement));

		return this;
	}

	public IfStatementBuilder elseBlock(final BlockStatement elseBlock) {
		this.elseBlock = new Opt<>(elseBlock);

		return this;
	}

	public IfStatement build() {
		if (condition == null) {
			throw new IllegalArgumentException("IF statements must include a condition");
		}

		if (thenBlock == null) {
			throw new IllegalArgumentException("IF statements must include a THEN block");
		}

		return new IfStatement(condition, thenBlock, elseBlock);
	}

}
