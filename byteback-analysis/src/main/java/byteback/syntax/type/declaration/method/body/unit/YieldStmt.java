package byteback.syntax.type.declaration.method.body.unit;

import soot.UnitPrinter;
import soot.jimple.internal.JReturnVoidStmt;

public class YieldStmt extends JReturnVoidStmt {

    @Override
    public void toString(final UnitPrinter printer) {
        printer.literal("yield");
    }

}
