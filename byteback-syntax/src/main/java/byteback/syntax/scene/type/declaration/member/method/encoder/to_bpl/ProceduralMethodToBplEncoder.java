package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl.ProceduralBodyToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InferredFramesTagProvider;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.IdentityStmtsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.IdentityStmtsTagProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagProvider;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import soot.*;

import java.util.List;

public class ProceduralMethodToBplEncoder extends MethodToBplEncoder {

    public static String SPEC_INDENT = "  ";

    private final TypeAccessToBplEncoder typeAccessToBplEncoder;

    private final ValueToBplEncoder valueToBplEncoder;

    private final ProceduralBodyToBplEncoder proceduralBodyToBplEncoder;

    public ProceduralMethodToBplEncoder(final Printer printer) {
        super(printer);
        this.typeAccessToBplEncoder = new TypeAccessToBplEncoder(printer);
        this.valueToBplEncoder = new ValueToBplEncoder(printer, ValueToBplEncoder.HeapContext.PRE_STATE);
        this.proceduralBodyToBplEncoder = new ProceduralBodyToBplEncoder(printer);
    }

    public void encodeMethod(final SootMethod sootMethod) {
        printer.print("procedure ");
        encodeMethodName(sootMethod.makeRef());
        printer.print("(");
        final IdentityStmtsTag identityStmtsTag = IdentityStmtsTagProvider.v().getOrThrow(sootMethod);
        final List<Local> methodLocals = identityStmtsTag.getValues();
        printer.startItems(", ");
        valueToBplEncoder.encodeBindings(methodLocals);
        printer.endItems();
        printer.print(") returns (");
        printer.startItems(", ");
        final Type returnType = sootMethod.getReturnType();

        if (returnType != VoidType.v()) {
            printer.separate();
            printer.print(ValueToBplEncoder.RETURN_SYMBOL + ": ");
            typeAccessToBplEncoder.encodeTypeAccess(returnType);
        }

        printer.separate();
        printer.print(ValueToBplEncoder.THROWN_SYMBOL + ": Reference");
        printer.print(")");
        printer.endItems();

        if (!sootMethod.hasActiveBody()) {
            printer.print(";");
        }

        printer.endLine();

        PreconditionsTagProvider.v().get(sootMethod)
                .ifPresent((preconditionsTag) -> {
                    final List<Value> preconditions = preconditionsTag.getConditions();

                    for (final Value value : preconditions) {
                        printer.print(SPEC_INDENT);
                        printer.print("requires ");
                        new ValueToBplEncoder(printer, ValueToBplEncoder.HeapContext.PRE_STATE).encodeValue(value);
                        printer.printLine(";");
                    }
                });

        PostconditionsTagProvider.v().get(sootMethod)
                .ifPresent((postconditionsTag) -> {
                    final List<Value> postconditions = postconditionsTag.getConditions();

                    for (final Value value : postconditions) {
                        printer.print(SPEC_INDENT);
                        printer.print("ensures ");
                        new ValueToBplEncoder(printer, ValueToBplEncoder.HeapContext.POST_STATE).encodeValue(value);
                        printer.printLine(";");
                    }
                });

        if (sootMethod.hasActiveBody()) {
            final Body body = sootMethod.getActiveBody();

            if (InferredFramesTagProvider.v().isTagged(body)) {
                printer.print(SPEC_INDENT);
                printer.printLine("modifies heap;");
            }

            printer.printLine("{");
            proceduralBodyToBplEncoder.encodeBody(body);
            printer.printLine("}");
        }
    }

}
