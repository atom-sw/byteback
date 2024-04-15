package byteback.syntax.type.declaration.field.context;

import byteback.syntax.type.declaration.context.ClassContext;
import soot.Scene;
import soot.SootClass;
import soot.SootField;

/**
 * A context surrounding a single field.
 *
 * @author paganma
 */
public class FieldContext extends ClassContext {

    private final SootField sootField;

    /**
     * Constructs a new {@link FieldContext}.
     *
     * @param scene     The Scene enclosing this context.
     * @param sootClass The Class enclosing this context.
     * @param sootField The field in this context.
     */
    public FieldContext(final Scene scene, final SootClass sootClass, final SootField sootField) {
        super(scene, sootClass);
        this.sootField = sootField;
    }

    /**
     * Constructs a new {@link FieldContext}.
     *
     * @param classContext The outer class context.
     * @param sootField    The field in this context.
     */
    public FieldContext(final ClassContext classContext, final SootField sootField) {
        this(classContext.getScene(), classContext.getSootClass(), sootField);
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
