package byteback.specification;

import byteback.specification.Contract.AttachLabel;
import byteback.specification.Contract.Lemma;
import byteback.specification.Contract.Return;

@AttachLabel("Lsoot/dummy/InvokeDynamic;")
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
