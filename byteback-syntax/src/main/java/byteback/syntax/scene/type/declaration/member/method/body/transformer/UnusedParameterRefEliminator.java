package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.ParameterLocal;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import soot.*;
import soot.jimple.AssignStmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;

import java.util.Iterator;

public class UnusedParameterRefEliminator extends BodyTransformer {

    private static final Lazy<UnusedParameterRefEliminator> INSTANCE = Lazy.from(UnusedParameterRefEliminator::new);

    public static UnusedParameterRefEliminator v() {
        return INSTANCE.get();
    }

    private UnusedParameterRefEliminator() {
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final SootMethod sootMethod = bodyContext.getSootMethod();

        if (!BehaviorTagMarker.v().hasTag(sootMethod)) {
            return;
        }

        final Body body = bodyContext.getBody();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();
        final var unitGraph = new BriefUnitGraph(body);
        final var localDefs = new SimpleLocalDefs(unitGraph);
        final var localUses = new SimpleLocalUses(unitGraph, localDefs);

        NEXT_UNIT:
        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof final AssignStmt assignStmt
                    && assignStmt.getLeftOp() instanceof final Local local
                    && assignStmt.getRightOp() instanceof ParameterLocal) {
                for (final Unit defUnit : localDefs.getDefsOf(local)) {
                    if (!localUses.getUsesOf(defUnit).isEmpty()) {
                        continue NEXT_UNIT;
                    }
                }

                units.remove(unit);
            }
        }
    }

}
