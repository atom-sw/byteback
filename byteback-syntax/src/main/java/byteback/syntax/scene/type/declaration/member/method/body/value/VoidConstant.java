package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.common.function.Lazy;
import soot.Type;
import soot.VoidType;
import soot.jimple.Constant;

/**
 * A void constant, representing the absence of a value.
 * > For when `null` is not enough
 * In practice we only use this value as a placeholder to be assigned to @caughtexception when there is no exception.
 * The rationale for not using a null constant for representing the same condition is that a thrown exception is a
 * reference, and null itself is also a reference. In practice though throwing `null` will always result in a
 * NullPointerException.
 *
 * @author paganma
 */
public class VoidConstant extends Constant implements DefaultCaseValue {

    private static final Lazy<VoidConstant> INSTANCE = Lazy.from(VoidConstant::new);

    private VoidConstant() {
    }

    public static VoidConstant v() {
        return INSTANCE.get();
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
