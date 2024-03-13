package byteback.analysis.model.source;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import byteback.analysis.model.syntax.FieldModel;

/**
 * Soot field builder.
 *
 * @author Aaloan Miftah
 */
final class FieldModelBuilder extends FieldVisitor {

    private final FieldModel fieldModel;

    private final ClassModelBuilder classModelBuilder;

    FieldModelBuilder(FieldModel fieldModel, ClassModelBuilder classModelBuilder) {
        super(Opcodes.ASM5);
        this.fieldModel = fieldModel;
        this.classModelBuilder = classModelBuilder;
    }
}