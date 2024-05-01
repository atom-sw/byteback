package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl.BehaviorBodyToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.ExceptionalTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.TwoStateTagMarker;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.OperatorTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.InputRefsTagAccessor;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import byteback.syntax.tag.AnnotationTagReader;
import soot.Body;
import soot.Local;
import soot.SootMethod;

import java.util.List;

public class BehaviorMethodToBplEncoder extends MethodToBplEncoder {

    public BehaviorMethodToBplEncoder(final Printer printer) {
        super(printer);
    }

    public void encodeMethod(final SootMethod sootMethod) {
        if (AnnotationTagReader.v().hasAnnotation(sootMethod, BBLibNames.PRELUDE_ANNOTATION)) {
            return;
        }

        printer.print("function ");
        encodeMethodName(sootMethod.makeRef());
        printer.print("(");
        printer.startItems(", ");

        if (!OperatorTagMarker.v().hasTag(sootMethod)) {
            printer.separate();
            printer.print(ValueToBplEncoder.HEAP_SYMBOL);
            printer.print(": Store");

            if (TwoStateTagMarker.v().hasTag(sootMethod)) {
                printer.separate();
                printer.print(ValueToBplEncoder.OLD_HEAP_SYMBOL);
                printer.print(": Store");
            }

            if (ExceptionalTagMarker.v().hasTag(sootMethod)) {
                printer.separate();
                printer.print(ValueToBplEncoder.THROWN_SYMBOL);
                printer.print(": Reference");
            }
        }

        final List<Local> parameterLocals = InputRefsTagAccessor.v().getOrThrow(sootMethod).getInputLocals();
        new ValueToBplEncoder(printer, ValueToBplEncoder.HeapContext.PRE_STATE).encodeBindings(parameterLocals);
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
