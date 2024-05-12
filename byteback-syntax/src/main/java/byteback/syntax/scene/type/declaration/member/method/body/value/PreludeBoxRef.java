package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.BoxType;
import byteback.syntax.scene.type.HeapType;
import soot.RefType;
import soot.Type;

public class PreludeBoxRef extends PreludeRef {

	public PreludeBoxRef(final BoxType boxType) {
		super("box", boxType, new Type[] { HeapType.v(), RefType.v(), boxType.getContentType() });
	}

}
