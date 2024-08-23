package byteback.syntax.scene.type.declaration.member.method.reference;

import byteback.syntax.scene.type.BoxType;
import soot.Type;

public class UnboxRef extends ExternalRef {

	public UnboxRef(final Type contentType) {
		super("unbox", new BoxType(contentType), new Type[] { contentType });
	}

}
