package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Type;

/**
 * Reference corresponding to the value returned by a method.
 *
 * @author paganma
 */
public class ReturnRef extends OutputRef implements DefaultCaseValue {

    public ReturnRef(final Type returnType) {
        super("return", returnType);
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof final ReturnRef returnRef
                && returnRef.getType().equals(getType());
    }

    @Override
    public int equivHashCode() {
        return 31 * getType().hashCode();
    }

}
