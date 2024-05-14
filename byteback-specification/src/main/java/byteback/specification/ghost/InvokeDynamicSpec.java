package byteback.specification.ghost;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;
import byteback.specification.ghost.Ghost.Export;

@Attach("soot.dummy.InvokeDynamic")
public abstract class InvokeDynamicSpec {

	@Abstract
	public InvokeDynamicSpec() {
	}

	@Return
	@Export
	@Abstract
	public static String makeConcatWithConstants(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Return
	@Export
	@Abstract
	public static String makeConcatWithConstants(int index) {
		throw new UnsupportedOperationException();
	}

}
