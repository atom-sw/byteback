package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class ExportTag implements Tag {

	public static String NAME = "ExportTag";

	private static final Lazy<ExportTag> INSTANCE = Lazy.from(ExportTag::new);

	public static ExportTag v() {
		return INSTANCE.get();
	}

	private ExportTag() {
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
