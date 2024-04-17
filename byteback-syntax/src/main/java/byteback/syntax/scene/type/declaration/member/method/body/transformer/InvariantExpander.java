package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.AssertStmt;
import byteback.syntax.scene.type.declaration.member.method.body.unit.InvariantStmt;
import byteback.common.function.Lazy;

import java.util.*;
import java.util.function.Supplier;

import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.jimple.IfStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.toolkits.graph.LoopNestTree;

/**
 * Expands loop invariants into a set of assertions and assumptions.
 * The criteria used is as follows:
 * ``` java
 * HEAD:
 *   ...
 *   invariant e;
 *   ...
 *   if (c) goto EXIT;
 *   ...
 *   goto HEAD;
 *   ...
 * EXIT:
 *   ...
 * ```
 * is transformed into:
 * ``` java
 *   assert e;
 * HEAD:
 *   assume e;
 *   ...
 *   assert e;
 *   if (c) goto EXIT;
 *   ...
 *   assert e;
 *   goto HEAD;
 *   ...
 * EXIT:
 *   assume e
 *   ...
 * ```
 *
 * @author paganma
 */
public class InvariantExpander extends BodyTransformer {

    private static final Lazy<InvariantExpander> INSTANCE = Lazy.from(InvariantExpander::new);

    private InvariantExpander() {
    }

    public static InvariantExpander v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();
        final PatchingChain<Unit> units = body.getUnits();
        final LoopNestTree loopTree = new LoopNestTree(body);

        for (final Loop loop : loopTree) {
            for (final Unit unit : loop.getLoopStatements()) {
                if (unit instanceof final InvariantStmt invariantUnit) {
                    final Value condition = invariantUnit.getCondition();

                    final Supplier<AssertStmt> assertionSupplier = () -> {
                        final AssertStmt assertionUnit = Vimp.v().newAssertStmt(condition);
                        assertionUnit.addAllTagsOf(invariantUnit);

                        return assertionUnit;
                    };

                    units.insertBefore(assertionSupplier.get(), loop.getHead());

                    if (loop.getHead() instanceof IfStmt) {
                        units.insertAfter(assertionSupplier.get(), loop.getHead());
                    }

                    units.insertBefore(assertionSupplier.get(), loop.getBackJumpStmt());

                    final HashSet<Unit> exitTargets = new HashSet<>();

                    for (final Stmt exit : loop.getLoopExits()) {
                        exitTargets.addAll(loop.targetsOfLoopExit(exit));
                    }

                    for (final Unit exitTarget : exitTargets) {
                        units.insertBefore(assertionSupplier.get(), exitTarget);
                    }

                    units.remove(invariantUnit);
                }
            }
        }
    }

}
