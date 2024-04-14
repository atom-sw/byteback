package byteback.syntax.value.context;

import byteback.syntax.type.declaration.method.body.unit.context.UnitContext;
import soot.Body;
import soot.UnitBox;
import soot.ValueBox;

public class ValueContext extends UnitContext {

    private final ValueBox valueBox;

    public ValueContext(final Body body, final UnitBox unitBox, final ValueBox valueBox) {
        super(body, unitBox);
        this.valueBox = valueBox;
    }

    public ValueContext(final UnitContext unitContext, final ValueBox valueBox) {
        this(unitContext.getBody(), unitContext.getUnitBox(), valueBox);
    }

    public ValueBox getValueBox() {
        return valueBox;
    }

}
