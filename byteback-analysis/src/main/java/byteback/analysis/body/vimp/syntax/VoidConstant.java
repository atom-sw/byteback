package byteback.analysis.body.vimp.syntax;

import byteback.common.function.Lazy;
import soot.Type;
import soot.VoidType;
import soot.jimple.Constant;

/**
 * A void constant, representing the absence of a value.
 *
 * @author paganma
 */
public class VoidConstant extends Constant implements Unswitchable {

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
