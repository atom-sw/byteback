package byteback.syntax.scene.type.declaration.member.method.body.value.analyzer;

import byteback.syntax.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.NestedExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.QuantifierExpr;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.AbstractIntBinopExpr;
import soot.jimple.internal.AbstractIntLongBinopExpr;

public class VimpTypeInterpreter implements TypeInterpreter<Value> {

    private static final Lazy<VimpTypeInterpreter> INSTANCE = Lazy.from(() -> new VimpTypeInterpreter(Scene.v()));

    public static VimpTypeInterpreter v() {
        return INSTANCE.get();
    }

    private final Scene scene;

    private VimpTypeInterpreter(final Scene scene) {
        this.scene = scene;
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

    public Type join(final Type type1, final Type type2) {

        if (type1 != type2) {

            if (type1 == UnknownType.v() || type2 == UnknownType.v()) {
                return UnknownType.v();
            }

            if (type1 == VoidType.v() || type2 == VoidType.v()) {
                return VoidType.v();
            }

            if (type1 == NullType.v() || type2 == NullType.v()) {
                return NullType.v();
            }

            if (Type.toMachineType(type1) == Type.toMachineType(type2)) {
                if (typeOrder(type1) < typeOrder(type2)) {
                    return type1;
                } else {
                    return type2;
                }
            }

            return type1.merge(type2, scene);
        }

        return type1;
    }

    public Type getStrongest(final Type type1, final Type type2) {
        if (typeOrder(type1) > typeOrder(type2)) {
            return type1;
        } else {
            return type2;
        }
    }


    public Type getWeakest(final Type type1, final Type type2) {
        if (typeOrder(type1) < typeOrder(type2)) {
            return type1;
        } else {
            return type2;
        }
    }

    public Type typeOf(final Value value) {
        if (value instanceof BinopExpr binopExpr) {
            // Our interpretation of the types for binary expressions concerning int-like types is different from the
            // default in Jimple.
            // Types are merged using the `join` rule as defined above.
            if (value instanceof ConditionExpr) {
                return BooleanType.v();
            } else if (binopExpr instanceof AbstractIntBinopExpr || binopExpr instanceof AbstractIntLongBinopExpr) {
                return join(typeOf(binopExpr.getOp1()), typeOf(binopExpr.getOp2()));
            }
        } else if (value instanceof final UnopExpr unopExpr) {
            if (unopExpr instanceof NegExpr negExpr) {
                return typeOf(negExpr.getOp());
            }
        } else if (value instanceof final NestedExpr nestedExpr) {
            return typeOf(nestedExpr.getValue());
        } else if (value instanceof QuantifierExpr) {
            // Quantifier expressions are always boolean.
            return BooleanType.v();
        }

        // In all other cases, just use Jimple's default type interpretation.
        return JimpleTypeInterpreter.v().typeOf(value);
    }

}
