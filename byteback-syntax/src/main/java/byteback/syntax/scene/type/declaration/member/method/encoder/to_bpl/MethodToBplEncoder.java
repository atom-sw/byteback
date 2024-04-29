package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.encoder.MethodEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import byteback.syntax.tag.AnnotationTagReader;
import soot.ArrayType;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;

import java.util.ArrayList;

public class MethodToBplEncoder extends MethodEncoder {

    public MethodToBplEncoder(final Printer printer) {
        super(printer);
    }

    public void encodeMethodName(final SootMethodRef sootMethodRef) {
        printer.print("`");
        printer.print(sootMethodRef.getDeclaringClass().getName());
        printer.print(".");
        final String methodName = sootMethodRef.getName()
                .replace("<", "_LT_")
                .replace(">", "_GT_");
        printer.print(methodName);
        printer.print("#");
        printer.startItems("#");
        final var signatureTypes = new ArrayList<Type>();
        signatureTypes.add(sootMethodRef.getReturnType());
        signatureTypes.addAll(sootMethodRef.getParameterTypes());

        for (final Type type : signatureTypes) {
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
        printer.print("`");
    }

    @Override
    public void encodeMethod(final SootMethod sootMethod) {
        if (BehaviorTagMarker.v().hasTag(sootMethod)) {
            new BehaviorMethodToBplEncoder(printer).encodeMethod(sootMethod);
        } else {
            new ProceduralMethodToBplEncoder(printer).encodeMethod(sootMethod);
        }

        printer.endLine();
    }

}
