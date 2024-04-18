package byteback.syntax.scene.type.declaration.encoder.to_bpl;

import byteback.common.function.Lazy;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.encoder.ClassEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl.MethodToBplEncoder;
import byteback.syntax.scene.type.declaration.tag.AxiomsProvider;
import byteback.syntax.scene.type.declaration.tag.AxiomsTag;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;

import java.util.List;

public class ClassToBplEncoder implements ClassEncoder {

    private static final Lazy<ClassToBplEncoder> INSTANCE = Lazy.from(ClassToBplEncoder::new);

    public static ClassToBplEncoder v() {
        return INSTANCE.get();
    }

    private ClassToBplEncoder() {
    }

    public void encodeAxiom(final Printer printer, final Value axiomValue) {
        printer.print("axiom ");
        ValueToBplEncoder.v().encodeValue(printer, axiomValue);
        printer.print(";");
        printer.endLine();
    }

    public void encodeAxiomsTag(final Printer printer, final AxiomsTag axiomsTag) {
        final List<Value> axiomValues = axiomsTag.getValues();

        for (final Value axiomValue : axiomValues) {
            encodeAxiom(printer, axiomValue);
        }
    }

    public void encodeClassConstant(final Printer printer, final SootClass sootClass) {
        printer.print("const unique `");
        printer.print(sootClass.getName());
        printer.print("`: Type;");
        printer.endLine();
    }

    @Override
    public void encodeClass(final Printer printer, final SootClass sootClass) {
        printer.printLine("// START OF CLASS: " + sootClass.getName());
        encodeClassConstant(printer, sootClass);
        printer.endLine();
        final AxiomsTag axiomsTag = AxiomsProvider.v().getOrCompute(sootClass);
        encodeAxiomsTag(printer, axiomsTag);
        printer.endLine();

        if (sootClass.resolvingLevel() >= SootClass.SIGNATURES) {
            for (final SootMethod sootMethod : sootClass.getMethods()) {
                MethodToBplEncoder.v().encodeMethod(printer, sootMethod);
            }
        }

        printer.printLine("// END OF CLASS: " + sootClass.getName());
        printer.endLine();
    }

}
