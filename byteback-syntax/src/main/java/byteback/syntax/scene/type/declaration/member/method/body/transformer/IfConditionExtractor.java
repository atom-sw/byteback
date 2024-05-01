package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import soot.*;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;

import java.util.Iterator;
import java.util.List;

public class IfConditionExtractor extends BodyTransformer {

    private static final Lazy<IfConditionExtractor> INSTANCE = Lazy.from(IfConditionExtractor::new);

    public static IfConditionExtractor v() {
        return INSTANCE.get();
    }

    private IfConditionExtractor() {
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final Body body = bodyContext.getBody();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();
        final var localGenerator = new DefaultLocalGenerator(body);

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof final IfStmt ifStmt) {
                final Local local = localGenerator.generateLocal(BooleanType.v());
                final Value condition = ifStmt.getCondition();
                final AssignStmt conditionAssignStmt = Jimple.v().newAssignStmt(local, condition);
                final Unit thenBranch = ifStmt.getTarget();
                final IfStmt newIfStmt = Vimp.v().newIfStmt(local, thenBranch);
                units.insertAfter(List.of(conditionAssignStmt, newIfStmt), ifStmt);
                units.remove(ifStmt);
            }
        }
    }

}
