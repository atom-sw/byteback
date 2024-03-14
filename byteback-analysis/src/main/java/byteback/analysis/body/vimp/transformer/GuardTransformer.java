package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.Body;
import byteback.analysis.body.common.syntax.Local;
import byteback.analysis.body.common.syntax.Trap;
import byteback.analysis.body.common.syntax.Unit;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.jimple.syntax.stmt.AssignStmt;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.syntax.VoidConstant;
import byteback.analysis.common.syntax.Chain;
import byteback.common.collection.ListHashMap;
import byteback.common.collection.Stacks;
import byteback.common.function.Lazy;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class GuardTransformer extends BodyTransformer {

    private static final Lazy<GuardTransformer> instance = Lazy.from(GuardTransformer::new);

    private GuardTransformer() {
    }

    public static GuardTransformer v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final Chain<Unit> units = body.getUnits();
        final Chain<Trap> traps = body.getTraps();
        final ListHashMap<Unit, Trap> startToTraps = new ListHashMap<>();
        final ListHashMap<Unit, Trap> endToTraps = new ListHashMap<>();
        final HashSet<Unit> trapHandlers = new HashSet<>();
        final Stack<Trap> activeTraps = new Stack<>();
        final Iterator<Unit> unitIterator = units.snapshotIterator();
        units.addFirst(Grimp.v().newAssignStmt(Vimp.v().newCaughtExceptionRef(), VoidConstant.v()));

        for (final Trap trap : traps) {
            startToTraps.add(trap.getBeginUnit(), trap);
            endToTraps.add(trap.getEndUnit(), trap);
            trapHandlers.add(trap.getHandlerUnit());
        }

        for (final Unit handler : trapHandlers) {
            assert handler instanceof AssignStmt assign && assign.getLeftOp() instanceof Local
                    && assign.getRightOp() instanceof CaughtExceptionRef;

            final AssignStmt newUnit = Grimp.v().newAssignStmt(Vimp.v().newCaughtExceptionRef(), VoidConstant.v());
            units.insertAfter(newUnit, handler);
        }

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();
            final List<Trap> startedTraps = startToTraps.get(unit);
            final List<Trap> endedTraps = endToTraps.get(unit);

            if (endedTraps != null) {
                Stacks.popAll(activeTraps, endToTraps.get(unit));
            }

            if (startedTraps != null) {
                Stacks.pushAll(activeTraps, startToTraps.get(unit));
            }

            if (unit instanceof ThrowStmt throwUnit) {
                final Unit retUnit = Grimp.v().newReturnVoidStmt();

                units.insertBefore(retUnit, throwUnit);
                throwUnit.redirectJumpsToThisTo(retUnit);
                units.remove(throwUnit);

                final Unit assignUnit;

                if (throwUnit.getOp() instanceof CaughtExceptionRef) {
                    assignUnit = units.getPredOf(retUnit);
                } else {
                    assignUnit = Grimp.v().newAssignStmt(Vimp.v().newCaughtExceptionRef(), throwUnit.getOp());
                    units.insertBefore(assignUnit, retUnit);
                    retUnit.redirectJumpsToThisTo(assignUnit);
                }

                Unit indexUnit = assignUnit;

                if (throwUnit.getOp().getType() instanceof RefType) {
                    for (int i = activeTraps.size() - 1; i >= 0; --i) {
                        final Trap activeTrap = activeTraps.get(i);
                        final RefType trapType = activeTrap.getException().getClassType();
                        final Value condition = Vimp.v().newInstanceOfExpr(Vimp.v().newCaughtExceptionRef(), trapType);
                        final Unit ifUnit = Vimp.v().newIfStmt(condition, activeTrap.getHandlerUnit());
                        units.insertAfter(ifUnit, indexUnit);
                        indexUnit = ifUnit;
                    }
                }
            }
        }
    }

}
