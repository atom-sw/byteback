package byteback.syntax.scene.type.declaration.member.method.reference;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.HeapType;
import byteback.syntax.scene.type.TypeType;
import soot.BooleanType;
import soot.RefType;
import soot.Type;

public class InstanceOfRef extends ExternalRef {

	private static final Lazy<InstanceOfRef> INSTANCE = Lazy.from(InstanceOfRef::new);

	public static InstanceOfRef v() {
		return INSTANCE.get();
	}

	private InstanceOfRef() {
		super("reference.instanceof", BooleanType.v(), new Type[] { HeapType.v(), RefType.v(), TypeType.v() });
	}

}
