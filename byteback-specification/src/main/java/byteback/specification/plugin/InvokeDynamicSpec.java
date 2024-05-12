package byteback.specification.plugin;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;

@Plugin.Attach("soot.dummy.InvokeDynamic")
public abstract class InvokeDynamicSpec {

	@Abstract
	public InvokeDynamicSpec() {
	}

	@Return
	@Abstract
	public static String makeConcatWithConstants(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Return
	@Abstract
	public static String makeConcatWithConstants(int index) {
		throw new UnsupportedOperationException();
	}

}
