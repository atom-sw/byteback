package byteback.analysis.body.vimp.syntax;

import soot.jimple.Expr;
import soot.jimple.ExprSwitch;
import soot.util.Switch;

/**
 * An expression whose apply method always calls the default method of the given visitor.
 * We use this class to keep compatibility to Soot's analysis that may use visitors to navigate Vimp expressions.
 * Within ByteBack the preferred approach to achieve this is by using pattern matching for `instanceof`.
 *
 * @author paganma
 */
public interface UnswitchableExpr extends Expr {

    default void apply(final Switch visitor) {
        if (visitor instanceof ExprSwitch exprSwitch) {
            exprSwitch.defaultCase(this);
        }
    }

}
