package byteback.syntax.type.declaration.field.context;

import byteback.syntax.type.declaration.context.ClassContext;
import soot.Scene;
import soot.SootClass;
import soot.SootField;

public class FieldContext extends ClassContext {

    private final SootField sootField;

    public FieldContext(final Scene scene, final SootClass sootClass, final SootField sootField) {
        super(scene, sootClass);
        this.sootField = sootField;
    }

    public FieldContext(final ClassContext classContext, final SootField sootField) {
        this(classContext.getScene(), classContext.getSootClass(), sootField);
    }

    public SootField getSootField() {
        return sootField;
    }

}
