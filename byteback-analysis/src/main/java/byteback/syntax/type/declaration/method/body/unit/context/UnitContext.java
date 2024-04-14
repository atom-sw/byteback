package byteback.syntax.type.declaration.method.body.unit.context;

import byteback.syntax.type.declaration.method.body.context.BodyContext;
import soot.Body;
import soot.UnitBox;

public class UnitContext extends BodyContext {

    private final UnitBox unitBox;

    public UnitContext(final Body body, final UnitBox unitBox) {
        super(body);
        this.unitBox = unitBox;
    }

    public UnitContext(final BodyContext bodyContext, final UnitBox unitBox) {
        this(bodyContext.getBody(), unitBox);
    }

    public UnitBox getUnitBox() {
        return unitBox;
    }

}
