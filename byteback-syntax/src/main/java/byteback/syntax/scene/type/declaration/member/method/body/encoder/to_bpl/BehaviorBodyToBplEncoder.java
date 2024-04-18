package byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl;

import byteback.common.function.Lazy;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.BodyEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.ReturnRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import soot.Body;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;

public class BehaviorBodyToBplEncoder implements BodyEncoder {

    private static final Lazy<BehaviorBodyToBplEncoder> INSTANCE = Lazy.from(BehaviorBodyToBplEncoder::new);

    public static BehaviorBodyToBplEncoder v() {
        return INSTANCE.get();
    }

    private BehaviorBodyToBplEncoder() {
    }

    @Override
    public void encodeBody(final Printer printer, final Body body) {
        for (final Unit unit : body.getUnits()) {
            if (unit instanceof AssignStmt assignStmt && assignStmt.getLeftOp() instanceof ReturnRef) {
                final Value behaviorValue = assignStmt.getRightOp();
                ValueToBplEncoder.v().encodeValue(printer, behaviorValue);
            }
        }
    }

}
