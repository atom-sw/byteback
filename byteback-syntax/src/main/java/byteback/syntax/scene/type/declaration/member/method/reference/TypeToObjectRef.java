package byteback.syntax.scene.type.declaration.member.method.reference;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.TypeType;
import soot.RefType;
import soot.Type;

public class TypeToObjectRef extends ExternalRef {

	private static final Lazy<TypeToObjectRef> INSTANCE = Lazy.from(TypeToObjectRef::new);

	public static TypeToObjectRef v() {
		return INSTANCE.get();
	}

	private TypeToObjectRef() {
		super("type.reference", RefType.v(), new Type[] { TypeType.v() });
	}

}
