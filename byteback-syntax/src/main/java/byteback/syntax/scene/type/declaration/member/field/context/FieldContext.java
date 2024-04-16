package byteback.syntax.scene.type.declaration.member.field.context;

import byteback.syntax.scene.type.declaration.member.context.MemberContext;
import byteback.syntax.scene.type.declaration.context.ClassContext;
import soot.SootField;

/**
 * A context surrounding a single field.
 *
 * @author paganma
 */
public abstract class FieldContext<C extends ClassContext<?>> extends MemberContext<C> {

    private final SootField sootField;

    /**
     * Constructs a new {@link FieldContext}.
     *
     * @param classContext The outer class context.
     * @param sootField    The field in this context.
     */
    public FieldContext(final C classContext, final SootField sootField) {
        super(classContext);
        this.sootField = sootField;
    }

    /**
     * Getter for the Soot field in this context.
     *
     * @return The Soot field in this context.
     */
    public SootField getSootField() {
        return sootField;
    }

}
