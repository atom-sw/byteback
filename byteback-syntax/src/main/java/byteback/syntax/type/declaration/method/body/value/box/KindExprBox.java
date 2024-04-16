package byteback.syntax.type.declaration.method.body.value.box;

import byteback.syntax.type.TypeType;
import soot.AbstractValueBox;
import soot.Value;

public class KindExprBox extends AbstractValueBox {

    public KindExprBox(final Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(final Value value) {
        return value.getType() == TypeType.v();
    }

}
