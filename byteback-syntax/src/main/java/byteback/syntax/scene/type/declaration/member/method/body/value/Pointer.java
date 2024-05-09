package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.PointerType;
import soot.Immediate;

public interface Pointer extends Immediate {

	@Override
	PointerType getType();

}
