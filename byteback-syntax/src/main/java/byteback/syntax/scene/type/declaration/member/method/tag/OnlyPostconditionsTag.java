package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.Value;

import java.util.ArrayList;
import java.util.List;

public class OnlyPostconditionsTag extends ConditionsTag {

	public static final String NAME = "OnlyPostconditionsTag";

	public OnlyPostconditionsTag(final List<Value> conditions) {
		super(conditions);
	}

	public OnlyPostconditionsTag() {
		this(new ArrayList<>());
	}

	@Override
	public String getName() {
		return NAME;
	}

}
