package byteback.analysis.transformer;

import byteback.analysis.Namespace;
import byteback.analysis.Vimp;
import byteback.analysis.vimp.VoidConstant;
import byteback.util.Lazy;
import java.util.Iterator;
import java.util.Map;
import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Grimp;
import soot.grimp.GrimpBody;
import soot.jimple.InvokeExpr;
import soot.util.Chain;

public class CallCheckTransformer extends BodyTransformer {

	private static final Lazy<CallCheckTransformer> instance = Lazy.from(CallCheckTransformer::new);

	public static CallCheckTransformer v() {
		return instance.get();
	}

	private CallCheckTransformer() {
	}

	@Override
	public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		if (body instanceof GrimpBody) {
			transformBody(body);
		} else {
			throw new IllegalArgumentException("Can only transform Grimp");
		}
	}

	public Value makeCheckExpr() {
		return Grimp.v().newEqExpr(Vimp.v().newCaughtExceptionRef(), VoidConstant.v());
	}

	public void transformBody(final Body body) {
		final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();
		final Chain<Unit> units = body.getUnits();

		while (unitIterator.hasNext()) {
			final Unit unit = unitIterator.next();

			for (final ValueBox vbox : unit.getUseAndDefBoxes()) {
				final Value value = vbox.getValue();

				if (value instanceof InvokeExpr invokeExpr && !Namespace.isPureMethod(invokeExpr.getMethod())
						&& !Namespace.isAnnotationClass(invokeExpr.getMethod().getDeclaringClass())) {
					final Unit throwUnit = Grimp.v().newThrowStmt(Vimp.v().newCaughtExceptionRef());
					units.insertAfter(throwUnit, unit);
					final Unit elseBranch = units.getSuccOf(throwUnit);
					final Unit ifUnit = Vimp.v().newIfStmt(makeCheckExpr(), elseBranch);
					units.insertAfter(ifUnit, unit);
				}
			}
		}
	}

}
