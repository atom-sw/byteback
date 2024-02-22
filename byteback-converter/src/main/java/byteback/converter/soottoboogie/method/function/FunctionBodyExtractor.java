package byteback.converter.soottoboogie.method.function;

import byteback.analysis.body.vimp.visitor.AbstractVimpStmtSwitch;
import byteback.converter.soottoboogie.method.StatementConversionException;
import byteback.frontend.boogie.ast.Expression;
import soot.Value;
import soot.jimple.*;

public class FunctionBodyExtractor extends AbstractVimpStmtSwitch<Expression> {

	private Expression result;

	@Override
	public void caseIdentityStmt(final IdentityStmt identity) {
	}

	@Override
	public void caseReturnStmt(final ReturnStmt returnStatement) {
		final Value operand = returnStatement.getOp();
		result = new FunctionExpressionExtractor().visit(operand);
	}

	@Override
	public void defaultCase(final Stmt s) {
		throw new StatementConversionException(s, "Unable to convert statement " + s);
	}

	@Override
	public Expression getResult() {
		return result;
	}

}
