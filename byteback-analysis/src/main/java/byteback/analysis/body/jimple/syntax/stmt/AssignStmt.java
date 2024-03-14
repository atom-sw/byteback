package byteback.analysis.body.jimple.syntax.stmt;

import byteback.analysis.body.common.syntax.expr.Immediate;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.jimple.syntax.expr.AssigneeBox;
import byteback.analysis.body.jimple.syntax.expr.VariableBox;

import java.util.List;

public class AssignStmt extends DefinitionStmt {

    public static class LinkedVariableBox extends VariableBox {
        ValueBox otherBox = null;

        public LinkedVariableBox(Value v) {
            super(v);
        }

        public void setOtherBox(ValueBox otherBox) {
            this.otherBox = otherBox;
        }

        @Override
        public boolean canContainValue(Value v) {
            if (super.canContainValue(v)) {
                return (otherBox == null) || (v instanceof Immediate) || (otherBox.getValue() instanceof Immediate);
            }

            return false;
        }
    }

    public static class LinkedAssigneeBox extends AssigneeBox {
        ValueBox otherBox = null;

        public LinkedAssigneeBox(Value v) {
            super(v);
        }

        public void setOtherBox(ValueBox otherBox) {
            this.otherBox = otherBox;
        }

        @Override
        public boolean canContainValue(Value v) {
            if (super.canContainValue(v)) {
                return (otherBox == null) || (v instanceof Immediate) || (otherBox.getValue() instanceof Immediate);
            }
            return false;
        }
    }

    public AssignStmt(Value variable, Value rvalue) {
        this(new LinkedVariableBox(variable), new LinkedAssigneeBox(rvalue));

        ((LinkedVariableBox) leftBox).setOtherBox(rightBox);
        ((LinkedAssigneeBox) rightBox).setOtherBox(leftBox);

        if (!leftBox.canContainValue(variable) || !rightBox.canContainValue(rvalue)) {
            throw new RuntimeException(
                    "Illegal assignment statement. Make sure that either left side or right hand side has a local or constant."
                            + "Variable is class " + variable.getClass().getName() + "(" + leftBox.canContainValue(variable) + ")"
                            + " and rvalue is class " + rvalue.getClass().getName() + "(" + rightBox.canContainValue(rvalue) + ").");
        }
    }

    protected AssignStmt(final ValueBox assigneeBox, final ValueBox assignedBox) {
        super(assigneeBox, assignedBox);

        if (leftBox instanceof LinkedVariableBox) {
            ((LinkedVariableBox) leftBox).setOtherBox(rightBox);
        }

        if (rightBox instanceof LinkedAssigneeBox) {
            ((LinkedAssigneeBox) rightBox).setOtherBox(leftBox);
        }
    }

    @Override
    public String toString() {
        return leftBox.getValue().toString() + " = " + rightBox.getValue().toString();
    }
}
