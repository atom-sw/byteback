package byteback.syntax.scene.type.declaration.member.field.transformer.context;

import byteback.syntax.scene.type.declaration.transformer.context.ClassTransformationContext;
import byteback.syntax.scene.type.declaration.member.field.context.FieldContext;
import soot.SootField;

public class FieldTransformerContext extends FieldContext<ClassTransformationContext> {

    /**
     * Constructs a new {@link FieldContext}.
     *
     * @param classContext The outer class context.
     * @param sootField    The field in this context.
     */
    public FieldTransformerContext(final ClassTransformationContext classContext, final SootField sootField) {
        super(classContext, sootField);
    }

}
