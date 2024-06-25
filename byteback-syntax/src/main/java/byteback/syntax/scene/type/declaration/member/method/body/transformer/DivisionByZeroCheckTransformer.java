package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import java.util.Optional;

import soot.IntType;
import soot.Scene;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DivExpr;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;

public class DivisionByZeroCheckTransformer extends CheckTransformer {

	public DivisionByZeroCheckTransformer(final Scene scene) {
		super(scene, "java.lang.ArithmeticException");
	}

	@Override
	public Optional<Value> makeUnitCheck(final Unit unit) {
		for (final ValueBox valueBox : unit.getUseBoxes()) {
			final Value value = valueBox.getValue();

			if (value instanceof final DivExpr divExpr
					&& divExpr.getType() instanceof IntType) {
				final Value checkExpr = Jimple.v().newNeExpr(divExpr.getOp2(), IntConstant.v(0));

				return Optional.of(checkExpr);
			}
		}

		return Optional.empty();
	}

}
