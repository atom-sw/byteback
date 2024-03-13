package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.visitor.SpecialExprSwitch;
import byteback.common.function.Lazy;
import byteback.analysis.model.syntax.type.Type;
import soot.VoidType;
import byteback.analysis.body.jimple.syntax.Constant;
import soot.util.Switch;

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
    public void apply(Switch visitor) {
        if (visitor instanceof SpecialExprSwitch<?> specialExprSwitch) {
            specialExprSwitch.caseVoidConstant(this);
        }
    }

    @Override
    public String toString() {
        return "@void";
    }

}
