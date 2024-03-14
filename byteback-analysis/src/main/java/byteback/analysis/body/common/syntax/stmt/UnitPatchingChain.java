package byteback.analysis.body.common.syntax.stmt;

import byteback.analysis.body.jimple.syntax.stmt.GotoStmt;
import byteback.analysis.common.syntax.Chain;
import byteback.analysis.common.syntax.PatchingChain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Although the Patching Chain is meant to only work for units, it can also work with certain subclasses of units. However,
 * for insertOnEdge and similar operations, new Jimple statements have to be generated. As such, it might be the case that a
 * {@code PatchingChain<X extends Unit>} is not allowed to contain such new statements, since they are not a subclass of X.
 * Therefore, we decided to create a chain specifically for units, where we can be certain that they are allowed to contain
 * all kind of units. Feel free to go and grab a beer.
 */
public class UnitPatchingChain extends PatchingChain<Unit> {

    public UnitPatchingChain(Chain<Unit> aChain) {
        super(aChain);
    }

    /**
     * Inserts instrumentation in a manner such that the resulting control flow graph (CFG) of the program will contain
     * <code>toInsert</code> on an edge that is defined by <code>point_source</code> and <code>point_target</code>.
     *
     * @param toInsert  instrumentation to be added in the Chain
     * @param sourceUnit the source point of an edge in CFG
     * @param targetUnit the target point of an edge
     */
    public void insertOnEdge(final Collection<? extends Unit> toInsert, final Unit sourceUnit, final Unit targetUnit) {
        // Nothing to do if it's empty
        if (toInsert.isEmpty()) {
            return;
        }

        // Insert 'toInsert' before 'target' point in chain if the source point is null
        if (sourceUnit == null) {
            Unit firstInserted = toInsert.iterator().next();
            targetUnit.redirectJumpsToThisTo(firstInserted);
            innerChain.insertBefore(toInsert, targetUnit);
            return;
        }

        // Insert 'toInsert' after 'source' point in chain if the target point is null
        if (targetUnit == null) {
            innerChain.insertAfter(toInsert, sourceUnit);
            return;
        }

        // If target is right after the source in the Chain
        // 1- Redirect all jumps (if any) from 'source' to 'target', to 'toInsert[0]'
        // (source->target) ==> (source->toInsert[0])
        // 2- Insert 'toInsert' after 'source' in Chain
        if (getSuccOf(sourceUnit) == targetUnit) {
            Unit firstInserted = toInsert.iterator().next();

            for (UnitBox box : sourceUnit.getUnitBoxes()) {
                if (box.getUnit() == targetUnit) {
                    box.setUnit(firstInserted);
                }
            }

            innerChain.insertAfter(toInsert, sourceUnit);
            return;
        }

        // If the target is not right after the source in chain then,
        // 1- Redirect all jumps (if any) from 'source' to 'target', to 'toInsert[0]'
        // (source->target) ==> (source->toInsert[0])
        // 1.1- if there are no jumps from source to target, then such an edge
        // does not exist. Throw an exception.
        // 2- Insert 'toInsert' before 'target' in Chain
        // 3- If required, add a 'goto target' statement so that no other edge
        // executes 'toInsert'
        final Unit firstInserted = toInsert.iterator().next();
        boolean validEdgeFound = false;
        Unit originalPred = this.getPredOf(targetUnit);
        for (UnitBox box : sourceUnit.getUnitBoxes()) {
            if (box.getUnit() == targetUnit) {
                if (sourceUnit instanceof GotoStmt) {
                    box.setUnit(firstInserted);
                    innerChain.insertAfter(toInsert, sourceUnit);

                    Unit goto_unit = new GotoStmt(targetUnit);
                    if (toInsert instanceof List<? extends Unit> l) {
                        innerChain.insertAfter(goto_unit, l.get(l.size() - 1));
                    } else {
                        innerChain.insertAfter(goto_unit, (Unit) toInsert.toArray()[toInsert.size() - 1]);
                    }
                    return;
                }

                box.setUnit(firstInserted);
                validEdgeFound = true;
            }
        }
        if (validEdgeFound) {
            innerChain.insertBefore(toInsert, targetUnit);

            if (originalPred != sourceUnit) {
                if (originalPred instanceof GotoStmt) {
                    return;
                }

                Unit goto_unit = new GotoStmt(targetUnit);
                innerChain.insertBefore(Collections.singletonList(goto_unit), firstInserted);
            }
            return;
        }

        // In certain scenarios, the above code can add extra 'goto' units on a
        // different edge
        // So, an edge [src --> tgt] becomes [src -> goto tgt -> tgt].
        // When this happens, the original edge [src -> tgt] ceases to exist.
        // The following code handles such scenarios.
        final Unit succ = getSuccOf(sourceUnit);

        if (succ instanceof GotoStmt) {
            if (succ.getUnitBoxes().get(0).getUnit() == targetUnit) {
                succ.redirectJumpsToThisTo(firstInserted);
                innerChain.insertBefore(toInsert, succ);
                return;
            }
        }
        // If the control reaches this point, it means that an edge [src -> tgt]
        // as specified by user does not exist so throw an exception.
        throw new RuntimeException(
                "insertOnEdge failed! No such edge found. The edge on which you want to insert an instrumentation is invalid.");

    }

    /**
     * Inserts instrumentation in a manner such that the resulting control flow graph (CFG) of the program will contain
     * <code>toInsert</code> on an edge that is defined by <code>point_source</code> and <code>point_target</code>.
     *
     * @param toInsert  instrumentation to be added in the Chain
     * @param sourceUnit the source point of an edge in CFG
     * @param targetUnit the target point of an edge
     */
    public void insertOnEdge(final List<Unit> toInsert, final Unit sourceUnit, final Unit targetUnit) {
        insertOnEdge((Collection<Unit>) toInsert, sourceUnit, targetUnit);
    }

    /**
     * Inserts instrumentation in a manner such that the resulting control flow graph (CFG) of the program will contain
     * <code>toInsert</code> on an edge that is defined by <code>point_source</code> and <code>point_target</code>.
     *
     * @param toInsert  instrumentation to be added in the Chain
     * @param sourceUnit the source point of an edge in CFG
     * @param targetUnit the target point of an edge
     */
    public void insertOnEdge(final Chain<Unit> toInsert, final Unit sourceUnit, final Unit targetUnit) {
        insertOnEdge((Collection<Unit>) toInsert, sourceUnit, targetUnit);
    }

    /**
     * Inserts instrumentation in a manner such that the resulting control flow graph (CFG) of the program will contain
     * <code>toInsert</code> on an edge that is defined by <code>point_source</code> and <code>point_target</code>.
     *
     * @param toInsert  the instrumentation to be added in the Chain
     * @param sourceUnit the source point of an edge in CFG
     * @param sourceTarget the target point of an edge
     */
    public void insertOnEdge(final Unit toInsert, final Unit sourceUnit, final Unit sourceTarget) {
        insertOnEdge(Collections.singleton(toInsert), sourceUnit, sourceTarget);
    }
}
