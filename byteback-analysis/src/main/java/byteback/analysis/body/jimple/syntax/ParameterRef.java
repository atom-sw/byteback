package byteback.analysis.body.jimple.syntax;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import byteback.analysis.model.syntax.type.Type;
import soot.UnitPrinter;
import byteback.analysis.body.common.syntax.ValueBox;
import soot.util.Switch;

import java.util.Collections;
import java.util.List;

/**
 * <code>ParameterRef</code> objects are used by <code>Body</code> objects to refer to the parameter slots on method entry.
 * <br>
 * <p>
 * For instance, in an instance method, the first statement will often be <code> this := @parameter0; </code>
 */
public class ParameterRef implements IdentityRef {
    int n;
    Type paramType;

    /**
     * Constructs a ParameterRef object of the specified type, representing the specified parameter number.
     */
    public ParameterRef(Type paramType, int number) {
        this.n = number;
        this.paramType = paramType;
    }

    public boolean equivTo(Object o) {
        if (o instanceof ParameterRef) {
            return n == ((ParameterRef) o).n && paramType.equals(((ParameterRef) o).paramType);
        }
        return false;
    }

    public int equivHashCode() {
        return n * 101 + paramType.hashCode() * 17;
    }

    /**
     * Create a new ParameterRef object with the same paramType and number.
     */
    public Object clone() {
        return new ParameterRef(paramType, n);
    }

    /**
     * Converts the given ParameterRef into a String i.e. <code>@parameter0: .int</code>.
     */
    public String toString() {
        return "@parameter" + n + ": " + paramType;
    }

    /**
     * Returns the index of this ParameterRef.
     */
    public int getIndex() {
        return n;
    }

    /**
     * Sets the index of this ParameterRef.
     */
    public void setIndex(int index) {
        n = index;
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }

    /**
     * Returns the type of this ParameterRef.
     */
    public Type getType() {
        return paramType;
    }

    /**
     * Used with RefSwitch.
     */
    public void apply(Switch sw) {
        ((RefSwitch) sw).caseParameterRef(this);
    }
}
