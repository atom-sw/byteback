package byteback.syntax.scene.type.declaration.member.method.tag;

import byteback.syntax.tag.TagAccessor;
import soot.SootMethod;

public abstract class ConditionsTagAccessor<T extends ConditionsTag> extends TagAccessor<SootMethod, T> {

	public ConditionsTagAccessor(final String tagName) {
		super(tagName);
	}

}
