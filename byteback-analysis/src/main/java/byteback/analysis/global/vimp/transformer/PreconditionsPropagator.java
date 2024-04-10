package byteback.analysis.global.vimp.transformer;

import byteback.analysis.global.vimp.tag.PreconditionsProvider;
import byteback.analysis.global.vimp.tag.PreconditionsTag;
import byteback.analysis.local.vimp.syntax.value.box.ConditionExprBox;
import byteback.common.function.Lazy;
import soot.Value;
import soot.jimple.Jimple;
import soot.jimple.OrExpr;

import java.util.List;

public class PreconditionsPropagator extends ConditionsPropagator<PreconditionsTag> {

    private static final Lazy<PreconditionsPropagator> instance = Lazy.from(() ->
            new PreconditionsPropagator(PreconditionsProvider.v()));

    public static PreconditionsPropagator v() {
        return instance.get();
    }

    public PreconditionsPropagator(PreconditionsProvider conditionsProvider) {
        super(conditionsProvider);
    }

    @Override
    public void combineConditions(final List<ConditionExprBox> originalPreconditionBoxes,
                                  final List<ConditionExprBox> overridingPreconditionBoxes) {
        for (final ConditionExprBox originalConditionBox : originalPreconditionBoxes) {
            for (final ConditionExprBox overridingConditionBox : overridingPreconditionBoxes) {
                final Value originalCondition = originalConditionBox.getValue();
                final Value overridingCondition = overridingConditionBox.getValue();
                final OrExpr weakenedCondition = Jimple.v().newOrExpr(originalCondition, overridingCondition);
                overridingConditionBox.setValue(weakenedCondition);
            }
        }
    }

}
