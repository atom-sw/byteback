package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.OldExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpEffectEvaluator;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagFlagger;
import byteback.syntax.scene.type.declaration.member.method.tag.TwoStateTag;
import byteback.syntax.scene.type.declaration.member.method.tag.TwoStateTagFlagger;
import byteback.syntax.transformer.TransformationException;
import soot.*;
import soot.jimple.IdentityStmt;
import soot.jimple.ReturnStmt;


/**
 * Ensures the validity of methods annotated with @Behavior, and tags them accordingly.
 *
 * @author paganma
 */
public class BehaviorBodyValidator extends BodyMatchValidator {

    private static final Lazy<BehaviorBodyValidator> INSTANCE = Lazy.from(BehaviorBodyValidator::new);

    private BehaviorBodyValidator() {
    }

    public static BehaviorBodyValidator v() {
        return INSTANCE.get();
    }

    @Override
    public boolean admitsDef(final ValueContext valueContext) {
        final Value value = valueContext.getValueBox().getValue();

        return value instanceof Local;
    }

    @Override
    public boolean admitsUse(final ValueContext valueContext) {
        final Value value = valueContext.getValueBox().getValue();

        if (value instanceof OldExpr) {
            final SootMethod sootMethod = valueContext
                    .getUnitContext()
                    .getBodyContext()
                    .getSootMethod();

            return TwoStateTagFlagger.v().isTagged(sootMethod);
        } else {
            return !VimpEffectEvaluator.v().hasSideEffects(value);
        }
    }

    @Override
    public boolean admitsUnit(final UnitContext unitContext) {
        final Unit unit = unitContext.getUnitBox().getUnit();

        return unit instanceof IdentityStmt || unit instanceof ReturnStmt;
    }

    @Override
    public void transformBody(final BodyContext bodyContext) {
        final SootMethod sootMethod = bodyContext.getSootMethod();

        if (!BehaviorTagFlagger.v().isTagged(sootMethod)) {
            return;
        }

        super.transformBody(bodyContext);
    }

}
