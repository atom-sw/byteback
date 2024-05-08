package byteback.specification.plugin;

import byteback.specification.Contract.Return;

@Plugin.Attach("soot.dummy.InvokeDynamic")
public abstract class InvokeDynamicSpec {

	@Return
	public static String makeConcatWithConstants(int fromIndex, int toIndex) {
		return null;
	}

	@Return
	public static String makeConcatWithConstants(int index) {
		return null;
	}

}
