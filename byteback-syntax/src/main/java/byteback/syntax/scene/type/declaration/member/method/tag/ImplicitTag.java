package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag marking methods accessing the two-state.
 *
 * @author paganma
 */
public class ImplicitTag implements Tag {

	public static String NAME = "ImplicitTag";

	private static final Lazy<ImplicitTag> instance = Lazy.from(ImplicitTag::new);

	public static ImplicitTag v() {
		return instance.get();
	}

	private ImplicitTag() {
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public byte[] getValue() throws AttributeValueException {
		return new byte[0];
	}

}
