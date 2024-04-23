package byteback.syntax.scene.type.declaration.member.method.body.unit.context;

import byteback.syntax.context.Context;
import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import soot.UnitBox;

/**
 * A context surrounding a single unit.
 *
 * @author paganma
 */
public class UnitContext implements Context {

    private final BodyContext bodyContext;

    private final UnitBox unitBox;

    /**
     * Constructs a new {@link UnitContext}.
     *
     * @param bodyContext The outer body context.
     * @param unitBox     The unit box in this context.
     */
    public UnitContext(final BodyContext bodyContext, final UnitBox unitBox) {
        this.bodyContext = bodyContext;
        this.unitBox = unitBox;
    }

    public BodyContext getBodyContext() {
        return bodyContext;
    }

    /**
     * Getter for the unit box in this context.
     *
     * @return The unit box in this context.
     */
    public UnitBox getUnitBox() {
        return unitBox;
    }

}
