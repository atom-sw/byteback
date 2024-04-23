package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl.ProceduralBodyToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InferredFramesTag;
import byteback.syntax.scene.type.declaration.member.method.body.tag.InferredFramesTagProvider;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.FrameConditionFinder;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.PostValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsTag;
import byteback.syntax.scene.type.declaration.member.method.tag.ParameterLocalsTagProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PostconditionsTagProvider;
import byteback.syntax.scene.type.declaration.member.method.tag.PreconditionsTagProvider;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import soot.*;

import java.util.List;

public class ProceduralMethodToBplEncoder extends MethodToBplEncoder {

    public static String SPEC_INDENT = "  ";

    private final TypeAccessToBplEncoder typeAccessToBplEncoder;

    private final ValueToBplEncoder valueToBplEncoder;

    private final PostValueToBplEncoder postValueToBplEncoder;

    private final ProceduralBodyToBplEncoder proceduralBodyToBplEncoder;

    public ProceduralMethodToBplEncoder(final Printer printer) {
        super(printer);
        this.typeAccessToBplEncoder = new TypeAccessToBplEncoder(printer);
        this.valueToBplEncoder = new ValueToBplEncoder(printer);
        this.postValueToBplEncoder = new PostValueToBplEncoder(printer);
        this.proceduralBodyToBplEncoder = new ProceduralBodyToBplEncoder(printer);
    }

    public void encodeMethod(final SootMethod sootMethod) {
        printer.print("procedure ");
        encodeMethodName(sootMethod);
        printer.print("(");
        final ParameterLocalsTag parameterLocalsTag = ParameterLocalsTagProvider.v().getOrThrow(sootMethod);
        final List<Local> methodLocals = parameterLocalsTag.getValues();
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
                    final List<Value> preconditions = preconditionsTag.getValues();

                    for (final Value value : preconditions) {
                        printer.print(SPEC_INDENT);
                        printer.print("requires ");
                        valueToBplEncoder.encodeValue(value);
                        printer.printLine(";");
                    }
                });

        PostconditionsTagProvider.v().get(sootMethod)
                .ifPresent((postconditionsTag) -> {
                    final List<Value> postconditions = postconditionsTag.getValues();

                    for (final Value value : postconditions) {
                        printer.print(SPEC_INDENT);
                        printer.print("ensures ");
                        postValueToBplEncoder.encodeValue(value);
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
