package byteback.analysis.body.vimp;

import byteback.analysis.body.vimp.visitor.SpecialExprVisitor;
import byteback.common.Lazy;
import soot.Type;
import soot.VoidType;
import soot.jimple.Constant;
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
    public void apply(Switch sw) {
        if (sw instanceof SpecialExprVisitor<?> visitor) {
            visitor.caseVoidConstant(this);
        }
    }

    @Override
    public String toString() {
        return "@void";
    }

}
