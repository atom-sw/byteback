package byteback.analysis.body.vimp;

import byteback.analysis.body.vimp.syntax.CallExpr;
import byteback.common.function.Lazy;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;

/**
 * Additional functions that operate on Soot Values.
 *
 * @author paganma
 */
public class VimpValues {

    private static final Lazy<VimpValues> instance = Lazy.from(VimpValues::new);

    public static VimpValues v() {
        return instance.get();
    }

    /**
     * @param value A Soot value.
     * @return `true` if `value` may have side effects, false` otherwise.
     */
    public boolean hasSideEffects(final Value value) {
        return isStatefulInvoke(value)
                || value instanceof NewExpr
                || value instanceof NewArrayExpr;
    }

    /**
     * @param value A Soot value.
     * @return `true` if `value` is an invoke expression and may have side effects, false` otherwise.
     */
    public boolean isStatefulInvoke(final Value value) {
        return value instanceof InvokeExpr && !(value instanceof CallExpr);
    }

}
