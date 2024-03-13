package byteback.analysis.body.vimp.syntax;

import soot.BooleanType;
import byteback.analysis.model.syntax.type.Type;
import byteback.analysis.body.jimple.syntax.Expr;

public interface LogicExpr extends Expr {

    @Override
    default Type getType() {
        return BooleanType.v();
    }

}
