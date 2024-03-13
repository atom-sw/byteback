package byteback.analysis.body.jimple.syntax.internal;

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

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.model.syntax.type.Type;
import soot.*;
import byteback.analysis.body.jimple.syntax.ExprSwitch;
import byteback.analysis.body.jimple.syntax.InstanceOfExpr;
import byteback.analysis.body.jimple.syntax.Jimple;
import soot.util.Switch;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public abstract class AbstractInstanceOfExpr implements InstanceOfExpr {

    protected final ValueBox opBox;
    protected Type checkType;

    protected AbstractInstanceOfExpr(ValueBox opBox, Type checkType) {
        this.opBox = opBox;
        this.checkType = checkType;
    }

    @Override
    public abstract Object clone();

    @Override
    public boolean equivTo(Object o) {
        if (o instanceof AbstractInstanceOfExpr aie) {
            return this.opBox.getValue().equivTo(aie.opBox.getValue()) && this.checkType.equals(aie.checkType);
        }
        return false;
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    @Override
    public int equivHashCode() {
        return opBox.getValue().equivHashCode() * 101 + checkType.hashCode() * 17;
    }

    @Override
    public String toString() {
        return opBox.getValue().toString() + " " + Jimple.INSTANCEOF + " " + checkType.toString();
    }

    @Override
    public void toString(UnitPrinter up) {
        opBox.toString(up);
        up.literal(" " + Jimple.INSTANCEOF + " ");
        up.type(checkType);
    }

    @Override
    public Value getOp() {
        return opBox.getValue();
    }

    @Override
    public void setOp(Value op) {
        opBox.setValue(op);
    }

    @Override
    public ValueBox getOpBox() {
        return opBox;
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        List<ValueBox> list = new ArrayList<ValueBox>(opBox.getValue().getUseBoxes());
        list.add(opBox);
        return list;
    }

    @Override
    public Type getType() {
        return BooleanType.v();
    }

    @Override
    public Type getCheckType() {
        return checkType;
    }

    @Override
    public void setCheckType(Type checkType) {
        this.checkType = checkType;
    }

    @Override
    public void apply(Switch sw) {
        ((ExprSwitch) sw).caseInstanceOfExpr(this);
    }
}
