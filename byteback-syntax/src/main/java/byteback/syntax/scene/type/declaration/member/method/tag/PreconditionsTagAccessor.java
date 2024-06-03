package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;

public class PreconditionsTagAccessor extends ConditionsTagAccessor<PreconditionsTag> {

	private static final Lazy<PreconditionsTagAccessor> INSTANCE = Lazy
			.from(() -> new PreconditionsTagAccessor(PreconditionsTag.NAME));

	public static PreconditionsTagAccessor v() {
		return INSTANCE.get();
	}

	private PreconditionsTagAccessor(final String tagName) {
		super(tagName);
	}

}
