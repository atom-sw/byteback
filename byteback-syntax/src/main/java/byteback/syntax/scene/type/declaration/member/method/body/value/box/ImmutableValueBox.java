package byteback.syntax.scene.type.declaration.member.method.body.value.box;

import soot.AbstractValueBox;
import soot.Value;

public class ImmutableValueBox extends AbstractValueBox {

    public ImmutableValueBox(final Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(final Value value) {
        return true;
    }

}
