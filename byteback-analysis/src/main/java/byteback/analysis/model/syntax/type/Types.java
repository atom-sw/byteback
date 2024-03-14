package byteback.analysis.model.syntax.type;

import byteback.common.function.Lazy;

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

}
