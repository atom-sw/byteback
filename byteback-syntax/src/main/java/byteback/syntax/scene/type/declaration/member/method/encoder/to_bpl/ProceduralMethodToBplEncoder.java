package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl.ProceduralBodyToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.PostValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsProvider;
import byteback.syntax.scene.type.encoder.TypeAccessEncoder;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import soot.*;

import java.util.List;

public class ProceduralMethodToBplEncoder extends MethodToBplEncoder {

    public ProceduralMethodToBplEncoder(final Printer printer) {
			super(printer);
    }

    public void encodeMethod(final SootMethod sootMethod) {
        printer.print("procedure ");
        encodeMethodName(sootMethod);
        printer.print("(");
        final List<Local> methodBindings = ParameterLocalsProvider.v().getOrCompute(sootMethod).getValues();
        printer.startItems(", ");
        new ValueToBplEncoder(printer).encodeBindings(methodBindings);
        printer.endItems();
        printer.print(") returns (");
        printer.startItems(", ");
        final Type returnType = sootMethod.getReturnType();

        if (returnType != VoidType.v()) {
            printer.separate();
            printer.print("`#return`: ");
            new TypeAccessToBplEncoder(printer).encodeTypeAccess(returnType);
        }

        printer.separate();
        printer.print("`#thrown`: Reference");
        printer.print(")");
        printer.endItems();
        printer.endLine();

        for (final Value value : PreconditionsProvider.v().getOrCompute(sootMethod).getValues()) {
            printer.print("  requires ");
            new ValueToBplEncoder(printer).encodeValue(value);
            printer.print(";");
            printer.endLine();
        }

        for (final Value value : PostconditionsProvider.v().getOrCompute(sootMethod).getValues()) {
            printer.print("  ensures ");
            new PostValueToBplEncoder(printer).encodeValue(value);
            printer.print(";");
            printer.endLine();
        }

        if (sootMethod.hasActiveBody()) {
            printer.print("{");
            new ProceduralBodyToBplEncoder(printer).encodeBody(sootMethod.getActiveBody());
            printer.print("}");
        } else {
            printer.print(";");
        }
    }

}
