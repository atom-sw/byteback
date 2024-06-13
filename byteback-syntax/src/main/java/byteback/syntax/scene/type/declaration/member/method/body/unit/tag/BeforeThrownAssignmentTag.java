package byteback.syntax.scene.type.declaration.member.method.body.unit.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * Tag used to annotate a branch associated with an exception handler.
 *
 * @author paganma
 */
public class BeforeThrownAssignmentTag implements Tag {

	public static String NAME = "BeforeThrownAssignmentTag";

	private static final Lazy<BeforeThrownAssignmentTag> INSTANCE = Lazy.from(BeforeThrownAssignmentTag::new);

	public static BeforeThrownAssignmentTag v() {
		return INSTANCE.get();
	}

	private BeforeThrownAssignmentTag() {
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
