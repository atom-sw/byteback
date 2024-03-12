package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import byteback.analysis.model.MethodModel;
import soot.jimple.Stmt;

/**
 * Abstract base class for call sites
 *
 * @author Steven Arzt
 */
public class AbstractCallSite {

    protected Stmt stmt;
    protected MethodModel container;

    public AbstractCallSite(Stmt stmt, MethodModel container) {
        this.stmt = stmt;
        this.container = container;
    }

    public Stmt getStmt() {
        return stmt;
    }

    public MethodModel getContainer() {
        return container;
    }

    @Override
    public String toString() {
        return stmt == null ? "<null>" : stmt.toString();
    }
}
