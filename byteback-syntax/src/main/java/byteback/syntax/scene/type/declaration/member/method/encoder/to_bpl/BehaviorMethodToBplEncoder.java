package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl.BehaviorBodyToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.tag.ExceptionalFlagger;
import byteback.syntax.scene.type.declaration.member.method.body.tag.TwoStateFlagger;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.OldValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.OperatorFlagger;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsProvider;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import byteback.syntax.tag.AnnotationReader;
import soot.Body;
import soot.Local;
import soot.SootMethod;

import java.util.List;

public class BehaviorMethodToBplEncoder extends MethodToBplEncoder {


    public BehaviorMethodToBplEncoder(final Printer printer) {
        super(printer);
    }

    public void encodeMethod(final SootMethod sootMethod) {
        if (AnnotationReader.v().hasAnnotation(sootMethod, BBLibNames.PRELUDE_ANNOTATION)) {
            return;
        }

        printer.print("function ");
        encodeMethodName(sootMethod);
        printer.print("(");
        printer.startItems(", ");

        if (!OperatorFlagger.v().isTagged(sootMethod)) {
            printer.separate();
            printer.print(ValueToBplEncoder.HEAP_SYMBOL);
            printer.print(": Store");

            if (TwoStateFlagger.v().isTagged(sootMethod)) {
                printer.separate();
                printer.print(OldValueToBplEncoder.OLD_HEAP_SYMBOL);
                printer.print(": Store");
            }

            if (ExceptionalFlagger.v().isTagged(sootMethod)) {
                printer.separate();
                printer.print(ValueToBplEncoder.THROWN_SYMBOL);
                printer.print(": Reference");
            }
        }

        final List<Local> parameterLocals = ParameterLocalsProvider.v().getOrCompute(sootMethod).getValues();
        new ValueToBplEncoder(printer).encodeBindings(parameterLocals);
        printer.endItems();

        printer.print(") returns (");
        new TypeAccessToBplEncoder(printer).encodeTypeAccess(sootMethod.getReturnType());
        printer.print(")");

        if (sootMethod.hasActiveBody()) {
            final Body body = sootMethod.getActiveBody();
            printer.endLine();
            printer.print("{ ");
            new BehaviorBodyToBplEncoder(printer).encodeBody(body);
            printer.print(" }");
        } else {
            printer.print(";");
        }

        printer.endLine();
    }

}
