package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import soot.Scene;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.*;

import java.util.Optional;

/**
 * Introduces explicit index checks before every array dereference.
 *
 * @author paganma
 */
public class ArraySizeCheckTransformer extends CheckTransformer {

	public ArraySizeCheckTransformer(final Scene scene) {
		super(scene, "java.lang.NegativeArraySizeException");
	}

	@Override
	public Optional<Value> makeUnitCheck(final Unit unit) {
		for (final ValueBox valueBox : unit.getUseBoxes()) {
			final Value value = valueBox.getValue();

			if (value instanceof final NewArrayExpr newArrayExpr) {
				final Value checkExpr = Jimple.v().newGeExpr(newArrayExpr.getSize(), IntConstant.v(0));
				
				return Optional.of(checkExpr);
			}
		}

		return Optional.empty();
	}

}
