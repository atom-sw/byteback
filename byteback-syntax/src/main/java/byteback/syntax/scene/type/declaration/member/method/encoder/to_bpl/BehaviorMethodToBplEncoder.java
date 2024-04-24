package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl.BehaviorBodyToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.ExceptionalTagFlagger;
import byteback.syntax.scene.type.declaration.member.method.tag.TwoStateTagFlagger;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.OperatorTagFlagger;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsTagProvider;
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
        encodeMethodName(sootMethod);
        printer.print("(");
        printer.startItems(", ");

        if (!OperatorTagFlagger.v().isTagged(sootMethod)) {
            printer.separate();
            printer.print(ValueToBplEncoder.HEAP_SYMBOL);
            printer.print(": Store");

            if (TwoStateTagFlagger.v().isTagged(sootMethod)) {
                printer.separate();
                printer.print(ValueToBplEncoder.HEAP_SYMBOL);
                printer.print(": Store");
            }

            if (ExceptionalTagFlagger.v().isTagged(sootMethod)) {
                printer.separate();
                printer.print(ValueToBplEncoder.THROWN_SYMBOL);
                printer.print(": Reference");
            }
        }

        final List<Local> parameterLocals = ParameterLocalsTagProvider.v().getOrThrow(sootMethod).getValues();
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
