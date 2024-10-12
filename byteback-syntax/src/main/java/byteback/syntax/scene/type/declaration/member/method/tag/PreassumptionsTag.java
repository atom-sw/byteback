package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.Value;

import java.util.ArrayList;
import java.util.List;

public class PreassumptionsTag extends ConditionsTag {

	public static final String NAME = "PreassumptionsTag";

	public PreassumptionsTag(final List<Value> conditions) {
		super(conditions);
	}

	public PreassumptionsTag() {
		this(new ArrayList<>());
	}

	@Override
	public String getName() {
		return NAME;
	}

}
