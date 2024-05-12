package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.PointerType;
import soot.RefType;
import soot.Type;

public class PreludeReadRef extends PreludeRef {

	public PreludeReadRef(final PointerType pointerType) {
		super("store.read", pointerType.getPointedType(), new Type[] { HeapType.v(), RefType.v(), pointerType });
	}

}
