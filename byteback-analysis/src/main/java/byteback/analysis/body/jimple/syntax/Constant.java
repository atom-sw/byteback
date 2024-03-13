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

import byteback.analysis.body.common.syntax.Immediate;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;

import java.util.Collections;
import java.util.List;

public abstract class Constant implements Value, Immediate {

    @Override
    public final List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }

    /**
     * Clones the current constant. Not implemented here.
     */
    @Override
    public Object clone() {
        throw new RuntimeException();
    }

    /**
     * Returns true if this object is structurally equivalent to c. For Constants, equality is structural equality, so we just
     * call equals().
     */
    @Override
    public boolean equivTo(Object c) {
        return equals(c);
    }

    /**
     * Returns a hash code consistent with structural equality for this object. For Constants, equality is structural equality;
     * we hope that each subclass defines hashCode() correctly.
     */
    @Override
    public int equivHashCode() {
        return hashCode();
    }
}
