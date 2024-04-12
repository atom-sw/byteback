package byteback.converter.boogie.type;

import byteback.analysis.global.vimp.tag.AxiomsProvider;
import byteback.analysis.local.vimp.syntax.value.box.ConditionExprBox;
import byteback.converter.boogie.value.ValueToBplEncoder;
import byteback.converter.common.type.ClassEncoder;
import soot.RefType;
import soot.Scene;
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
    public void transformClass(final Scene scene, final SootClass sootClass) {
        final var valueEncoder = new ValueToBplEncoder(writer);
        writeClassConstantDeclaration(sootClass);

        for (final ConditionExprBox axiomBox : AxiomsProvider.v().getOrCompute(sootClass).getValueBoxes()) {
            writer.write("axiom ");
            valueEncoder.writeValue(axiomBox.getValue());
            writer.write(";");
            writer.println();
        }
    }

}
