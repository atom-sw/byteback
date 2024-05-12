package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.BoxType;
import soot.Type;

public class PreludeUnboxRef extends PreludeRef {

	public PreludeUnboxRef(final Type contentType) {
		super("unbox", new BoxType(contentType), new Type[] { contentType });
	}

}
