package byteback.syntax.scene.type;

import soot.Type;

/**
 * A generic Box type representing a modifiable area of memory. This
 * is mostly used to model array elements.
 *
 * @author paganma
 */
public class BoxType extends ParametricType implements DefaultCaseType {

	public BoxType(final Type contentType) {
		super(new Type[] { contentType });
	}

	public Type getContentType() {
		return typeParameters[0];
	}

	@Override
	public String getConstructorName() {
		return "Box";
	}

}
