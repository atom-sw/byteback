package byteback.analysis.body.vimp.syntax;

import soot.BooleanType;
import soot.Immediate;
import soot.Type;
import soot.jimple.Expr;

public interface LogicExpr extends Expr {

    @Override
    default Type getType() {
        return BooleanType.v();
    }

}
