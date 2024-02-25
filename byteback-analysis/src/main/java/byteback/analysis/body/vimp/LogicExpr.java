package byteback.analysis.body.vimp;

import soot.BooleanType;
import soot.Immediate;
import soot.jimple.Expr;

public interface LogicExpr extends Expr, Immediate {

    @Override
    default BooleanType getType() {
        return BooleanType.v();
    }

}
