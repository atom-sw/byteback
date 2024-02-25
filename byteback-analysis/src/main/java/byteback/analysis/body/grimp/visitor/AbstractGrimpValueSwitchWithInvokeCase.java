package byteback.analysis.body.grimp.visitor;

import byteback.analysis.common.visitor.Visitor;
import soot.Value;
import soot.jimple.*;

public class AbstractGrimpValueSwitchWithInvokeCase<T> extends AbstractGrimpValueSwitch<T>
        implements Visitor<Value, T> {

    @Override
    public void caseStaticInvokeExpr(final StaticInvokeExpr v) {
        caseInvokeExpr(v);
    }

    @Override
    public void caseVirtualInvokeExpr(final VirtualInvokeExpr v) {
        caseInvokeExpr(v);
    }

    @Override
    public void caseInterfaceInvokeExpr(final InterfaceInvokeExpr v) {
        caseInvokeExpr(v);
    }

    @Override
    public void caseDynamicInvokeExpr(final DynamicInvokeExpr v) {
        caseInvokeExpr(v);
    }

    @Override
    public void caseSpecialInvokeExpr(final SpecialInvokeExpr v) {
        caseInvokeExpr(v);
    }

    public void caseInvokeExpr(final InvokeExpr v) {
        defaultCase(v);
    }

}
