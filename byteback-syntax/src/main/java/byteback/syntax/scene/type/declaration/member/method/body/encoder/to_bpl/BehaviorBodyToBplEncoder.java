package byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.BodyEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.ReturnRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import soot.Body;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;

public class BehaviorBodyToBplEncoder extends BodyEncoder {

    public BehaviorBodyToBplEncoder(final Printer printer) {
			super(printer);
    }

    @Override
    public void encodeBody(final Body body) {
        for (final Unit unit : body.getUnits()) {
            if (unit instanceof AssignStmt assignStmt && assignStmt.getLeftOp() instanceof ReturnRef) {
                final Value behaviorValue = assignStmt.getRightOp();
                new ValueToBplEncoder(printer).encodeValue(behaviorValue);
                return;
            }
        }
    }

}