package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.Value;

import java.util.ArrayList;
import java.util.List;

public class OnlyPreconditionsTag extends ConditionsTag {

	public static final String NAME = "OnlyPreconditionsTag";

	public OnlyPreconditionsTag(final List<Value> conditions) {
		super(conditions);
	}

	public OnlyPreconditionsTag() {
		this(new ArrayList<>());
	}

	@Override
	public String getName() {
		return NAME;
	}

}
