package soot;

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

import byteback.analysis.model.ClassModel;
import soot.util.Chain;

import java.util.*;

/**
 * Utility methods for dealing with traps.
 */

// TODO is this class really necessary?
public class TrapManager {

    /**
     * If exception e is caught at unit u in body b, return true; otherwise, return false.
     */
    public static boolean isExceptionCaughtAt(ClassModel e, Unit u, Body b) {
        // Look through the traps t of b, checking to see if (1) caught exception is
        // e and, (2) unit lies between t.beginUnit and t.endUnit.
        final Hierarchy h = Scene.v().getActiveHierarchy();
        final Chain<Unit> units = b.getUnits();

        for (Trap t : b.getTraps()) {
            /* Ah ha, we might win. */
            if (h.isClassSubclassOfIncluding(e, t.getException())) {
                for (Iterator<Unit> it = units.iterator(t.getBeginUnit(), units.getPredOf(t.getEndUnit())); it.hasNext(); ) {
                    if (u.equals(it.next())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns the list of traps caught at Unit u in Body b.
     */
    public static List<Trap> getTrapsAt(Unit unit, Body b) {
        final Chain<Unit> units = b.getUnits();
        List<Trap> trapsList = new ArrayList<>();
        for (Trap t : b.getTraps()) {
            for (Iterator<Unit> it = units.iterator(t.getBeginUnit(), units.getPredOf(t.getEndUnit())); it.hasNext(); ) {
                if (unit.equals(it.next())) {
                    trapsList.add(t);
                }
            }
        }
        return trapsList;
    }

    /**
     * Returns a set of units which lie inside the range of any trap.
     */
    public static Set<Unit> getTrappedUnitsOf(Body body) {
        final Chain<Unit> units = body.getUnits();
        Set<Unit> trapsSet = new HashSet<>();
        for (Trap t : body.getTraps()) {
            for (Iterator<Unit> it = units.iterator(t.getBeginUnit(), units.getPredOf(t.getEndUnit())); it.hasNext(); ) {
                trapsSet.add(it.next());
            }
        }
        return trapsSet;
    }

    /**
     * Splits all traps so that they do not cross the range rangeStart - rangeEnd. Note that rangeStart is inclusive, rangeEnd
     * is exclusive.
     */
    public static void splitTrapsAgainst(final Body body, final Unit rangeStart, final Unit rangeEnd) {
        final Chain<Trap> traps = body.getTraps();
        final Chain<Unit> units = body.getUnits();

        for (Iterator<Trap> trapsIt = traps.snapshotIterator(); trapsIt.hasNext(); ) {
            Trap t = trapsIt.next();

            boolean insideRange = false;
            for (Iterator<Unit> unitIt = units.iterator(t.getBeginUnit(), t.getEndUnit()); unitIt.hasNext(); ) {
                Unit u = unitIt.next();
                if (rangeStart.equals(u)) {
                    insideRange = true;
                }
                if (!unitIt.hasNext()) { // i.e. u.equals(t.getEndUnit())
                    if (insideRange) {
                        Trap newTrap = (Trap) t.clone();
                        t.setBeginUnit(rangeStart);
                        newTrap.setEndUnit(rangeStart);
                        traps.insertAfter(newTrap, t);
                    } else {
                        break;
                    }
                }
                if (rangeEnd.equals(u)) {
                    // insideRange had better be true now.
                    if (!insideRange) {
                        throw new RuntimeException("inversed range?");
                    }
                    Trap firstTrap = (Trap) t.clone();
                    Trap secondTrap = (Trap) t.clone();
                    firstTrap.setEndUnit(rangeStart);
                    secondTrap.setBeginUnit(rangeStart);
                    secondTrap.setEndUnit(rangeEnd);
                    t.setBeginUnit(rangeEnd);

                    traps.insertAfter(firstTrap, t);
                    traps.insertAfter(secondTrap, t);
                }
            }
        }
    }

    /**
     * Given a body and a unit handling an exception, returns the list of exception types possibly caught by the handler.
     */
    public static List<RefType> getExceptionTypesOf(Unit u, Body body) {
        List<RefType> possibleTypes = new ArrayList<>();
        for (Trap trap : body.getTraps()) {
            if (trap.getHandlerUnit() == u) {
                RefType type = RefType.v(trap.getException().getName());
                possibleTypes.add(type);
            }
        }

        return possibleTypes;
    }
}
