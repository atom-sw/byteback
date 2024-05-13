package byteback.syntax.scene.type.declaration.member.method.body.unit.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a branch associated with an exception handler.
 *
 * @author paganma
 */
public class ThrowTargetTag implements Tag {

	public static String NAME = "ThrowTargetTag";

	private static final Lazy<ThrowTargetTag> INSTANCE = Lazy.from(ThrowTargetTag::new);

	public static ThrowTargetTag v() {
		return INSTANCE.get();
	}

	private ThrowTargetTag() {
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
