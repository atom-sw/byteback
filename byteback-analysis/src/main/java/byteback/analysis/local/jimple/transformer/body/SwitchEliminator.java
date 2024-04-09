package byteback.analysis.local.jimple.transformer.body;

import byteback.analysis.common.transformer.TransformationException;
import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.*;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SwitchEliminator extends BodyTransformer {

    private static final Lazy<SwitchEliminator> instance = Lazy.from(SwitchEliminator::new);

    public static SwitchEliminator v() {
        return instance.get();
    }

    private SwitchEliminator() {
    }

    private PatchingChain<Unit> expandSwitchStmt(final Value base, final Unit defaultTarget,
                                                 final Iterator<Map.Entry<IntConstant, Unit>> valueToTargetIterator) {
        final var units = new UnitPatchingChain(new HashChain<>());

        // TODO Consider merging cases for consecutive value ranges.
        while (valueToTargetIterator.hasNext()) {
            final Map.Entry<IntConstant, Unit> valueToTargetEntry = valueToTargetIterator.next();
            final Value value = valueToTargetEntry.getKey();
            final Unit target = valueToTargetEntry.getValue();
            final EqExpr conditionExpr = Jimple.v().newEqExpr(base, value);
            final IfStmt ifStmt = Jimple.v().newIfStmt(conditionExpr, target);
            units.addLast(ifStmt);
        }

        final GotoStmt defaultStmt = Jimple.v().newGotoStmt(defaultTarget);
        units.addLast(defaultStmt);

        return units;
    }

    @Override
    public void transformBody(final Body body) {
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();

        while ((unitIterator.hasNext())) {
            final Unit unit = unitIterator.next();

            if (unit instanceof SwitchStmt switchStmt) {
                final Stream<Map.Entry<IntConstant, Unit>> valueToTarget;

                if (unit instanceof LookupSwitchStmt lookupSwitchStmt) {
                    valueToTarget = IntStream
                            .range(0, lookupSwitchStmt.getTargetCount())
                            .mapToObj((index) ->
                                    new AbstractMap.SimpleEntry<>(
                                            IntConstant.v(lookupSwitchStmt.getLookupValue(index)),
                                            lookupSwitchStmt.getTarget(index)
                                    ));
                } else if (unit instanceof TableSwitchStmt tableSwitchStmt) {
                    valueToTarget = IntStream
                            .rangeClosed(tableSwitchStmt.getLowIndex(), tableSwitchStmt.getHighIndex())
                            .mapToObj((index) ->
                                    new AbstractMap.SimpleEntry<>(
                                            IntConstant.v(index),
                                            tableSwitchStmt.getTarget(index - 1)
                                    )
                            );
                } else {
                    throw new TransformationException("Unrecognized switch statement.", unit);
                }

                final Chain<Unit> expandedSwitch =
                        expandSwitchStmt(switchStmt.getKey(), switchStmt.getDefaultTarget(), valueToTarget.iterator());

                units.insertBefore(expandedSwitch, switchStmt);
                units.remove(switchStmt);
            }
        }
    }

}
