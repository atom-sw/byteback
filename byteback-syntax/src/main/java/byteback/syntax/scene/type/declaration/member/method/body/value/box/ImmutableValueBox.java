package byteback.syntax.scene.type.declaration.member.method.body.value.box;

import soot.AbstractValueBox;
import soot.Value;

public class ImmutableValueBox extends AbstractValueBox {

    public ImmutableValueBox(final Value value) {
        this.value = value;
    }

    @Override
    public boolean canContainValue(final Value value) {
        return false;
    }

    @Override
    public void setValue(final Value value) {
        throw new UnsupportedOperationException();
    }

}
