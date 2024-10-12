package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;

public class PreassumptionsTagAccessor extends ConditionsTagAccessor<PreassumptionsTag> {

	private static final Lazy<PreassumptionsTagAccessor> INSTANCE = Lazy
			.from(() -> new PreassumptionsTagAccessor(PreassumptionsTag.NAME));

	public static PreassumptionsTagAccessor v() {
		return INSTANCE.get();
	}

	private PreassumptionsTagAccessor(final String tagName) {
		super(tagName);
	}

}
