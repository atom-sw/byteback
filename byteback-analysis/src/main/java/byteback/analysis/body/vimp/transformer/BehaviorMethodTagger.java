package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.vimp.VimpValues;
import byteback.analysis.body.vimp.tag.BehaviorMethodTag;
import byteback.analysis.common.Hosts;
import byteback.analysis.common.namespace.BBLibNames;
import byteback.analysis.common.tag.LocationTag;
import byteback.common.function.Lazy;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.ReturnStmt;

import java.util.function.Supplier;


/**
 * Ensures the validity of methods annotated with @Behavior, and tags them accordingly.
 * @author paganma
 */
public class BehaviorMethodTagger extends BodyTransformer {

    private static final Lazy<BehaviorMethodTagger> instance = Lazy.from(BehaviorMethodTagger::new);

    private BehaviorMethodTagger() {
    }

    public static BehaviorMethodTagger v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final SootMethod sootMethod = body.getMethod();

        if (Hosts.v().hasAnnotation(sootMethod, BBLibNames.BEHAVIOR_ANNOTATION) || Hosts.v().hasAnnotation(sootMethod, BBLibNames.PREDICATE_ANNOTATION)) {
            for (final Unit unit : body.getUnits()) {
                final Supplier<LocationTag> locationTagSupplier = () ->
                        LocationTag.fromHosts(sootMethod.getDeclaringClass(), sootMethod, body, unit);

                if (!(unit instanceof AssignStmt || unit instanceof ReturnStmt || unit instanceof IdentityStmt)) {
                    throw new BehaviorValidationException("Invalid statement in behavior method: " + unit,
                            locationTagSupplier.get());
                }

                for (final ValueBox valueBox : unit.getUseBoxes()) {
                    if (VimpValues.v().hasSideEffects(valueBox.getValue())) {
                        throw new BehaviorValidationException("Impure expression in behavior method: " + unit,
                                locationTagSupplier.get());
                    }
                }

                for (final ValueBox valueBox : unit.getDefBoxes()) {
                    if (VimpValues.v().hasSideEffects(valueBox.getValue())) {
                        throw new BehaviorValidationException("Impure assignment in behavior method: " + unit,
                                locationTagSupplier.get());
                    }
                }
            }

            sootMethod.addTag(BehaviorMethodTag.v());
        }
    }

}
