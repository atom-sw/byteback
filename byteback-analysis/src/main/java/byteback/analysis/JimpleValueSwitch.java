package byteback.analysis;

import byteback.analysis.vimp.LogicAndExpr;
import byteback.analysis.vimp.LogicOrExpr;
import byteback.analysis.vimp.LogicXorExpr;
import byteback.analysis.vimp.OldExpr;
import byteback.analysis.vimp.VoidConstant;
import soot.Value;
import soot.jimple.*;

public abstract class JimpleValueSwitch<R> extends AbstractJimpleValueSwitch<R> implements LogicExprSwitch<R> {

	@Override
	public void caseLogicAndExpr(final LogicAndExpr v) {
		caseAndExpr(v);
	}

	@Override
	public void caseLogicOrExpr(final LogicOrExpr v) {
		caseOrExpr(v);
	}

	@Override
	public void caseLogicXorExpr(final LogicXorExpr v) {
		caseXorExpr(v);
	}

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

	public void caseVoidConstant(final VoidConstant v) {
		defaultCase(v);
	}

	public void caseOldExpr(final OldExpr v) {
		defaultCase(v);
	}

	@Override
	public void defaultCase(final Object v) {
		caseDefault((Value) v);
	}

}
