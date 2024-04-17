package byteback.syntax.scene.type.declaration.member.method.body.value.context;

import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import soot.Context;
import soot.ValueBox;

public class ValueContext implements Context {

    private final UnitContext unitContext;

    private final ValueBox valueBox;

    public ValueContext(final UnitContext unitContext, final ValueBox valueBox) {
        this.unitContext = unitContext;
        this.valueBox = valueBox;
    }

    public UnitContext getUnitContext() {
        return unitContext;
    }

    public ValueBox getValueBox() {
        return valueBox;
    }

}
