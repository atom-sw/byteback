package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.Body;
import byteback.analysis.body.common.syntax.expr.Immediate;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.graph.*;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.jimple.syntax.expr.Local;
import byteback.analysis.body.jimple.syntax.expr.Ref;
import byteback.analysis.body.jimple.syntax.stmt.AssignStmt;
import byteback.analysis.body.vimp.VimpValues;
import byteback.analysis.body.vimp.syntax.NestedExpr;
import byteback.analysis.common.syntax.Chain;
import byteback.analysis.common.syntax.HashChain;
import byteback.common.collection.SetHashMap;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Set;

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

    public boolean canSubstitute(final Unit unit, final ValueBox valueBox) {
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

        public void track(final AssignStmt assignStmt) {
            if (assignStmt.getLeftOp() instanceof Local local) {
                localToSubstitution.put(local, assignStmt);
            } else if (assignStmt.getLeftOp() instanceof Ref dependency) {
                final Set<Local> dependentLocals = dependencyToLocals.get(dependency);

                if (dependentLocals != null) {
                    for (final Local dependentLocal : dependentLocals) {
                        localToSubstitution.remove(dependentLocal);
                    }
                }

                dependencyToLocals.remove(dependency);
            }
        }

        public void substituteFrom(final Unit unit, final ValueBox startingValueBox) {
            final var nextValueBoxes = new ArrayDeque<ValueBox>();
            nextValueBoxes.add(startingValueBox);

            while (!nextValueBoxes.isEmpty()) {
                final ValueBox valueBox = nextValueBoxes.pop();
                if (valueBox.getValue() instanceof final Local local) {
                    final AssignStmt substitution = localToSubstitution.get(local);

                    if (substitution != null
                            && localDefs.getDefsOfAt(local, unit).size() == 1
                            && localUses.getUsesOf(substitution).size() == 1
                            && !VimpValues.v().hasSideEffects(substitution.getRightOp())) {
                        nextValueBoxes.addAll(substitution.getUseBoxes());

                        if (substitution.getRightOp() instanceof Immediate immediate) {
                            valueBox.setValue(immediate);
                        } else {
                            valueBox.setValue(new NestedExpr(substitution));
                        }

                        units.remove(substitution);
                    }
                }
            }
        }

        public void substituteUses(final Unit unit) {
            for (final ValueBox useBox : unit.getUseBoxes()) {
                if (canSubstitute(unit, useBox)) {
                    substituteFrom(unit, useBox);
                }
            }
        }

        public void track(final Unit unit) {
            if (unit instanceof final AssignStmt assignUnit) {
                track(assignUnit);
            }

            substituteUses(unit);
        }

        public void fold(final Iterable<Unit> block) {
            for (final Unit unit : block) {
                track(unit);
            }
        }

    }

}
