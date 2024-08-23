package byteback.syntax.scene.type.declaration.member.method.reference;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.TypeType;
import soot.RefType;
import soot.Type;

public class NewRef extends ExternalRef {

	private static final Lazy<NewRef> INSTANCE = Lazy.from(NewRef::new);

	public static NewRef v() {
		return INSTANCE.get();
	}

	public NewRef() {
		super("new", RefType.v(), new Type[] { TypeType.v() });
	}

}
