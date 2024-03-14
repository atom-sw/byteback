package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.jimple.syntax.expr.Constant;
import byteback.analysis.model.syntax.type.VoidType;
import byteback.common.function.Lazy;
import byteback.analysis.model.syntax.type.Type;

public class VoidConstant extends Constant {

    private static final Lazy<VoidConstant> instance = Lazy.from(VoidConstant::new);

    private VoidConstant() {
    }

    public static VoidConstant v() {
        return instance.get();
    }

    @Override
    public Type getType() {
        return VoidType.v();
    }

    @Override
    public String toString() {
        return "@void";
    }

}
