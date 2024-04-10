package byteback.specification.plugin;

import byteback.specification.Contract.Attach;
import byteback.specification.Contract.Lemma;
import byteback.specification.Contract.Return;

@Attach("soot.dummy.InvokeDynamic")
public abstract class InvokeDynamicSpec {

	@Lemma
	@Return
	public static String makeConcatWithConstants(int fromIndex, int toIndex) {
		return "";
	}

	@Lemma
	@Return
	public static String makeConcatWithConstants(int index) {
		return "";
	}

}
