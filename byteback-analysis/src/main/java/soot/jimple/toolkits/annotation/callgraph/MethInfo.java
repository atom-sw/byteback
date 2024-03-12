package soot.jimple.toolkits.annotation.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import soot.Kind;
import byteback.analysis.model.MethodModel;

public class MethInfo {
    private MethodModel method;
    private boolean canExpandCollapse;
    private Kind edgeKind;

    public MethInfo(MethodModel meth, boolean b, Kind kind) {
        method(meth);
        canExpandCollapse(b);
        edgeKind(kind);
    }

    public Kind edgeKind() {
        return edgeKind;
    }

    public void edgeKind(Kind kind) {
        edgeKind = kind;
    }

    public boolean canExpandCollapse() {
        return canExpandCollapse;
    }

    public void canExpandCollapse(boolean b) {
        canExpandCollapse = b;
    }

    public MethodModel method() {
        return method;
    }

    public void method(MethodModel m) {
        method = m;
    }
}
