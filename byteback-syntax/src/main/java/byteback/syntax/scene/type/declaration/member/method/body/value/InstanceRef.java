package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.RefType;

public class InstanceRef extends InputRef implements DefaultCaseValue {

    public InstanceRef(final RefType thisType) {
        super("this", thisType);
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof final InstanceRef instanceRef
                && instanceRef.getType().equals(getType());
    }

    @Override
    public int equivHashCode() {
        return 98533 * getType().hashCode();
    }

}
