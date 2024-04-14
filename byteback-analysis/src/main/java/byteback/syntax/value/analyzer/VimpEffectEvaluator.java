package byteback.syntax.value.analyzer;

import byteback.syntax.value.CallExpr;
import byteback.common.function.Lazy;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;

/**
 * Analyzer for checking whether a Vimp value may imply side effects.
 *
 * @author paganma
 */
public class VimpEffectEvaluator {

    private static final Lazy<VimpEffectEvaluator> instance = Lazy.from(VimpEffectEvaluator::new);

    public static VimpEffectEvaluator v() {
        return instance.get();
    }

    /**
     * @param value A Vimp value.
     * @return `true` if `value` may have side effects, false` otherwise.
     */
    public boolean hasSideEffects(final Value value) {
        return isStatefulInvoke(value)
                || value instanceof NewExpr
                || value instanceof NewArrayExpr;
    }

    /**
     * @param value A Vimp value.
     * @return `true` if `value` is an invoke expression and may have side effects, false` otherwise.
     */
    public boolean isStatefulInvoke(final Value value) {
        return value instanceof InvokeExpr && !(value instanceof CallExpr);
    }

}
