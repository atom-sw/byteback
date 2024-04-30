package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.RefType;

public class ThisLocal extends InputLocal implements DefaultCaseValue {

    public ThisLocal(final RefType thisType) {
        super("this", thisType);
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof final ThisLocal instanceRef
                && instanceRef.getType().equals(getType());
    }

    @Override
    public int equivHashCode() {
        return 98533 * getType().hashCode();
    }

}
