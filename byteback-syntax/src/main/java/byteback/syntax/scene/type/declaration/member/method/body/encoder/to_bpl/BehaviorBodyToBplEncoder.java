package byteback.syntax.scene.type.declaration.member.method.body.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.encoder.BodyEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.value.ReturnRef;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl.ValueToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.TwoStateTagFlagger;
import soot.Body;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.ReturnStmt;

public class BehaviorBodyToBplEncoder extends BodyEncoder {

    public BehaviorBodyToBplEncoder(final Printer printer) {
        super(printer);
    }

    @Override
    public void encodeBody(final Body body) {
        final ValueToBplEncoder.HeapContext heapContext;

        if (TwoStateTagFlagger.v().isTagged(body.getMethod())) {
            heapContext = ValueToBplEncoder.HeapContext.TWO_STATE;
        } else {
            heapContext = ValueToBplEncoder.HeapContext.PRE_STATE;
        }

        final var valueToBplEncoder = new ValueToBplEncoder(printer, heapContext);

        for (final Unit unit : body.getUnits()) {
            if (unit instanceof final ReturnStmt returnStmt) {
                final Value behaviorValue = returnStmt.getOp();
                valueToBplEncoder.encodeValue(behaviorValue);
                return;
            }
        }
    }

}
