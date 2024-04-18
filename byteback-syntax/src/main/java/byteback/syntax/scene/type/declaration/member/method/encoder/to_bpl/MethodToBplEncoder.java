package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.common.function.Lazy;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.encoder.MethodEncoder;
import byteback.syntax.tag.AnnotationReader;
import soot.ArrayType;
import soot.SootMethod;
import soot.Type;

public class MethodToBplEncoder implements MethodEncoder {

    private static final Lazy<MethodToBplEncoder> INSTANCE = Lazy.from(MethodToBplEncoder::new);

    public static MethodToBplEncoder v() {
        return INSTANCE.get();
    }

    private MethodToBplEncoder() {
    }

    public void encodeMethodName(final Printer printer, final SootMethod sootMethod) {
        printer.print(sootMethod.getDeclaringClass().getName());
        printer.print(".");
        printer.print(sootMethod.getName());

        printer.print("#");
        printer.startItems("#");
        for (final Type type : sootMethod.getParameterTypes()) {
            printer.separate();
            if (type instanceof ArrayType arrayType) {
                printer.print(arrayType.baseType.toString());
                printer.print("$");
            } else {
                printer.print(type.toString());
            }
        }
        printer.endItems();
        printer.print("#");
    }

    @Override
    public void encodeMethod(final Printer printer, final SootMethod sootMethod) {
        if (AnnotationReader.v().hasAnnotation(sootMethod, BBLibNames.BEHAVIOR_ANNOTATION)) {
            BehaviorMethodToBplEncoder.v().encodeBehaviorMethod(printer, sootMethod);
        } else {
            ProceduralMethodToBplEncoder.v().encodeProceduralMethod(printer, sootMethod);
        }
    }

}
