package byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.BodyEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import soot.Body;
import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.jimple.ReturnStmt;

public class BehaviorBodyToBplEncoder extends BodyEncoder {

	public BehaviorBodyToBplEncoder(final Printer printer) {
		super(printer);
	}

	@Override
	public void encodeBody(final Body body) {
		final PatchingChain<Unit> units = body.getUnits();

		final var valueToBplEncoder = new ValueToBplEncoder(printer);

		for (final Unit unit : units) {
			if (unit instanceof final ReturnStmt returnStmt) {
				final Value behaviorValue = returnStmt.getOp();
				valueToBplEncoder.encodeValue(behaviorValue);
				return;
			}
		}
	}

}
