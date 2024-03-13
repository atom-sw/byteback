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

import byteback.analysis.body.common.syntax.Local;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.model.syntax.type.Type;
import soot.*;
import soot.baf.syntax.Baf;
import byteback.analysis.body.jimple.syntax.JimpleValueSwitch;
import soot.util.Switch;

import java.util.Collections;
import java.util.List;

public class JimpleLocal implements Local, ConvertToBaf {

    protected String name;
    protected Type type;
    private volatile int number = 0;

    /**
     * Constructs a JimpleLocal of the given name and type.
     */
    public JimpleLocal(String name, Type type) {
        setName(name);
        setType(type);
    }

    /**
     * Returns true if the given object is structurally equal to this one.
     */
    @Override
    public boolean equivTo(Object o) {
        return this.equals(o);
    }

    /**
     * Returns a hash code for this object, consistent with structural equality.
     */
    @Override
    public int equivHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /**
     * Returns a clone of the current JimpleLocal.
     */
    @Override
    public Object clone() {
        // do not intern the name again
        JimpleLocal local = new JimpleLocal(null, type);
        local.name = name;
        return local;
    }

    /**
     * Returns the name of this object.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this object as given.
     */
    @Override
    public void setName(String name) {
        this.name = (name == null) ? null : name.intern();
    }

    /**
     * Returns the type of this local.
     */
    @Override
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of this local.
     */
    @Override
    public void setType(Type t) {
        this.type = t;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void toString(UnitPrinter up) {
        up.local(this);
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }

    @Override
    public void apply(Switch sw) {
        ((JimpleValueSwitch) sw).caseLocal(this);
    }

    @Override
    public void convertToBaf(JimpleToBafContext context, List<byteback.analysis.body.jimple.syntax.Unit> out) {
        Unit u = Baf.v().newLoadInst(getType(), context.getBafLocalOfJimpleLocal(this));
        u.addAllTagsOf(context.getCurrentUnit());
        out.add(u);
    }

    @Override
    public final int getNumber() {
        return number;
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public boolean isStackLocal() {
        String n = getName();
        return n != null && n.charAt(0) == '$';
    }
}
