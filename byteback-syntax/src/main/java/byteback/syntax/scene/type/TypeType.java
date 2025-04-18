package byteback.syntax.scene.type;

import byteback.common.function.Lazy;
import soot.Type;

/**
 * A type for a type value.
 *
 * @author paganma
 */
public class TypeType extends Type implements DefaultCaseType {

	private static final Lazy<TypeType> INSTANCE = Lazy.from(TypeType::new);

	public static TypeType v() {
		return INSTANCE.get();
	}

	private TypeType() {
	}

	@Override
	public String toString() {
		return "Type";
	}

}
