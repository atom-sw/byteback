package byteback.specification.plugin;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;

@Plugin.Attach("soot.dummy.InvokeDynamic")
public abstract class InvokeDynamicSpec {

	@Abstract
	@Return
	public static String makeConcatWithConstants(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Abstract
	@Return
	public static String makeConcatWithConstants(int index) {
		throw new UnsupportedOperationException();
	}

}
