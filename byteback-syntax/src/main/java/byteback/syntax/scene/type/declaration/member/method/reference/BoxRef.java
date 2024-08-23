package byteback.syntax.scene.type.declaration.member.method.reference;

import byteback.syntax.scene.type.BoxType;
import byteback.syntax.scene.type.HeapType;
import soot.RefType;
import soot.Type;

public class BoxRef extends ExternalRef {

	public BoxRef(final BoxType boxType) {
		super("box", boxType, new Type[] { HeapType.v(), RefType.v(), boxType.getContentType() });
	}

}
