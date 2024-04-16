package byteback.syntax.scene.type.declaration.member.method.body.value.context;

import byteback.syntax.context.Context;
import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import soot.ValueBox;

public class ValueContext<U extends UnitContext<?>> implements Context {

    private final U unitContext;

    private final ValueBox valueBox;

    public ValueContext(final U unitContext, final ValueBox valueBox) {
        this.unitContext = unitContext;
        this.valueBox = valueBox;
    }

    public U getUnitContext() {
        return unitContext;
    }

    public ValueBox getValueBox() {
        return valueBox;
    }

}
