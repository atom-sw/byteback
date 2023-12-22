package byteback.analysis.vimp;

import byteback.analysis.JimpleValueSwitch;
import byteback.util.Lazy;
import soot.Type;
import soot.VoidType;
import soot.jimple.Constant;
import soot.util.Switch;

public class VoidConstant extends Constant {

	private static final Lazy<VoidConstant> instance = Lazy.from(VoidConstant::new);

	public static VoidConstant v() {
		return instance.get();
	}

	private VoidConstant() {
	}

	@Override
	public Type getType() {
		return VoidType.v();
	}

	@Override
	public void apply(Switch sw) {
		if (sw instanceof JimpleValueSwitch<?> vs) {
			vs.caseVoidConstant(this);
		}
	}

	@Override
	public String toString() {
		return "voidc";
	}

}
