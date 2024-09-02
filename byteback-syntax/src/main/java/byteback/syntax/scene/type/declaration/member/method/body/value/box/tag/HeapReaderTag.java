package byteback.syntax.scene.type.declaration.member.method.body.value.box.tag;

import byteback.common.function.Lazy;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class HeapReaderTag implements Tag {

	public static String NAME = "HeapReaderTag";

	private static final Lazy<HeapReaderTag> INSTANCE = Lazy.from(HeapReaderTag::new);

	public static HeapReaderTag v() {
		return INSTANCE.get();
	}

	private HeapReaderTag() {
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
