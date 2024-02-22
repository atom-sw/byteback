package byteback.analysis.body.vimp;

import byteback.analysis.body.vimp.visitor.SpecialExprVisitor;
import soot.jimple.ExprSwitch;

public interface SpecialExprSwitch<T> extends ExprSwitch, SpecialExprVisitor<T> {
}
