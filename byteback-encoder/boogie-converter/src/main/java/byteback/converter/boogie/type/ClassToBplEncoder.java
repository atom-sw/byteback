package byteback.converter.boogie.type;

import byteback.syntax.type.declaration.context.ClassContext;
import byteback.syntax.type.declaration.tag.AxiomsProvider;
import byteback.syntax.type.declaration.method.body.value.box.ConditionExprBox;
import byteback.converter.boogie.value.ValueToBplEncoder;
import byteback.converter.common.type.ClassEncoder;
import soot.RefType;
import soot.SootClass;

import java.io.PrintWriter;

public class ClassToBplEncoder extends ClassEncoder {

    public ClassToBplEncoder(PrintWriter writer) {
        super(writer);
    }


    public static String makeClassConstantName(final RefType refType) {
        return "`" + refType.getClassName() + "`";
    }


    public void writeClassConstantDeclaration(final SootClass sootClass) {
        writer.println("const unique " + makeClassConstantName(sootClass.getType()) + ": Type;");
    }

    @Override
    public void transformClass(final ClassContext classContext) {
        final SootClass sootClass = classContext.getSootClass();
        final var valueEncoder = new ValueToBplEncoder(writer);
        writeClassConstantDeclaration(sootClass);

        for (final ConditionExprBox axiomBox : AxiomsProvider.v().getOrCompute(sootClass).getValues()) {
            writer.write("axiom ");
            valueEncoder.writeValue(axiomBox.getValue());
            writer.write(";");
            writer.println();
        }
    }

}
