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

import byteback.analysis.model.ClassModel;

import java.util.List;

/**
 * A trap (exception catcher), used within Body classes. Intermediate representations must use an implementation of Trap to
 * describe caught exceptions.
 */
public interface Trap extends UnitBoxOwner {

    /**
     * <p>
     * Returns the first trapped unit, unless this <code>Trap</code> does not trap any units at all.
     * </p>
     *
     * <p>
     * If this is a degenerate <code>Trap</code> which traps no units (which can occur if all the units originally trapped by
     * the exception handler have been optimized away), returns an untrapped unit. The returned unit will likely be the first
     * unit remaining after the point where the trapped units were once located, but the only guarantee provided is that for
     * such an empty trap, <code>getBeginUnit()</code> will return the same value as {@link #getEndUnit()}.
     * </p>
     */
    Unit getBeginUnit();

    /**
     * <p>
     * Returns the unit following the last trapped unit (that is, the first succeeding untrapped unit in the underlying
     * <Code>Chain</code>), unless this <code>Trap</code> does not trap any units at all.
     * </p>
     *
     * <p>
     * In the case of a degenerate <code>Trap</code> which traps no units, returns the same untrapped unit as
     * <code>getBeginUnit()</code>
     * </p>
     *
     * <p>
     * Note that a weakness of marking the end of the trapped region with the first untrapped unit is that Soot has no good
     * mechanism for describing a <code>Trap</code> which traps the last unit in a method.
     * </p>
     */
    Unit getEndUnit();

    /**
     * Returns the unit handling the exception being trapped.
     */
    Unit getHandlerUnit();

    /**
     * Returns the box holding the unit returned by {@link #getBeginUnit()}.
     */
    UnitBox getBeginUnitBox();

    /**
     * Returns the box holding the unit returned by {@link #getEndUnit()}.
     */
    UnitBox getEndUnitBox();

    /**
     * Returns the box holding the exception handler unit.
     */
    UnitBox getHandlerUnitBox();

    /**
     * Returns the boxes for first, last, and handler units.
     */
    @Override
    List<UnitBox> getUnitBoxes();

    /**
     * Returns the exception being caught.
     */
    ClassModel getException();

    /**
     * Sets the value to be returned by {@link #getBeginUnit()} to <code>beginUnit</code>.
     */
    void setBeginUnit(Unit beginUnit);

    /**
     * Sets the value to be returned by {@link #getEndUnit()} to <code>endUnit</code>.
     */
    void setEndUnit(Unit endUnit);

    /**
     * Sets the unit handling the exception to <code>handlerUnit</code>.
     */
    void setHandlerUnit(Unit handlerUnit);

    /**
     * Sets the exception being caught to <code>exception</code>.
     */
    void setException(ClassModel exception);

    /**
     * Performs a shallow clone of this trap.
     */
    Object clone();
}
