package byteback.analysis.global.vimp.syntax.value.box;

import byteback.analysis.global.vimp.syntax.type.TypeType;
import soot.AbstractValueBox;
import soot.Value;

public class TypeExprBox extends AbstractValueBox {

    public TypeExprBox(final Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(final Value value) {
        return value.getType() == TypeType.v();
    }

}
