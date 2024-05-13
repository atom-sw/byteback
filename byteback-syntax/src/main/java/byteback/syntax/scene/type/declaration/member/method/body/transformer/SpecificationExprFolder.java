package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.unit.SpecificationStmt;
import soot.Unit;
import soot.ValueBox;

/**
 * Folder for the specification expressions that appear in the body of a
 * procedural method.
 *
 * @author paganma
 */
public class SpecificationExprFolder extends ExprFolder {

	private static final Lazy<SpecificationExprFolder> INSTANCE = Lazy.from(SpecificationExprFolder::new);

	public static SpecificationExprFolder v() {
		return INSTANCE.get();
	}

	@Override
	public boolean canSubstituteUse(final Unit unit, final ValueBox valueBox) {
		return unit instanceof SpecificationStmt;
	}

}
