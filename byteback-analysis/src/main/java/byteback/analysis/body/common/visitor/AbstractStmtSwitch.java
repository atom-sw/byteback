package byteback.analysis.body.common.visitor;

import byteback.analysis.common.visitor.Visitor;
import byteback.analysis.body.jimple.syntax.Stmt;

public abstract class AbstractStmtSwitch<R> extends byteback.analysis.body.jimple.syntax.AbstractStmtSwitch<R> implements Visitor<Stmt, R> {

    @Override
    public void defaultCase(final Object object) {
        defaultCase((Stmt) object);
    }

}
