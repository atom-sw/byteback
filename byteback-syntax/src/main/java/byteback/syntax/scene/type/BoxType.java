package byteback.syntax.scene.type;

import soot.Type;

/**
 * A generic Box type representing a modifiable area of memory. This
 * is mostly used to model array elements.
 *
 * @author paganma
 */
public class BoxType extends Type implements DefaultCaseType {

	public final Type contentType;

	public BoxType(final Type contentType) {
		this.contentType = contentType;
	}

	public Type getContentType() {
		return contentType;
	}

	@Override
	public String toString() {
		return "Box<" + contentType.toString() + ">";
	}

}
