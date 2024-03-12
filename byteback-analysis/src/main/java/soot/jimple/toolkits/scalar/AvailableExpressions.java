package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrick Lam
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

import soot.EquivalentValue;
import soot.Unit;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.Chain;

import java.util.List;

/**
 * Provides an user-interface for the AvailableExpressionsAnalysis class. Returns, for each statement, the list of
 * expressions available before and after it.
 */
public interface AvailableExpressions {
    /**
     * Returns a List containing the UnitValueBox pairs corresponding to expressions available before u.
     */
    List<UnitValueBoxPair> getAvailablePairsBefore(Unit u);

    /**
     * Returns a List containing the UnitValueBox pairs corresponding to expressions available after u.
     */
    List<UnitValueBoxPair> getAvailablePairsAfter(Unit u);

    /**
     * Returns a Chain containing the EquivalentValue objects corresponding to expressions available before u.
     */
    Chain<EquivalentValue> getAvailableEquivsBefore(Unit u);

    /**
     * Returns a Chain containing the EquivalentValue objects corresponding to expressions available after u.
     */
    Chain<EquivalentValue> getAvailableEquivsAfter(Unit u);
}
