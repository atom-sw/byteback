package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.encoder.MethodEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.BehaviorTagMarker;
import soot.ArrayType;
import soot.SootMethod;
import soot.Type;

import java.util.ArrayList;

public class MethodToBplEncoder extends MethodEncoder {

	public MethodToBplEncoder(final Printer printer) {
		super(printer);
	}

	public void encodeMethodName(final SootMethod sootMethod) {
		printer.print("`");
		printer.print(sootMethod.getDeclaringClass().getName());
		printer.print(".");
		final String methodName = sootMethod.getName()
				.replace("<", "?LT?")
				.replace(">", "?GT?")
				.replace(" ", "?");
		printer.print(methodName);
		printer.print("#");
		printer.startItems("#");
		final var signatureTypes = new ArrayList<Type>();
		signatureTypes.add(sootMethod.getReturnType());
		signatureTypes.addAll(sootMethod.getParameterTypes());

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
		try {
			if (BehaviorTagMarker.v().hasTag(sootMethod)) {
				new BehaviorMethodToBplEncoder(printer).encodeMethod(sootMethod);
			} else {
				new ProceduralMethodToBplEncoder(printer).encodeMethod(sootMethod);
			}
		} catch (final Throwable throwable) {
			//System.err.println(sootMethod.getActiveBody());
			throw new RuntimeException("Exception while converting method: " + sootMethod, throwable);
		}

		printer.endLine();
	}

}
