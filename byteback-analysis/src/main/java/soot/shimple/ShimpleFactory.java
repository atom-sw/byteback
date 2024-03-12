package soot.shimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Navindra Umanee <navindra@cs.mcgill.ca>
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

import soot.shimple.toolkits.graph.GlobalValueNumberer;
import soot.toolkits.graph.*;

/**
 * @author Navindra Umanee
 **/
public interface ShimpleFactory {

    /**
     * Constructors should memoize their return value. Call clearCache() to force recomputations if body has changed and
     * setBody() hasn't been called again.
     **/
    void clearCache();

    UnitGraph getUnitGraph();

    BlockGraph getBlockGraph();

    DominatorsFinder<Block> getDominatorsFinder();

    DominatorTree<Block> getDominatorTree();

    DominanceFrontier<Block> getDominanceFrontier();

    GlobalValueNumberer getGlobalValueNumberer();

    ReversibleGraph<Block> getReverseBlockGraph();

    DominatorsFinder<Block> getReverseDominatorsFinder();

    DominatorTree<Block> getReverseDominatorTree();

    DominanceFrontier<Block> getReverseDominanceFrontier();
}
