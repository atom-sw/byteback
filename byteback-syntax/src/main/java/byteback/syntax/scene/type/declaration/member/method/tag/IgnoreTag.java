package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a methods specifying behavior expressions.
 *
 * @author paganma
 */
public class IgnoreTag implements Tag {

	public static String NAME = "IgnoreTag";

	private static final Lazy<IgnoreTag> INSTANCE = Lazy.from(IgnoreTag::new);

	public static IgnoreTag v() {
		return INSTANCE.get();
	}

	private IgnoreTag() {
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
