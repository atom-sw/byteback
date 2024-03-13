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

import byteback.analysis.body.jimple.syntax.AbstractTrap;
import byteback.analysis.model.syntax.ClassModel;
import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.body.common.syntax.UnitBox;
import byteback.analysis.body.jimple.syntax.Jimple;

public class JTrap extends AbstractTrap {

    public JTrap(ClassModel exception, Unit beginStmt, Unit endStmt, Unit handlerStmt) {
        super(exception, Jimple.v().newStmtBox(beginStmt), Jimple.v().newStmtBox(endStmt), Jimple.v().newStmtBox(handlerStmt));
    }

    public JTrap(ClassModel exception, UnitBox beginStmt, UnitBox endStmt, UnitBox handlerStmt) {
        super(exception, beginStmt, endStmt, handlerStmt);
    }

    @Override
    public Object clone() {
        return new JTrap(exception, getBeginUnit(), getEndUnit(), getHandlerUnit());
    }

    @Override
    public String toString() {
      String buf = "Trap :" + "\nbegin  : " + getBeginUnit() +
              "\nend    : " + getEndUnit() +
              "\nhandler: " + getHandlerUnit();
        return buf;
    }
}
