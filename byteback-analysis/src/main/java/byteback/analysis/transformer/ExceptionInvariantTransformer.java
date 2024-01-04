package byteback.analysis.transformer;

import byteback.analysis.Vimp;
import byteback.analysis.vimp.VoidConstant;
import byteback.util.Lazy;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import soot.Body;
import soot.BodyTransformer;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.grimp.GrimpBody;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.util.Chain;

public class ExceptionInvariantTransformer extends BodyTransformer {

	private static final Lazy<ExceptionInvariantTransformer> instance = Lazy.from(ExceptionInvariantTransformer::new);

	public static ExceptionInvariantTransformer v() {
		return instance.get();
	}

	private ExceptionInvariantTransformer() {
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
		final LoopFinder loopFinder = new LoopFinder();
		final Set<Loop> loops = loopFinder.getLoops(body);
		final Set<Unit> trapHandlers = new HashSet<>();

		for (final Trap trap : body.getTraps()) {
			trapHandlers.add(trap.getHandlerUnit());
		}

		for (final Loop loop : loops) {
			if (loop.getHead() != loop.getBackJumpStmt()) {
				final Value assertionValue = Vimp.v().newEqExpr(Vimp.v().newCaughtExceptionRef(), VoidConstant.v());
				final Unit headUnit = loop.getHead();
				final Unit newHeadUnit = Vimp.v().newAssertionStmt(assertionValue);
				final Unit backJumpUnit = loop.getBackJumpStmt();
				final Unit newBackJumpUnit = Vimp.v().newAssertionStmt(assertionValue);
				final Set<Unit> annotatedUnits= new HashSet<>();

				units.insertBefore(newHeadUnit, headUnit);
				headUnit.redirectJumpsToThisTo(newHeadUnit);

				units.insertBefore(newBackJumpUnit, backJumpUnit);
				backJumpUnit.redirectJumpsToThisTo(newBackJumpUnit);

				for (final Unit exit : loop.getLoopExits()) {
					if (!annotatedUnits.contains(exit)) {
						final Unit newExitUnit = Vimp.v().newAssertionStmt(assertionValue);
						units.insertBefore(newExitUnit, exit);
						exit.redirectJumpsToThisTo(newExitUnit);
						annotatedUnits.add(exit);

						for (final Unit exitTarget : loop.targetsOfLoopExit((Stmt) exit)) {
							if (!trapHandlers.contains(exitTarget) && !annotatedUnits.contains(exitTarget)) {
								final Unit newTargetUnit = Vimp.v().newAssertionStmt(assertionValue);
								units.insertBefore(newTargetUnit, exitTarget);
								exitTarget.redirectJumpsToThisTo(newTargetUnit);
								annotatedUnits.add(exitTarget);
							}
						}
					}
				}
			}
		}
	}

}
