package byteback.analysis.body.vimp.syntax;

import soot.BooleanType;
import soot.Type;
import soot.jimple.Expr;

/**
 * Base class for a logic expression. A logic expression can exclusively be of boolean type.
 *
 * @author paganma
 */
public interface LogicExpr extends Expr, Unswitchable {

    @Override
    default Type getType() {
        return BooleanType.v();
    }

}
