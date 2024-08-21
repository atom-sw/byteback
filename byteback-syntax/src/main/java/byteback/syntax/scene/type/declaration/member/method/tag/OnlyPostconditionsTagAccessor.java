package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;

public class OnlyPostconditionsTagAccessor extends ConditionsTagAccessor<OnlyPostconditionsTag> {

	private static final Lazy<OnlyPostconditionsTagAccessor> INSTANCE = Lazy
			.from(() -> new OnlyPostconditionsTagAccessor(OnlyPostconditionsTag.NAME));

	public static OnlyPostconditionsTagAccessor v() {
		return INSTANCE.get();
	}

	private OnlyPostconditionsTagAccessor(final String tagName) {
		super(tagName);
	}

}
