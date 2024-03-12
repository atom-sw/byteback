package byteback.analysis.body.vimp.visitor;

import byteback.analysis.body.vimp.syntax.OldExpr;
import byteback.analysis.body.vimp.syntax.VoidConstant;
import byteback.analysis.common.visitor.Visitor;
import soot.Value;

public interface SpecialExprSwitch<T> extends Visitor<Value, T> {

    default void caseVoidConstant(final VoidConstant v) {
        defaultCase(v);
    }

    default void caseOldExpr(final OldExpr v) {
        defaultCase(v);
    }

}
