package byteback.syntax.scene.type.declaration.member.field.context;

import byteback.syntax.scene.type.declaration.context.ClassContext;
import byteback.syntax.scene.type.declaration.member.context.MemberContext;
import soot.SootField;

/**
 * A context surrounding a single field.
 *
 * @author paganma
 */
public class FieldContext extends MemberContext {

    /**
     * The field in this context.
     */
    private final SootField sootField;

    /**
     * Constructs a new {@link FieldContext}.
     *
     * @param classContext The outer class context.
     * @param sootField    The field in this context.
     */
    public FieldContext(final ClassContext classContext, final SootField sootField) {
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
