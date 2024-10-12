package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.common.function.Lazy;

public class PostassumptionsTagAccessor extends ConditionsTagAccessor<PostassumptionsTag> {

	private static final Lazy<PostassumptionsTagAccessor> INSTANCE = Lazy
			.from(() -> new PostassumptionsTagAccessor(PostassumptionsTag.NAME));

	public static PostassumptionsTagAccessor v() {
		return INSTANCE.get();
	}

	private PostassumptionsTagAccessor(final String tagName) {
		super(tagName);
	}

}
