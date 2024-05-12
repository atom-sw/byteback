package byteback.syntax.scene.type;

import soot.Type;

/**
 * A type for a heap reference.
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
