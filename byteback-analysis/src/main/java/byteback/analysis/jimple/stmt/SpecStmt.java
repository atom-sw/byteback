package byteback.analysis.jimple.stmt;

import byteback.analysis.jimple.expr.SpecExpr;
import sootup.core.jimple.basic.StmtPositionInfo;
import sootup.core.jimple.common.stmt.Stmt;

public abstract class SpecStmt extends Stmt {

	private final SpecExpr specification;

	public SpecStmt(final SpecExpr specification, final StmtPositionInfo positionInfo) {
		super(positionInfo);
		this.specification = specification;
	}

	public SpecExpr getSpecification() {
		return specification;
	}

	@Override
	public boolean branches() {
		return false;
	}

	@Override
	public boolean fallsThrough() {
		return true;
	}

}
