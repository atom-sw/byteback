package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.jimple.syntax.expr.Expr;
import byteback.analysis.model.syntax.type.BooleanType;
import byteback.analysis.model.syntax.type.Type;

public interface LogicExpr extends Expr {

    @Override
    default Type getType() {
        return BooleanType.v();
    }

}
