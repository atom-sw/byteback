package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.common.function.Lazy;
import soot.*;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;

import java.util.Iterator;
import java.util.List;

public class IfConditionExtractor extends BodyTransformer {

    private static final Lazy<IfConditionExtractor> instance = Lazy.from(IfConditionExtractor::new);

    public static IfConditionExtractor v() {
        return instance.get();
    }

    private IfConditionExtractor() {
    }

    @Override
    public void transformBody(final Body body) {
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();
        final var localGenerator = new DefaultLocalGenerator(body);

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof IfStmt ifStmt) {
                final Local local = localGenerator.generateLocal(BooleanType.v());
                final AssignStmt conditionAssignStmt = Jimple.v().newAssignStmt(local, ifStmt.getCondition());
                final IfStmt newIfStmt = Vimp.v().newIfStmt(local, ifStmt.getTarget());
                units.insertAfter(List.of(conditionAssignStmt, newIfStmt), ifStmt);
                units.remove(ifStmt);
            }
        }
    }

}
