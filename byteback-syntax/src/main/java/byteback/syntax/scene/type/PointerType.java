package byteback.syntax.scene.type;

import soot.Type;

/**
 * A type for a pointer (heap reference).
 *
 * @author paganma
 */
public class PointerType extends ParametricType {

	public PointerType(final Type pointedType) {
		super(new Type[] { pointedType });
	}

	public Type getPointedType() {
		return typeParameters[0];
	}

	@Override
	public String getConstructorName() {
		return "Pointer";
	}

}
