package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.common.function.Lazy;
import byteback.syntax.encoder.Encoder;
import byteback.syntax.name.BBLibNames;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl.BehaviorBodyToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.tag.TwoStateFlagger;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsProvider;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import byteback.syntax.tag.AnnotationReader;
import soot.Local;
import soot.SootMethod;

import java.util.List;

public class BehaviorMethodToBplEncoder implements Encoder {

    private static final Lazy<BehaviorMethodToBplEncoder> INSTANCE = Lazy.from(BehaviorMethodToBplEncoder::new);

    public static BehaviorMethodToBplEncoder v() {
        return INSTANCE.get();
    }

    private BehaviorMethodToBplEncoder() {
    }

    public void encodeBehaviorMethod(final Printer printer, final SootMethod sootMethod) {
        if (!AnnotationReader.v().hasAnnotation(sootMethod, BBLibNames.PRELUDE_ANNOTATION)) {
            printer.print("function `");
            MethodToBplEncoder.v().encodeMethodName(printer, sootMethod);
            printer.print("`(");

            if (!AnnotationReader.v().hasAnnotation(sootMethod, BBLibNames.PRIMITIVE_ANNOTATION)) {
                printer.print("h: Store, ");

                if (TwoStateFlagger.v().isTagged(sootMethod.getActiveBody())) {
                    printer.print("h': Store, ");
                }
            }

            final List<Local> parameterLocals = ParameterLocalsProvider.v().getOrCompute(sootMethod).getValues();
            ValueToBplEncoder.v().encodeBindings(printer, parameterLocals);

            printer.print(") returns (");
            TypeAccessToBplEncoder.v().encodeTypeAccess(printer, sootMethod.getReturnType());
            printer.print(")");
            printer.endLine();

            printer.print("{ ");
            if (sootMethod.hasActiveBody()) {
                BehaviorBodyToBplEncoder.v().encodeBody(printer, sootMethod.getActiveBody());
            }
            printer.print(" }");

            printer.endLine();
        }
    }

}
