package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.TypeType;
import soot.BooleanType;
import soot.RefType;
import soot.Type;

public class PreludeWeakInstanceOfRef extends PreludeRef {

	private static final Lazy<PreludeWeakInstanceOfRef> INSTANCE = Lazy.from(PreludeWeakInstanceOfRef::new);

	public static PreludeWeakInstanceOfRef v() {
		return INSTANCE.get();
	}

	private PreludeWeakInstanceOfRef() {
		super("reference.compatible", BooleanType.v(), new Type[] { HeapType.v(), RefType.v(), TypeType.v() });
	}

}
