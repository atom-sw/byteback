package byteback.analysis.scene;

import byteback.common.function.Lazy;
import soot.*;

public class Types {

    private static final Lazy<Types> instance = Lazy.from(Types::new);

    public static Types v() {
        return instance.get();
    }

    private Types() {
    }

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

            return a.merge(b, Scene.v());
        }

        return a;
    }

}
