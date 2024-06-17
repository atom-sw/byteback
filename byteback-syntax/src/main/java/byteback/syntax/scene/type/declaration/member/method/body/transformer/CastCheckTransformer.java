package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import soot.RefLikeType;
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
public class CastCheckTransformer extends CheckTransformer {

	public CastCheckTransformer(final Scene scene) {
		super(scene, "java.lang.ClassCastException");
	}

	@Override
	public Optional<Value> makeUnitCheck(final Unit unit) {
		for (final ValueBox valueBox : unit.getUseBoxes()) {
			final Value value = valueBox.getValue();

			if (value instanceof final CastExpr castExpr) {
				if (castExpr.getCastType() instanceof RefLikeType castType) {
					final Value checkExpr = Jimple.v().newInstanceOfExpr(castExpr.getOp(), castType);

					return Optional.of(checkExpr);
				} else {
					break;
				}
			}
		}

		return Optional.empty();
	}

}
