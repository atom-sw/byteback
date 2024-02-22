package byteback.analysis.body.grimp.visitor;

import byteback.analysis.common.visitor.Visitor;
import soot.Value;
import soot.jimple.*;

public interface WithInvokeCase<T> extends Visitor<Value, T> {

    default void caseStaticInvokeExpr(final StaticInvokeExpr v) {
        caseInvokeExpr(v);
    }

    default void caseVirtualInvokeExpr(final VirtualInvokeExpr v) {
        caseInvokeExpr(v);
    }

    default void caseInterfaceInvokeExpr(final InterfaceInvokeExpr v) {
        caseInvokeExpr(v);
    }

    default void caseDynamicInvokeExpr(final DynamicInvokeExpr v) {
        caseInvokeExpr(v);
    }

    default void caseSpecialInvokeExpr(final SpecialInvokeExpr v) {
        caseInvokeExpr(v);
    }

    default void caseInvokeExpr(final InvokeExpr v) {
        defaultCase(v);
    }

}
