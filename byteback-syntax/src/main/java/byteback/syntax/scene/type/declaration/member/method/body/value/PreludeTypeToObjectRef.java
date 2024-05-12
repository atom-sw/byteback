package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.TypeType;
import soot.RefType;
import soot.Type;

public class PreludeTypeToObjectRef extends PreludeRef {

	private static final Lazy<PreludeTypeToObjectRef> INSTANCE = Lazy.from(PreludeTypeToObjectRef::new);

	public static PreludeTypeToObjectRef v() {
		return INSTANCE.get();
	}

	private PreludeTypeToObjectRef() {
		super("type.reference", RefType.v(), new Type[] { TypeType.v() });
	}

}
