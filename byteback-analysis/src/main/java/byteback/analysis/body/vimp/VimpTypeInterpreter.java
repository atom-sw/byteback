package byteback.analysis.body.vimp;

import byteback.analysis.body.common.syntax.TypeInterpreter;
import byteback.analysis.body.jimple.syntax.JimpleTypeInterpreter;
import byteback.analysis.body.vimp.syntax.QuantifierExpr;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.BinopExpr;
import soot.jimple.internal.AbstractIntBinopExpr;
import soot.jimple.internal.AbstractIntLongBinopExpr;

public class VimpTypeInterpreter implements TypeInterpreter<Value> {

    private static final Lazy<VimpTypeInterpreter> instance = Lazy.from(VimpTypeInterpreter::new);

    public static VimpTypeInterpreter v() {
        return instance.get();
    }

    private VimpTypeInterpreter() {
    }

    /**
     * Relates types based on the following partially-ordered relation:
     * long > int > short > byte > boolean.
     *
     * @param type Any given type.
     * @return The corresponding position in the relation.
     */
    public int typeOrder(final Type type) {

        if (type == LongType.v()) {
            return 0;
        }

        if (type == IntType.v()) {
            return 1;
        }

        if (type == ShortType.v()) {
            return 2;
        }

        if (type == ByteType.v()) {
            return 3;
        }

        if (type == BooleanType.v()) {
            return 4;
        }

        return -1;
    }

    public Type join(final Type a, final Type b) {

        if (a != b) {

            if (a == UnknownType.v() || b == UnknownType.v()) {
                throw new RuntimeException("Unable to merge unknown type");
            }

            if (a == VoidType.v() || b == VoidType.v()) {
                return VoidType.v();
            }

            if (a == NullType.v() || b == NullType.v()) {
                return NullType.v();
            }

            if (Type.toMachineType(a) == Type.toMachineType(b)) {
                if (typeOrder(a) < typeOrder(b)) {
                    return a;
                } else {
                    return b;
                }
            }

            // TODO: Pass Scene as a constructor parameter.
            return a.merge(b, Scene.v());
        }

        return a;
    }

    public Type typeOf(final Value value) {
        if (value instanceof BinopExpr binopExpr) {
            // Our interpretation of the types for binary expressions concerning int-like types is different from the
            // default in Jimple.
            // Types are merged using the `join` rule as defined above.
            if (binopExpr instanceof AbstractIntBinopExpr || binopExpr instanceof AbstractIntLongBinopExpr) {
                return join(typeOf(binopExpr.getOp1()), typeOf(binopExpr.getOp2()));
            }
        } else if (value instanceof QuantifierExpr) {
            // Quantifier expressions are always boolean.
            return BooleanType.v();
        }

        // In all other cases, just use Jimple's default type interpretation.
        return JimpleTypeInterpreter.v().typeOf(value);
    }

}
