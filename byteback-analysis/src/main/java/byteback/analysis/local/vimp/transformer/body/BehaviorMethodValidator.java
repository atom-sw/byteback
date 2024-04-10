package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.common.tag.AnnotationReader;
import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.local.vimp.analyzer.value.VimpEffectEvaluator;
import byteback.analysis.local.vimp.syntax.unit.YieldStmt;
import byteback.analysis.local.vimp.tag.body.BehaviorFlagger;
import byteback.analysis.common.name.BBLibNames;
import byteback.analysis.common.tag.LocationTag;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;

import java.util.function.Supplier;


/**
 * Ensures the validity of methods annotated with @Behavior, and tags them accordingly.
 *
 * @author paganma
 */
public class BehaviorMethodValidator extends BodyTransformer {

    private static final Lazy<BehaviorMethodValidator> instance = Lazy.from(BehaviorMethodValidator::new);

    private BehaviorMethodValidator() {
    }

    public static BehaviorMethodValidator v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final SootMethod sootMethod = body.getMethod();

        if (AnnotationReader.v().hasAnnotation(sootMethod, BBLibNames.BEHAVIOR_ANNOTATION)
                || AnnotationReader.v().hasAnnotation(sootMethod, BBLibNames.BEHAVIOR_ANNOTATION)) {

            for (final Unit unit : body.getUnits()) {
                final Supplier<LocationTag> locationTagSupplier = () ->
                        LocationTag.fromHosts(sootMethod.getDeclaringClass(), sootMethod, body, unit);

                if (!(unit instanceof AssignStmt || unit instanceof YieldStmt || unit instanceof IdentityStmt)) {
                    throw new BehaviorValidationException("Invalid statement in behavior method: " + unit,
                            locationTagSupplier.get());
                }

                for (final ValueBox useBox : unit.getUseBoxes()) {
                    final Value value = useBox.getValue();

                    if (VimpEffectEvaluator.v().hasSideEffects(value)) {
                        throw new BehaviorValidationException("Impure expression in behavior method: " + value,
                                locationTagSupplier.get());
                    }
                }

                for (final ValueBox valueBox : unit.getDefBoxes()) {
                    if (VimpEffectEvaluator.v().hasSideEffects(valueBox.getValue())) {
                        throw new BehaviorValidationException("Impure assignment in behavior method: " + unit,
                                locationTagSupplier.get());
                    }
                }
            }

            BehaviorFlagger.v().flag(body);
        }
    }

}
