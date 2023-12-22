package byteback.analysis;

import soot.jimple.ExprSwitch;

public interface LogicExprSwitch<T> extends ExprSwitch, LogicExprVisitor<T> {

}
