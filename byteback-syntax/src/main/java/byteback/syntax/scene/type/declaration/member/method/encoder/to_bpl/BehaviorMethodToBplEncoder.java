package byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl;

import java.util.List;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.analysis.ParameterLocalFinder;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl.BehaviorBodyToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.PreludeTagAccessor;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import soot.Body;
import soot.Local;
import soot.SootMethod;
import soot.Type;

public class BehaviorMethodToBplEncoder extends MethodToBplEncoder {

	public BehaviorMethodToBplEncoder(final Printer printer) {
		super(printer);
	}

	public void encodeInputLocals(final List<Local> inputLocals) {
		final var valueToBplEncoder = new ValueToBplEncoder(printer);
		final var typeAccessToBplEncoder = new TypeAccessToBplEncoder(printer);

		for (final Local inputLocal : inputLocals) {
			printer.separate();
			valueToBplEncoder.encodeLocal(inputLocal);
			printer.print(": ");
			final Type localType = VimpTypeInterpreter.v().typeOf(inputLocal);
			typeAccessToBplEncoder.encodeTypeAccess(localType);
		}
	}

	public void encodeMethod(final SootMethod sootMethod) {
		if (PreludeTagAccessor.v().hasTag(sootMethod)) {
			return;
		}

		printer.print("function ");
		encodeMethodName(sootMethod);
		printer.print("(");
		printer.startItems(", ");

		final List<Local> inputLocals = ParameterLocalFinder.v().findInputLocals(sootMethod);
		encodeInputLocals(inputLocals);
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
