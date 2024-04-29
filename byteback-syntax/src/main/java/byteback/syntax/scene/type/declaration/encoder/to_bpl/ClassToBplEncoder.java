package byteback.syntax.scene.type.declaration.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.encoder.ClassEncoder;
import byteback.syntax.scene.type.declaration.member.field.encoder.to_bpl.FieldToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl.MethodToBplEncoder;
import byteback.syntax.scene.type.declaration.tag.AxiomsTagAccessor;
import byteback.syntax.scene.type.declaration.tag.AxiomsTag;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Value;

import java.util.List;
import java.util.Optional;

/**
 * Encodes a class into the Boogie Intermediate Verification Language.
 *
 * @author paganma
 */
public class ClassToBplEncoder extends ClassEncoder {

    private final ValueToBplEncoder valueEncoder;

    private final FieldToBplEncoder fieldEncoder;

    private final MethodToBplEncoder methodEncoder;

    public ClassToBplEncoder(final Printer printer) {
        super(printer);
        this.valueEncoder = new ValueToBplEncoder(printer);
        this.fieldEncoder = new FieldToBplEncoder(printer);
        this.methodEncoder = new MethodToBplEncoder(printer);
    }

    public void encodeAxiom(final Printer printer, final Value axiomValue) {
        printer.print("axiom ");
        valueEncoder.encodeValue(axiomValue);
        printer.print(";");
        printer.endLine();
    }

    public void encodeAxiomsTag(final Printer printer, final AxiomsTag axiomsTag) {
        final List<Value> axioms = axiomsTag.getAxioms();

        for (final Value axiomValue : axioms) {
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

        AxiomsTagAccessor.v().get(sootClass)
                .ifPresent(axiomsTag -> {
                    encodeAxiomsTag(printer, axiomsTag);
                    printer.endLine();
                });

        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            for (final SootField sootField : sootClass.getFields()) {
                fieldEncoder.encodeField(sootField);
            }

            for (final SootMethod sootMethod : sootClass.getMethods()) {
                methodEncoder.encodeMethod(sootMethod);
            }
        }

        printer.printLine("// END OF CLASS: " + sootClass.getName());
        printer.endLine();
    }

}
