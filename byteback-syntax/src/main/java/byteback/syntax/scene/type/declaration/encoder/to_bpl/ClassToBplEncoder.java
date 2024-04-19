package byteback.syntax.scene.type.declaration.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.encoder.ClassEncoder;
import byteback.syntax.scene.type.declaration.member.field.transformer.encoder.to_bpl.FieldToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl.MethodToBplEncoder;
import byteback.syntax.scene.type.declaration.tag.AxiomsProvider;
import byteback.syntax.scene.type.declaration.tag.AxiomsTag;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Value;

import java.util.List;

public class ClassToBplEncoder extends ClassEncoder {

    public ClassToBplEncoder(final Printer printer) {
			super(printer);
    }

    public void encodeAxiom(final Printer printer, final Value axiomValue) {
        printer.print("axiom ");
        new ValueToBplEncoder(printer).encodeValue(axiomValue);
        printer.print(";");
        printer.endLine();
    }

    public void encodeAxiomsTag(final Printer printer, final AxiomsTag axiomsTag) {
        final List<Value> axiomValues = axiomsTag.getValues();

        for (final Value axiomValue : axiomValues) {
            encodeAxiom(printer, axiomValue);
        }
    }

    public void encodeClassConstant(final SootClass sootClass) {
        printer.print("`");
        printer.print(sootClass.getName());
        printer.print("`");
    }

    public void encodeClassConstantDeclaration(final SootClass sootClass) {
        printer.print("const unique ");
        encodeClassConstant(sootClass);
        printer.print(": Type;");
        printer.endLine();
    }

    @Override
    public void encodeClass(final SootClass sootClass) {
        printer.printLine("// START OF CLASS: " + sootClass.getName());
        encodeClassConstantDeclaration(sootClass);
        printer.endLine();
        final AxiomsTag axiomsTag = AxiomsProvider.v().getOrCompute(sootClass);
        encodeAxiomsTag(printer, axiomsTag);
        printer.endLine();

        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            for (final SootField sootField : sootClass.getFields()) {
                new FieldToBplEncoder(printer).encodeField(sootField);
            }

            for (final SootMethod sootMethod : sootClass.getMethods()) {
                new MethodToBplEncoder(printer).encodeMethod(sootMethod);
            }
        }

        printer.printLine("// END OF CLASS: " + sootClass.getName());
        printer.endLine();
    }

}
