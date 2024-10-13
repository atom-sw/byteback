package byteback.syntax.scene.type.declaration.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a methods specifying behavior expressions.
 *
 * @author paganma
 */
public class InvariantOnlyTag implements Tag {

	public static String NAME = "InvariantOnlyTag";

	private static final Lazy<InvariantOnlyTag> INSTANCE = Lazy.from(InvariantOnlyTag::new);

	public static InvariantOnlyTag v() {
		return INSTANCE.get();
	}

	private InvariantOnlyTag() {
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
