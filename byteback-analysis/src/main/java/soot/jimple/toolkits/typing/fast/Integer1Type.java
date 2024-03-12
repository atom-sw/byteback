package soot.jimple.toolkits.typing.fast;

import soot.*;

/**
 * @author Ben Bellamy
 */
public class Integer1Type extends PrimType implements IntegerType {

    public static Integer1Type v() {
        return G.v().soot_jimple_toolkits_typing_fast_Integer1Type();
    }

    public Integer1Type(Singletons.Global g) {
    }

    @Override
    public String toString() {
        return "[0..1]";
    }

    @Override
    public boolean equals(Object t) {
        return this == t;
    }

    @Override
    public boolean isAllowedInFinalCode() {
        return false;
    }

    @Override
    public String getTypeAsString() {
        return "java.lang.Integer";
    }

    @Override
    public Type getDefaultFinalType() {
        return BooleanType.v();
    }

    @Override
    public Class<?> getJavaBoxedType() {
        return Integer.class;
    }

    @Override
    public Class<?> getJavaPrimitiveType() {
        return int.class;
    }

}
