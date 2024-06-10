package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.TypeType;
import soot.RefType;
import soot.Type;

public class PreludeNewRef extends PreludeRef {

	private static final Lazy<PreludeNewRef> INSTANCE = Lazy.from(PreludeNewRef::new);

	public static PreludeNewRef v() {
		return INSTANCE.get();
	}

	public PreludeNewRef() {
		super("new", RefType.v(), new Type[] { TypeType.v() });
	}

}
