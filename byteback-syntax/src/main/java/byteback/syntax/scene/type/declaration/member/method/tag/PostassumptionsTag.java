package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.Value;

import java.util.ArrayList;
import java.util.List;

public class PostassumptionsTag extends ConditionsTag {

	public static final String NAME = "PostassumptionsTag";

	public PostassumptionsTag(final List<Value> conditions) {
		super(conditions);
	}

	public PostassumptionsTag() {
		this(new ArrayList<>());
	}

	@Override
	public String getName() {
		return NAME;
	}

}
