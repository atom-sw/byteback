package byteback.analysis.transformer;

import byteback.analysis.Vimp;
import byteback.analysis.vimp.VoidConstant;
import byteback.util.Lazy;
import byteback.util.ListHashMap;
import byteback.util.Stacks;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.RefType;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.grimp.Grimp;
import soot.grimp.GrimpBody;
import soot.jimple.AssignStmt;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ThrowStmt;
import soot.util.Chain;

public class GuardTransformer extends BodyTransformer {

	private static final Lazy<GuardTransformer> instance = Lazy.from(GuardTransformer::new);

	public static GuardTransformer v() {
		return instance.get();
	}

	private GuardTransformer() {
	}

	@Override
	public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		if (body instanceof GrimpBody) {
			transformBody(body);
		} else {
			throw new IllegalArgumentException("Can only transform Grimp");
		}
	}

	public void transformBody(final Body body) {
		final Chain<Unit> units = body.getUnits();
		final Chain<Trap> traps = body.getTraps();
		final ListHashMap<Unit, Trap> startToTraps = new ListHashMap<>();
		final ListHashMap<Unit, Trap> endToTraps = new ListHashMap<>();
		final HashSet<Unit> trapHandlers = new HashSet<>();
		final Stack<Trap> activeTraps = new Stack<>();
		final Iterator<Unit> unitIterator = units.snapshotIterator();
		units.addFirst(Grimp.v().newAssignStmt(Vimp.v().newCaughtExceptionRef(), VoidConstant.v()));

		for (final Trap trap : traps) {
			startToTraps.add(trap.getBeginUnit(), trap);
			endToTraps.add(trap.getEndUnit(), trap);
			trapHandlers.add(trap.getHandlerUnit());
		}

		for (final Unit handler : trapHandlers) {
			assert handler instanceof AssignStmt assign && assign.getLeftOp() instanceof Local
					&& assign.getRightOp() instanceof CaughtExceptionRef;

			final AssignStmt assignment = Grimp.v().newAssignStmt(Vimp.v().newCaughtExceptionRef(), VoidConstant.v());
			units.insertAfter(assignment, handler);
		}

		while (unitIterator.hasNext()) {
			final Unit unit = unitIterator.next();
			final List<Trap> startedTraps = startToTraps.get(unit);
			final List<Trap> endedTraps = endToTraps.get(unit);

			if (endedTraps != null) {
				Stacks.popAll(activeTraps, endToTraps.get(unit));
			}

			if (startedTraps != null) {
				Stacks.pushAll(activeTraps, startToTraps.get(unit));
			}

			if (unit instanceof ThrowStmt throwUnit) {
				final Unit retUnit = Grimp.v().newReturnVoidStmt();

				units.insertBefore(retUnit, throwUnit);;
				units.remove(throwUnit);

				final Unit assignUnit;

				if (throwUnit.getOp() instanceof CaughtExceptionRef) {
					assignUnit = units.getPredOf(retUnit);
				} else {
					assignUnit = Grimp.v().newAssignStmt(Vimp.v().newCaughtExceptionRef(), throwUnit.getOp());
					units.insertBefore(assignUnit, retUnit);
				}

				Unit indexUnit = assignUnit;

				if (throwUnit.getOp().getType()instanceof RefType throwType) {

					for (int i = activeTraps.size() - 1; i >= 0; --i) {
						final Trap activeTrap = activeTraps.get(i);

						final RefType trapType = activeTrap.getException().getType();
						final Value condition = Vimp.v().newInstanceOfExpr(Vimp.v().newCaughtExceptionRef(), trapType);
						final Unit ifUnit = Vimp.v().newIfStmt(condition, activeTrap.getHandlerUnit());
						units.insertAfter(ifUnit, indexUnit);
						indexUnit = ifUnit;
					}
				}
			}
		}
	}

}
