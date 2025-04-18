package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a methods specifying behavior expressions.
 *
 * @author paganma
 */
public class RequireOnlyTag implements Tag {

	public static String NAME = "RequireOnlyTag";

	private static final Lazy<RequireOnlyTag> INSTANCE = Lazy.from(RequireOnlyTag::new);

	public static RequireOnlyTag v() {
		return INSTANCE.get();
	}

	private RequireOnlyTag() {
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
