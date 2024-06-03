package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag marking methods accessing the two-state.
 *
 * @author paganma
 */
public class TwoStateTag implements Tag {

	public static String NAME = "TwoStateTag";

	private static final Lazy<TwoStateTag> instance = Lazy.from(TwoStateTag::new);

	public static TwoStateTag v() {
		return instance.get();
	}

	private TwoStateTag() {
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
