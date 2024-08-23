package byteback.syntax.scene.type.declaration.member.method.reference;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.TypeType;
import soot.BooleanType;
import soot.RefType;
import soot.Type;

public class CompatibleRef extends ExternalRef {

	private static final Lazy<CompatibleRef> INSTANCE = Lazy.from(CompatibleRef::new);

	public static CompatibleRef v() {
		return INSTANCE.get();
	}

	private CompatibleRef() {
		super("reference.compatible", BooleanType.v(), new Type[] { HeapType.v(), RefType.v(), TypeType.v() });
	}

}
