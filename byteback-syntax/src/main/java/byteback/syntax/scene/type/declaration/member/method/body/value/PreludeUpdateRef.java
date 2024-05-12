package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.PointerType;
import soot.RefType;
import soot.Type;

public class PreludeUpdateRef extends PreludeRef {

	public PreludeUpdateRef(final PointerType pointerType) {
		super("store.update", HeapType.v(),
				new Type[] { HeapType.v(), RefType.v(), pointerType, pointerType.getPointedType() });
	}

}
