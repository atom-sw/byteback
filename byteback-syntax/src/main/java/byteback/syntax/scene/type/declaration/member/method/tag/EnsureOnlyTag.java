package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a methods specifying behavior expressions.
 *
 * @author paganma
 */
public class EnsureOnlyTag implements Tag {

	public static String NAME = "EnsureOnlyTag";

	private static final Lazy<EnsureOnlyTag> INSTANCE = Lazy.from(EnsureOnlyTag::new);

	public static EnsureOnlyTag v() {
		return INSTANCE.get();
	}

	private EnsureOnlyTag() {
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
