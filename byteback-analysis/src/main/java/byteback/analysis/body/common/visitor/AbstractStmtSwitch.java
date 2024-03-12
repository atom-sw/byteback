package byteback.analysis.body.common.visitor;

import byteback.analysis.common.visitor.Visitor;
import soot.jimple.Stmt;

public abstract class AbstractStmtSwitch<R> extends soot.jimple.AbstractStmtSwitch<R> implements Visitor<Stmt, R> {

    @Override
    public void defaultCase(final Object object) {
        defaultCase((Stmt) object);
    }

}
