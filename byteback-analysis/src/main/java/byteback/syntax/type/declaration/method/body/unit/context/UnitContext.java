package byteback.syntax.type.declaration.method.body.unit.context;

import byteback.syntax.type.declaration.method.body.context.BodyContext;
import soot.Body;
import soot.UnitBox;

/**
 * A context surrounding a single unit.
 *
 * @author paganma
 */
public class UnitContext extends BodyContext {

    private final UnitBox unitBox;

    /**
     * Constructs a new {@link UnitContext}.
     *
     * @param body The body enclosing this context.
     * @param unitBox The unit box in this context.
     */
    public UnitContext(final Body body, final UnitBox unitBox) {
        super(body);
        this.unitBox = unitBox;
    }

    /**
     * Constructs a new {@link UnitContext}.
     *
     * @param bodyContext The outer body context.
     * @param unitBox The unit box in this context.
     */
    public UnitContext(final BodyContext bodyContext, final UnitBox unitBox) {
        this(bodyContext.getBody(), unitBox);
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
