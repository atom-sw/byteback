package byteback.syntax.scene.type;

import soot.Type;

/**
 * A type for a heap reference.
 *
 * @author paganma
 */
public class PointerType extends Type implements DefaultCaseType {

	public final Type pointedType;

	public PointerType(final Type pointedType) {
		this.pointedType = pointedType;
	}

	public Type getPointedType() {
		return pointedType;
	}

	@Override
	public String toString() {
		return "Pointer<" + pointedType.toString() + ">";
	}

}
