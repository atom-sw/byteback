package byteback.syntax.scene.type.declaration.member.method.body.value.box.tag;

import byteback.common.function.Lazy;
import byteback.syntax.tag.TagMarker;
import soot.ValueBox;

public class HeapReaderTagMarker extends TagMarker<ValueBox, HeapReaderTag> {

	private static final Lazy<HeapReaderTagMarker> INSTANCE = Lazy.from(() -> new HeapReaderTagMarker(HeapReaderTag.v()));

	public static HeapReaderTagMarker v() {
		return INSTANCE.get();
	}

	private HeapReaderTagMarker(final HeapReaderTag tag) {
		super(tag);
	}

}
