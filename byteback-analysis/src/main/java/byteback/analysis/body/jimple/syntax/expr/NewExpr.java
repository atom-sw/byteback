package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.type.ClassType;
import byteback.analysis.model.syntax.type.Type;

import java.util.Collections;
import java.util.List;

public class NewExpr implements Expr {

    protected final ClassType type;

    public NewExpr(final ClassType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "new " + type.toString();
    }

    public ClassType getBaseType() {
        return type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }
}
