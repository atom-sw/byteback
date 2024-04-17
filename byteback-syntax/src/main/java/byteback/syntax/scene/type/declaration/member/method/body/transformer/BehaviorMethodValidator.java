package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.tag.BehaviorFlagger;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformationContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.YieldStmt;
import byteback.syntax.tag.AnnotationReader;
import byteback.syntax.transformer.TransformationException;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpEffectEvaluator;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;


/**
 * Ensures the validity of methods annotated with @Behavior, and tags them accordingly.
 *
 * @author paganma
 */
public class BehaviorMethodValidator extends BodyTransformer {

    private static final Lazy<BehaviorMethodValidator> INSTANCE = Lazy.from(BehaviorMethodValidator::new);

    private BehaviorMethodValidator() {
    }

    public static BehaviorMethodValidator v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyTransformationContext bodyContext) {
        final Body body = bodyContext.getBody();
        final SootMethod sootMethod = bodyContext.getSootMethod();

        if (AnnotationReader.v().hasAnnotation(sootMethod, BBLibNames.BEHAVIOR_ANNOTATION)) {
            for (final Unit unit : body.getUnits()) {
                if (!(unit instanceof AssignStmt || unit instanceof YieldStmt || unit instanceof IdentityStmt)) {
                    throw new TransformationException("Invalid statement in behavior method: " + unit, unit);
                }

                for (final ValueBox useBox : unit.getUseBoxes()) {
                    final Value value = useBox.getValue();

                    if (VimpEffectEvaluator.v().hasSideEffects(value)) {
                        throw new TransformationException("Impure expression in behavior method: " + value, unit);
                    }
                }

                for (final ValueBox valueBox : unit.getDefBoxes()) {
                    if (VimpEffectEvaluator.v().hasSideEffects(valueBox.getValue())) {
                        throw new TransformationException("Impure assignment in behavior method: " + unit, unit);
                    }
                }
            }

            BehaviorFlagger.v().flag(body);
        }
    }

}
