package byteback.analysis.transformer;

import byteback.analysis.Vimp;
import byteback.analysis.vimp.InvariantStmt;
import byteback.util.Lazy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.Value;
import soot.grimp.GrimpBody;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.util.Chain;

public class InvariantExpander extends BodyTransformer {

	private static final Lazy<InvariantExpander> instance = Lazy.from(InvariantExpander::new);

	public static InvariantExpander v() {
		return instance.get();
	}

	private InvariantExpander() {
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
		final Iterator<Unit> unitIterator = units.snapshotIterator();
		final LoopFinder loopFinder = new LoopFinder();
		final HashMap<Unit, Loop> startToLoop = new HashMap<>();
		final HashMap<Unit, Loop> endToLoop = new HashMap<>();
		final Set<Loop> loops = loopFinder.getLoops(body);
		final Stack<Loop> activeLoops = new Stack<>();

		for (final Loop loop : loops) {
			startToLoop.put(loop.getHead(), loop);
			endToLoop.put(loop.getBackJumpStmt(), loop);
		}

		while (unitIterator.hasNext()) {
			final Unit unit = unitIterator.next();
			final Loop startedLoop = startToLoop.get(unit);
			final Loop endedLoop = endToLoop.get(unit);

			if (startedLoop != null) {
				activeLoops.push(startToLoop.get(unit));
			}

			if (endedLoop != null) {
				assert activeLoops.peek() == endedLoop;
				activeLoops.pop();
			}

			if (unit instanceof InvariantStmt invariantUnit) {
				if (activeLoops.isEmpty()) {
					throw new RuntimeException("Invariant " + invariantUnit + "cannot be expanded");
				}

				final Loop loop = activeLoops.peek();
				final Value condition = invariantUnit.getCondition();
				units.insertBefore(Vimp.v().newAssertionStmt(condition), loop.getHead());

				if (loop.getHead() instanceof IfStmt) {
					units.insertAfter(Vimp.v().newAssumptionStmt(condition), loop.getHead());
				}

				units.insertBefore(Vimp.v().newAssertionStmt(condition), loop.getBackJumpStmt());

				final HashSet<Unit> exitTargets = new HashSet<>();

				for (final Unit exit : loop.getLoopExits()) {
					exitTargets.addAll(loop.targetsOfLoopExit((Stmt) exit));
				}

				for (final Unit exitTarget : exitTargets) {
					units.insertBefore(Vimp.v().newAssertionStmt(condition), exitTarget);
				}

				units.remove(invariantUnit);
			}
		}
	}

}
