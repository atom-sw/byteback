package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.LocalBox;

public class IdentityStmt extends DefinitionStmt {

    public IdentityStmt(final Value local, final Value identityValue) {
        this(new LocalBox(local), new LocalBox(identityValue));
    }

    protected IdentityStmt(ValueBox localBox, ValueBox identityValueBox) {
        super(localBox, identityValueBox);
    }

    @Override
    public String toString() {
        return leftBox.getValue().toString() + " := " + rightBox.getValue().toString();
    }
}
