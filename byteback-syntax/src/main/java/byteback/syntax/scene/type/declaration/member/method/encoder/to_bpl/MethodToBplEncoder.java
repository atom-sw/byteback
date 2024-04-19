package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.encoder.MethodEncoder;
import byteback.syntax.tag.AnnotationReader;
import soot.ArrayType;
import soot.SootMethod;
import soot.Type;

public class MethodToBplEncoder extends MethodEncoder {

	public MethodToBplEncoder(final Printer printer) {
		super(printer);
	}

	public void encodeMethodName(final SootMethod sootMethod) {
		printer.print("`");
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
		printer.print("`");
	}

	@Override
	public void encodeMethod(final SootMethod sootMethod) {
		if (AnnotationReader.v().hasAnnotation(sootMethod, BBLibNames.BEHAVIOR_ANNOTATION)) {
			new BehaviorMethodToBplEncoder(printer).encodeMethod(sootMethod);
		} else {
			new ProceduralMethodToBplEncoder(printer).encodeMethod(sootMethod);
		}

		printer.endLine();
	}

}
