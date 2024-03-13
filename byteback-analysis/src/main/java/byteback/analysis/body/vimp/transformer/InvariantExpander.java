package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.syntax.AssertStmt;
import byteback.analysis.body.vimp.syntax.InvariantStmt;
import byteback.common.function.Lazy;
import byteback.analysis.body.common.Body;
import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.jimple.syntax.IfStmt;
import byteback.analysis.body.jimple.syntax.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.util.Chain;

import java.util.*;
import java.util.function.Supplier;

public class InvariantExpander extends BodyTransformer {

    private static final Lazy<InvariantExpander> instance = Lazy.from(InvariantExpander::new);

    private InvariantExpander() {
    }

    public static InvariantExpander v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final Chain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();
        final LoopFinder loopFinder = new LoopFinder();
        final Set<Loop> loops = loopFinder.getLoops(body);

        final var startToLoop = new HashMap<Unit, Loop>();
        final var endToLoop = new HashMap<Unit, Loop>();
        final var invariantStmts = new HashSet<InvariantStmt>();
        final var activeLoops = new ArrayDeque<Loop>();

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

                final Supplier<AssertStmt> assertionUnitSupplier = () -> {
                    final AssertStmt assertionUnit = Vimp.v().newAssertStmt(condition);
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

                invariantStmts.add(invariantUnit);
            }
        }

        for (final InvariantStmt invariantStmt : invariantStmts) {
            units.remove(invariantStmt);
        }
    }

}
