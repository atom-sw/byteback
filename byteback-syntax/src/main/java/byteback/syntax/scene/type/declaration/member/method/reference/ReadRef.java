package byteback.syntax.scene.type.declaration.member.method.reference;

import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.PointerType;
import soot.RefType;
import soot.Type;

public class ReadRef extends ExternalRef {

	public ReadRef(final PointerType pointerType) {
		super("store.read", pointerType.getPointedType(), new Type[] { HeapType.v(), RefType.v(), pointerType });
	}

}
