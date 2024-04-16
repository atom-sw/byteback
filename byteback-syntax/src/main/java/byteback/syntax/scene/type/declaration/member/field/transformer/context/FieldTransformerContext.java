package byteback.syntax.scene.type.declaration.member.field.transformer.context;

import byteback.syntax.scene.type.declaration.transformer.context.ClassTransformerContext;
import byteback.syntax.scene.type.declaration.member.field.context.FieldContext;
import soot.SootField;

public class FieldTransformerContext extends FieldContext<ClassTransformerContext> {

    /**
     * Constructs a new {@link FieldContext}.
     *
     * @param classContext The outer class context.
     * @param sootField    The field in this context.
     */
    public FieldTransformerContext(ClassTransformerContext classContext, SootField sootField) {
        super(classContext, sootField);
    }

}
