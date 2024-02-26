package byteback.analysis.body.vimp;

import soot.BooleanType;
import soot.Immediate;
import soot.Type;
import soot.jimple.Expr;

public interface LogicExpr extends Expr, Immediate {

    @Override
    default Type getType() {
        return BooleanType.v();
    }

}
