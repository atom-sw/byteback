package byteback.annotations;

import byteback.annotations.Contract.AttachLabel;
import byteback.annotations.Contract.Lemma;
import byteback.annotations.Contract.Return;

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
