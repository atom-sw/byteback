package byteback.syntax.scene.type;

import soot.Type;

public abstract class ParametricType extends Type {

    protected final Type[] typeParameters;

    public ParametricType(final Type[] typeParameters) {
        this.typeParameters = typeParameters;
    }

    public abstract String getConstructorName();

    @Override
    public String toString() {
        final var builder = new StringBuilder();
        builder.append(getConstructorName());
        builder.append("<");

        for (final Type type : typeParameters) {
            builder.append(type.toString());
        }

        builder.append(">");

        return builder.toString();
    }

}
