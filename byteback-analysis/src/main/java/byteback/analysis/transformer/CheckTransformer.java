package byteback.analysis.transformer;

import byteback.analysis.Vimp;
import byteback.util.Lazy;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethodRef;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Grimp;
import soot.grimp.GrimpBody;
import soot.jimple.Jimple;
import soot.jimple.SpecialInvokeExpr;
import soot.util.Chain;
import soot.util.HashChain;

public abstract class CheckTransformer extends BodyTransformer {

	public final SootClass exceptionClass;

	public CheckTransformer(final SootClass exceptionClass) {
		this.exceptionClass = exceptionClass;
	}

	@Override
	public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		if (body instanceof GrimpBody) {
			transformBody(body);
		} else {
			throw new IllegalArgumentException("Can only transform Grimp");
		}
	}

	public Chain<Unit> makeThrowUnits(final Body body, Supplier<Local> localSupplier) {
		final Chain<Unit> units = new HashChain<>();
		final Local local = localSupplier.get();
		final Unit initUnit = Grimp.v().newAssignStmt(local, Jimple.v().newNewExpr(exceptionClass.getType()));
		units.addLast(initUnit);
		final SootMethodRef constructorRef = exceptionClass.getMethod("<init>", Collections.emptyList()).makeRef();
		final SpecialInvokeExpr invokeExpr = Grimp.v().newSpecialInvokeExpr(local, constructorRef,
				Collections.emptyList());
		final Unit constructorUnit = Grimp.v().newInvokeStmt(invokeExpr);
		units.addLast(constructorUnit);
		final Unit throwUnit = Grimp.v().newThrowStmt(local);
		units.addLast(throwUnit);
		return units;
	}

	public abstract Value extractTarget(Value value);

	public abstract Value makeCheckExpr(Value inner, Value outer);

	public void transformBody(final Body body) {
		final Chain<Unit> units = body.getUnits();
		final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();
		final Lazy<Local> exceptionLocalSupplier = Lazy.from(() -> {
			final Local local = Scene.v().createLocalGenerator(body).generateLocal(exceptionClass.getType());

			return local;
		});

		while (unitIterator.hasNext()) {
			final Unit unit = unitIterator.next();

			for (final ValueBox valueBox : unit.getUseAndDefBoxes()) {
				final Value value = valueBox.getValue();
				Value target = extractTarget(value);

				if (target != null) {
					final Value checkExpr = makeCheckExpr(target, value);
					final Chain<Unit> throwStmts = makeThrowUnits(body, exceptionLocalSupplier);
					units.insertBefore(throwStmts, unit);
					final Unit checkStmt = Vimp.v().newIfStmt(checkExpr, unit);
					units.insertBefore(checkStmt, throwStmts.getFirst());
				}
			}
		}
	}

}
