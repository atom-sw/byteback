package byteback.analysis.body.jimple.syntax.stmt;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import byteback.analysis.body.common.syntax.*;
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

    protected AssignStmt(ValueBox variableBox, ValueBox rvalueBox) {
        super(variableBox, rvalueBox);
        if (leftBox instanceof LinkedVariableBox) {
            ((LinkedVariableBox) leftBox).setOtherBox(rightBox);
        }
        if (rightBox instanceof LinkedAssigneeBox) {
            ((LinkedAssigneeBox) rightBox).setOtherBox(leftBox);
        }
    }

    @Override
    public List<UnitBox> getUnitBoxes() {
        // handle possible PhiExpr's
        Value rValue = rightBox.getValue();
        if (rValue instanceof UnitBoxOwner) {
            return ((UnitBoxOwner) rValue).getUnitBoxes();
        } else {
            return super.getUnitBoxes();
        }
    }

    @Override
    public String toString() {
        return leftBox.getValue().toString() + " = " + rightBox.getValue().toString();
    }

    public void setLeftOp(Value variable) {
        getLeftOpBox().setValue(variable);
    }

    public void setRightOp(Value rvalue) {
        getRightOpBox().setValue(rvalue);
    }

}
