package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;

public class OnlyPreconditionsTagAccessor extends ConditionsTagAccessor<OnlyPreconditionsTag> {

	private static final Lazy<OnlyPreconditionsTagAccessor> INSTANCE = Lazy
			.from(() -> new OnlyPreconditionsTagAccessor(OnlyPreconditionsTag.NAME));

	public static OnlyPreconditionsTagAccessor v() {
		return INSTANCE.get();
	}

	private OnlyPreconditionsTagAccessor(final String tagName) {
		super(tagName);
	}

}
