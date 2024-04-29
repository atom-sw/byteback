package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.transformer.TransformationException;
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

    private static final Lazy<SwitchEliminator> INSTANCE = Lazy.from(SwitchEliminator::new);

    public static SwitchEliminator v() {
        return INSTANCE.get();
    }

    private SwitchEliminator() {
    }

    private PatchingChain<Unit> expandSwitchStmt(final Value base, final Unit defaultTarget,
                                                 final Iterator<Map.Entry<IntConstant, Unit>> valueToTargetIterator) {

        final var units = new UnitPatchingChain(new HashChain<>());

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
    public void transformBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof final SwitchStmt switchStmt) {
                final Stream<Map.Entry<IntConstant, Unit>> valueToTarget;

                if (unit instanceof final LookupSwitchStmt lookupSwitchStmt) {
                    valueToTarget = IntStream
                            .range(0, lookupSwitchStmt.getTargetCount())
                            .mapToObj((index) ->
                                    new AbstractMap.SimpleEntry<>(
                                            IntConstant.v(lookupSwitchStmt.getLookupValue(index)),
                                            lookupSwitchStmt.getTarget(index)
                                    ));
                } else if (unit instanceof final TableSwitchStmt tableSwitchStmt) {
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
