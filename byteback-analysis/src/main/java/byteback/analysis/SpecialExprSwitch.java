package byteback.analysis;

import soot.jimple.ExprSwitch;

public interface SpecialExprSwitch<T> extends ExprSwitch, SpecialExprVisitor<T> {

}
