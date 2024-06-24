package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
import soot.*;
import soot.jimple.*;

public class ExplicitTypeCaster extends BodyTransformer {

	private static final Lazy<ExplicitTypeCaster> INSTANCE = Lazy.from(ExplicitTypeCaster::new);

	public static ExplicitTypeCaster v() {
		return INSTANCE.get();
	}

	private ExplicitTypeCaster() {
	}

	public void castIfNeeded(final ValueBox valueBox, final Type expectedType) {
		final Value value = valueBox.getValue();

		// Propagate checks to subexpressions.
		if (value instanceof final BinopExpr binopExpr) {
			if (binopExpr instanceof AndExpr
					|| binopExpr instanceof OrExpr
					|| binopExpr instanceof XorExpr
					|| binopExpr instanceof AddExpr
					|| binopExpr instanceof SubExpr
					|| binopExpr instanceof MulExpr
					|| binopExpr instanceof DivExpr
					|| binopExpr instanceof EqExpr
					|| binopExpr instanceof NeExpr
					|| binopExpr instanceof LtExpr
					|| binopExpr instanceof LeExpr
					|| binopExpr instanceof GtExpr
					|| binopExpr instanceof GeExpr) {
				final Type type1 = VimpTypeInterpreter.v().typeOf(binopExpr.getOp1());
				final Type type2 = VimpTypeInterpreter.v().typeOf(binopExpr.getOp2());
				final Type joinedType = VimpTypeInterpreter.v().join(type1, type2);

				if (joinedType != NullType.v()) {
					castIfNeeded(binopExpr.getOp1Box(), joinedType);
					castIfNeeded(binopExpr.getOp2Box(), joinedType);
				}
			}
		}

		// If the current expression's type differs from the expected type, we cast the
		// expression to that expected
		// type.
		final Type actualType = VimpTypeInterpreter.v().typeOf(value);

		if (actualType != expectedType) {
			final Value newValue = Vimp.v().nest(Jimple.v().newCastExpr(Vimp.v().nest(value), expectedType));
			valueBox.setValue(newValue);
		}
	}

	@Override
	public void transformBody(final SootMethod sootMethod, final Body body) {
		for (final Unit unit : body.getUnits()) {
			if (unit instanceof final AssignStmt assignStmt) {
				final ValueBox rightOpBox = assignStmt.getRightOpBox();
				final Type leftType = VimpTypeInterpreter.v().typeOf(assignStmt.getLeftOp());
				castIfNeeded(rightOpBox, leftType);
			}
		}
	}

}
