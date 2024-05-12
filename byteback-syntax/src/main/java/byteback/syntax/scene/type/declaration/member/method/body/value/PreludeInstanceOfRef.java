package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.TypeType;
import soot.BooleanType;
import soot.RefType;
import soot.Type;

public class PreludeInstanceOfRef extends PreludeRef {

	private static final Lazy<PreludeInstanceOfRef> INSTANCE = Lazy.from(PreludeInstanceOfRef::new);

	public static PreludeInstanceOfRef v() {
		return INSTANCE.get();
	}

	private PreludeInstanceOfRef() {
		super("reference.instanceof", BooleanType.v(), new Type[] { HeapType.v(), RefType.v(), TypeType.v() });
	}

}
