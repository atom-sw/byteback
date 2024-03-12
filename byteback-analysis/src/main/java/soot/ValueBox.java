package soot;

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

import soot.tag.Host;

import java.io.Serializable;

/**
 * A box which can contain values.
 *
 * @see Value
 */
public interface ValueBox extends Host, Serializable {
    /**
     * Sets the value contained in this box as given. Subject to canContainValue() checks.
     */
    void setValue(Value value);

    /**
     * Returns the value contained in this box.
     */
    Value getValue();

    /**
     * Returns true if the given Value fits in this box.
     */
    boolean canContainValue(Value value);

    void toString(UnitPrinter up);

}
