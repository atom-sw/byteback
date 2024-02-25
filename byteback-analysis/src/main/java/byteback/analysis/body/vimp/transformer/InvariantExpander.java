package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.AssertionStmt;
import byteback.analysis.body.vimp.InvariantStmt;
import byteback.common.Lazy;

import java.util.*;
import java.util.function.Supplier;

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

    private InvariantExpander() {
    }

    public static InvariantExpander v() {
        return instance.get();
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
            final List<Stmt> loopStatements = loop.getLoopStatements();
            if (loopStatements.size() > 1) {
                startToLoop.put(loopStatements.get(0), loop);
                endToLoop.put(loopStatements.get(loopStatements.size() - 1), loop);
            }
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

                final Supplier<AssertionStmt> assertionUnitSupplier = () -> {
                    final AssertionStmt assertionUnit = Vimp.v().newAssertionStmt(condition);
                    assertionUnit.addAllTagsOf(invariantUnit);

                    return assertionUnit;
                };

                units.insertBefore(assertionUnitSupplier.get(), loop.getHead());

                if (loop.getHead() instanceof IfStmt) {
                    units.insertAfter(assertionUnitSupplier.get(), loop.getHead());
                }

                units.insertBefore(assertionUnitSupplier.get(), loop.getBackJumpStmt());

                final HashSet<Unit> exitTargets = new HashSet<>();

                for (final Stmt exit : loop.getLoopExits()) {
                    exitTargets.addAll(loop.targetsOfLoopExit(exit));
                }

                for (final Unit exitTarget : exitTargets) {
                    units.insertBefore(assertionUnitSupplier.get(), exitTarget);
                }

                units.remove(invariantUnit);
            }
        }
    }

}
