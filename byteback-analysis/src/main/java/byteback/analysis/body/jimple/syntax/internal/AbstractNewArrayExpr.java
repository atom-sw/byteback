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
import byteback.analysis.body.jimple.syntax.*;
import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.model.syntax.type.Type;
import soot.*;
import soot.baf.syntax.Baf;
import soot.util.Switch;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public abstract class AbstractNewArrayExpr implements NewArrayExpr, ConvertToBaf {

    protected Type baseType;
    protected final ValueBox sizeBox;

    protected AbstractNewArrayExpr(Type type, ValueBox sizeBox) {
        this.baseType = type;
        this.sizeBox = sizeBox;
    }

    @Override
    public abstract Object clone();

    @Override
    public boolean equivTo(Object o) {
        if (o instanceof AbstractNewArrayExpr ae) {
            return this.sizeBox.getValue().equivTo(ae.sizeBox.getValue()) && this.baseType.equals(ae.baseType);
        }
        return false;
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    @Override
    public int equivHashCode() {
        return sizeBox.getValue().equivHashCode() * 101 + baseType.hashCode() * 17;
    }

    @Override
    public String toString() {
        return Jimple.NEWARRAY + " (" + getBaseTypeString() + ")[" + sizeBox.getValue().toString() + ']';
    }

    @Override
    public void toString(UnitPrinter up) {
        up.literal(Jimple.NEWARRAY + " (");
        up.type(baseType);
        up.literal(")[");
        sizeBox.toString(up);
        up.literal("]");
    }

    private String getBaseTypeString() {
        return baseType.toString();
    }

    @Override
    public Type getBaseType() {
        return baseType;
    }

    @Override
    public void setBaseType(Type type) {
        baseType = type;
    }

    @Override
    public ValueBox getSizeBox() {
        return sizeBox;
    }

    @Override
    public Value getSize() {
        return sizeBox.getValue();
    }

    @Override
    public void setSize(Value size) {
        sizeBox.setValue(size);
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        List<ValueBox> useBoxes = new ArrayList<ValueBox>(sizeBox.getValue().getUseBoxes());
        useBoxes.add(sizeBox);
        return useBoxes;
    }

    @Override
    public Type getType() {
        if (baseType instanceof ArrayType base) {
            return ArrayType.v(base.baseType, base.numDimensions + 1);
        } else {
            return ArrayType.v(baseType, 1);
        }
    }

    @Override
    public void apply(Switch sw) {
        ((ExprSwitch) sw).caseNewArrayExpr(this);
    }

    @Override
    public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
        ((ConvertToBaf) getSize()).convertToBaf(context, out);

        Unit u = Baf.v().newNewArrayInst(getBaseType());
        out.add(u);
        u.addAllTagsOf(context.getCurrentUnit());
    }
}
