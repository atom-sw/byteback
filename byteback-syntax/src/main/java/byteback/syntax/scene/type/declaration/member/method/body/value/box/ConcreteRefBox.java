package byteback.syntax.scene.type.declaration.member.method.body.value.box;

import soot.AbstractValueBox;
import soot.Value;
import soot.jimple.ConcreteRef;

public class ConcreteRefBox extends AbstractValueBox {

    public ConcreteRefBox(final Value value) {
        setValue(value);
    }

    @Override
    public boolean canContainValue(final Value value) {
        return value instanceof ConcreteRef;
    }

}
