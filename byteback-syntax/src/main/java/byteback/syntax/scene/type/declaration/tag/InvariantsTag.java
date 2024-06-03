package byteback.syntax.scene.type.declaration.tag;

import soot.Value;

import java.util.ArrayList;
import java.util.List;

import byteback.syntax.scene.type.declaration.member.method.tag.ConditionsTag;

public class InvariantsTag extends ConditionsTag {

	public static final String NAME = "InvariantsTag";

	public InvariantsTag(final List<Value> conditions) {
		super(conditions);
	}

	public InvariantsTag() {
		this(new ArrayList<>());
	}

	@Override
	public String getName() {
		return NAME;
	}

}
