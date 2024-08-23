package byteback.syntax.scene.type.declaration.member.method.reference;

import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.PointerType;
import soot.RefType;
import soot.Type;

public class UpdateRef extends ExternalRef {

	public UpdateRef(final PointerType pointerType) {
		super("store.update", HeapType.v(),
				new Type[] { HeapType.v(), RefType.v(), pointerType, pointerType.getPointedType() });
	}

}
