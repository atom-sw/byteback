package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.local.vimp.syntax.query.VimpEffectEvaluator;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.analysis.local.vimp.syntax.value.NestedExpr;
import byteback.common.collection.SetHashMap;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.infoflow.CachedEquivalentValue;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.*;

/**
 * Folds expressions used in the Body as nested expressions based on some condition.
 * @see NestedExpr
 *
 * @author paganma
 */
public abstract class ExprFolder extends BodyTransformer {

    @Override
    public void transformBody(final Body body) {
        final Chain<Unit> units = body.getUnits();
        final BlockGraph blockGraph = new BriefBlockGraph(body);
        final UnitGraph unitGraph = new BriefUnitGraph(body);
        final LocalDefs localDefs = new SimpleLocalDefs(unitGraph);
        final LocalUses localUses = new SimpleLocalUses(unitGraph, localDefs);

        for (final Block block : blockGraph) {
            final var blockSnapshot = new HashChain<Unit>();
            final var blockFolder = new BlockFolder(units, localDefs, localUses);

            for (final Unit unit : block) {
                blockSnapshot.add(unit);
            }

            blockFolder.fold(blockSnapshot);
        }
    }

    /**
     * Whether a value can be substituted at position `valueBox` in `unit`.
     * @param unit The unit in which the substitution may occur.
     * @param valueBox The position in the unit in which the substitution may occur.
     * @return `true` if a value can be inlined in `valueBox`, `false` otherwise.
     */
    protected boolean canSubstitute(final Unit unit, final ValueBox valueBox) {
        return true;
    }

    public class BlockFolder {

        protected final HashMap<Local, AssignStmt> localToSubstitution;

        protected final SetHashMap<Value, Local> dependencyToLocals;

        protected final Chain<Unit> units;

        protected final LocalDefs localDefs;

        protected final LocalUses localUses;

        public BlockFolder(final Chain<Unit> units, final LocalDefs localDefs, final LocalUses localUses) {
            this.localToSubstitution = new HashMap<>();
            this.dependencyToLocals = new SetHashMap<>();
            this.units = units;
            this.localDefs = localDefs;
            this.localUses = localUses;
        }

        protected void track(final AssignStmt assignStmt) {
            if (assignStmt.getLeftOp() instanceof Local local) {
                localToSubstitution.put(local, assignStmt);
            } else if (assignStmt.getLeftOp() instanceof Ref dependency) {
                final Set<Local> dependentLocals = dependencyToLocals.get(new CachedEquivalentValue(dependency));

                if (dependentLocals != null) {
                    for (final Local dependentLocal : dependentLocals) {
                        localToSubstitution.remove(dependentLocal);
                    }
                }

                dependencyToLocals.remove(dependency);
            }
        }

        protected void substituteFrom(final Unit unit, final ValueBox startBox) {
            final var nextSubstitutions = new ArrayDeque<ValueBox>();
            nextSubstitutions.add(startBox);

            while (!nextSubstitutions.isEmpty()) {
                final ValueBox substitutionBox = nextSubstitutions.pop();

                if (substitutionBox.getValue() instanceof final Local local) {
                    final AssignStmt substitution = localToSubstitution.get(local);

                    if (substitution != null
                            && localDefs.getDefsOfAt(local, unit).size() == 1
                            && localUses.getUsesOf(substitution).size() == 1
                            && !VimpEffectEvaluator.v().hasSideEffects(substitution.getRightOp())) {
                        nextSubstitutions.addAll(substitution.getUseBoxes());

                        if (substitution.getRightOp() instanceof Immediate immediate) {
                            substitutionBox.setValue(immediate);
                        } else {
                            // Notice the condition above specifies that the local has exactly one def and one use at
                            // the position at which nestedExpr is being inserted, hence the substitution obeys
                            // NestedExpr's contract.
                            substitutionBox.setValue(Vimp.v().newNestedExpr(substitution));
                        }

                        units.remove(substitution);
                    }
                }
            }
        }

        protected void substituteUses(final Unit unit) {
            for (final ValueBox useBox : unit.getUseBoxes()) {
                if (canSubstitute(unit, useBox)) {
                    substituteFrom(unit, useBox);
                }
            }
        }

        protected void fold(final Iterable<Unit> block) {
            for (final Unit unit : block) {
                if (unit instanceof final AssignStmt assignUnit) {
                    track(assignUnit);
                }

                substituteUses(unit);
            }
        }

    }

}
