package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class NoStateTag implements Tag {

	public static final String NAME = "OperatorTag";

	private static final Lazy<NoStateTag> INSTANCE = Lazy.from(NoStateTag::new);

	public static NoStateTag v() {
		return INSTANCE.get();
	}

	private NoStateTag() {
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
