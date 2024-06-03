package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class ImportTag implements Tag {

	public static String NAME = "ImportTag";

	private static final Lazy<ImportTag> INSTANCE = Lazy.from(ImportTag::new);

	public static ImportTag v() {
		return INSTANCE.get();
	}

	private ImportTag() {
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
