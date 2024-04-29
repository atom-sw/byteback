package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Type;

public class ArgumentRef extends InputRef implements DefaultCaseValue {

    private final int index;

    /**
     * Constructs a ParameterRef object of the specified type, representing the specified parameter number.
     *
     * @param paramType The type of the parameter ref.
     * @param index The corresponding position in the parameters list.
     */
    public ArgumentRef(final Type paramType, final int index) {
        super("a" + index, paramType);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof final ArgumentRef argumentRef
                && argumentRef.index == index
                && argumentRef.getType().equals(getType());
    }

    @Override
    public int equivHashCode() {
        return 31 * (index + 1) * getType().hashCode();
    }

}
