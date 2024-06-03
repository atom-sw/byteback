package byteback.syntax.scene.type.declaration.member.method.body.value.box.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class FreeTag implements Tag {

	public static String NAME = "FreeTag";

	private static final Lazy<FreeTag> INSTANCE = Lazy.from(FreeTag::new);

	public static FreeTag v() {
		return INSTANCE.get();
	}

	private FreeTag() {
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
