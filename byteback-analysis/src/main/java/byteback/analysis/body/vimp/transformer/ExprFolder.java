package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.Body;
import byteback.analysis.body.common.syntax.Immediate;
import byteback.analysis.body.common.syntax.Local;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.VimpValues;
import byteback.common.collection.SetHashMap;
import byteback.analysis.body.jimple.syntax.AssignStmt;
import byteback.analysis.body.jimple.syntax.Ref;
import soot.jimple.toolkits.infoflow.CachedEquivalentValue;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Set;

public abstract class ExprFolder extends BodyTransformer {

    @Override
    public void transformBody(final Body body) {
        final Chain<byteback.analysis.body.jimple.syntax.Unit> units = body.getUnits();
        final BlockGraph blockGraph = new BriefBlockGraph(body);
        final UnitGraph unitGraph = new BriefUnitGraph(body);
        final LocalDefs localDefs = new SimpleLocalDefs(unitGraph);
        final LocalUses localUses = new SimpleLocalUses(unitGraph, localDefs);

        for (final Block block : blockGraph) {
            final var blockSnapshot = new HashChain<byteback.analysis.body.jimple.syntax.Unit>();
            final var blockFolder = new BlockFolder(units, localDefs, localUses);

            for (final byteback.analysis.body.jimple.syntax.Unit unit : block) {
                blockSnapshot.add(unit);
            }

            blockFolder.fold(blockSnapshot);
        }
    }

    public boolean canSubstitute(final byteback.analysis.body.jimple.syntax.Unit unit, final ValueBox valueBox) {
        return true;
    }

    public class BlockFolder {

        protected final HashMap<Local, AssignStmt> localToSubstitution;

        protected final SetHashMap<Value, Local> dependencyToLocals;

        protected final Chain<byteback.analysis.body.jimple.syntax.Unit> units;

        protected final LocalDefs localDefs;

        protected final LocalUses localUses;

        public BlockFolder(final Chain<byteback.analysis.body.jimple.syntax.Unit> units, final LocalDefs localDefs, final LocalUses localUses) {
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
                final Set<Local> dependentLocals = dependencyToLocals.get(new CachedEquivalentValue(dependency));

                if (dependentLocals != null) {
                    for (final Local dependentLocal : dependentLocals) {
                        localToSubstitution.remove(dependentLocal);
                    }
                }

                dependencyToLocals.remove(dependency);
            }
        }

        public void substituteFrom(final byteback.analysis.body.jimple.syntax.Unit unit, final ValueBox startingValueBox) {
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
                            valueBox.setValue(Vimp.v().newNestedExpr(substitution));
                        }

                        units.remove(substitution);
                    }
                }
            }
        }

        public void substituteUses(final byteback.analysis.body.jimple.syntax.Unit unit) {
            for (final ValueBox useBox : unit.getUseBoxes()) {
                if (canSubstitute(unit, useBox)) {
                    substituteFrom(unit, useBox);
                }
            }
        }

        public void track(final byteback.analysis.body.jimple.syntax.Unit unit) {
            if (unit instanceof final AssignStmt assignUnit) {
                track(assignUnit);
            }

            substituteUses(unit);
        }

        public void fold(final Iterable<byteback.analysis.body.jimple.syntax.Unit> block) {
            for (final Unit unit : block) {
                track(unit);
            }
        }

    }

}
