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

import byteback.analysis.body.common.syntax.UnitBox;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.*;
import byteback.analysis.body.jimple.syntax.Unit;
import soot.*;
import soot.baf.syntax.Baf;
import soot.baf.syntax.PlaceholderInst;
import soot.util.Switch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class JLookupSwitchStmt extends AbstractSwitchStmt implements LookupSwitchStmt {
    /**
     * List of lookup values from the corresponding bytecode instruction, represented as IntConstants.
     */
    protected List<IntConstant> lookupValues;

    /**
     * Constructs a new JLookupSwitchStmt. lookupValues should be a list of IntConst s.
     */
    public JLookupSwitchStmt(Value key, List<IntConstant> lookupValues, List<? extends Unit> targets, Unit defaultTarget) {
        this(Jimple.v().newImmediateBox(key), lookupValues, getTargetBoxesArray(targets, Jimple.v()::newStmtBox),
                Jimple.v().newStmtBox(defaultTarget));
    }

    /**
     * Constructs a new JLookupSwitchStmt. lookupValues should be a list of IntConst s.
     */
    public JLookupSwitchStmt(Value key, List<IntConstant> lookupValues, List<? extends UnitBox> targets,
                             UnitBox defaultTarget) {
        this(Jimple.v().newImmediateBox(key), lookupValues, targets.toArray(new UnitBox[targets.size()]), defaultTarget);
    }

    protected JLookupSwitchStmt(ValueBox keyBox, List<IntConstant> lookupValues, UnitBox[] targetBoxes,
                                UnitBox defaultTargetBox) {
        super(keyBox, defaultTargetBox, targetBoxes);
        setLookupValues(lookupValues);
    }

    @Override
    public Object clone() {
        List<IntConstant> clonedLookupValues = new ArrayList<IntConstant>(lookupValues.size());
        for (IntConstant c : lookupValues) {
            clonedLookupValues.add(IntConstant.v(c.value));
        }
        return new JLookupSwitchStmt(getKey(), clonedLookupValues, getTargets(), getDefaultTarget());
    }

    @Override
    public String toString() {
        final char endOfLine = ' ';
        StringBuilder buf = new StringBuilder(Jimple.LOOKUPSWITCH + "(");

        buf.append(keyBox.getValue().toString()).append(')').append(endOfLine);
        buf.append('{').append(endOfLine);

        for (ListIterator<IntConstant> it = lookupValues.listIterator(); it.hasNext(); ) {
            IntConstant c = it.next();
            buf.append("    " + Jimple.CASE + " ").append(c).append(": " + Jimple.GOTO + " ");
            Unit target = getTarget(it.previousIndex());
            buf.append(target == this ? "self" : target).append(';').append(endOfLine);
        }
        {
            buf.append("    " + Jimple.DEFAULT + ": " + Jimple.GOTO + " ");
            Unit target = getDefaultTarget();
            buf.append(target == this ? "self" : target).append(';').append(endOfLine);
        }
        buf.append('}');

        return buf.toString();
    }

    @Override
    public void toString(UnitPrinter up) {
        up.literal(Jimple.LOOKUPSWITCH + "(");
        keyBox.toString(up);
        up.literal(")");
        up.newline();
        up.literal("{");
        up.newline();
        for (ListIterator<IntConstant> it = lookupValues.listIterator(); it.hasNext(); ) {
            IntConstant c = it.next();
            up.literal("    " + Jimple.CASE + " ");
            up.constant(c);
            up.literal(": " + Jimple.GOTO + " ");
            targetBoxes[it.previousIndex()].toString(up);
            up.literal(";");
            up.newline();
        }

        up.literal("    " + Jimple.DEFAULT + ": " + Jimple.GOTO + " ");
        defaultTargetBox.toString(up);
        up.literal(";");
        up.newline();
        up.literal("}");
    }

    @Override
    public void setLookupValues(List<IntConstant> lookupValues) {
        this.lookupValues = new ArrayList<IntConstant>(lookupValues);
    }

    @Override
    public void setLookupValue(int index, int value) {
        lookupValues.set(index, IntConstant.v(value));
    }

    @Override
    public int getLookupValue(int index) {
        return lookupValues.get(index).value;
    }

    @Override
    public List<IntConstant> getLookupValues() {
        return Collections.unmodifiableList(lookupValues);
    }

    @Override
    public void apply(Switch sw) {
        ((StmtSwitch) sw).caseLookupSwitchStmt(this);
    }

    @Override
    public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
        ((ConvertToBaf) getKey()).convertToBaf(context, out);

        final Baf baf = Baf.v();
        final List<Unit> targets = getTargets();
        List<PlaceholderInst> targetPlaceholders = new ArrayList<PlaceholderInst>(targets.size());
        for (Unit target : targets) {
            targetPlaceholders.add(baf.newPlaceholderInst(target));
        }

        Unit u = baf.newLookupSwitchInst(baf.newPlaceholderInst(getDefaultTarget()), getLookupValues(), targetPlaceholders);
        u.addAllTagsOf(this);
        out.add(u);
    }

    @Override
    public Unit getTargetForValue(int value) {
        for (int i = 0; i < lookupValues.size(); i++) {
            if (lookupValues.get(i).value == value) {
                return getTarget(i);
            }
        }
        return getDefaultTarget();
    }
}
