package byteback.analysis.model.syntax.type;

public class ArrayType extends Type {

    public final Type baseType;

    public final int dimensions;

    public ArrayType(final Type baseType, final int dimensions) {
        if (!(baseType instanceof PrimitiveType || baseType instanceof ClassType || baseType instanceof NullType)) {
            throw new IllegalArgumentException("Base type must be PrimType or RefType but not '" + baseType + "'");
        }

        if (dimensions < 1) {
            throw new IllegalArgumentException("Attempt to create array with " + dimensions + " dimensions");
        }

        this.baseType = baseType;
        this.dimensions = dimensions;
    }

    @Override
    public String toString() {

        return baseType +
                "[]".repeat(Math.max(0, dimensions));
    }

    public Type getElementType() {
        if (dimensions > 1) {
            return new ArrayType(baseType, dimensions - 1);
        } else {
            return baseType;
        }
    }
}
