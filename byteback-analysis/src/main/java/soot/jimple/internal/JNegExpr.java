package soot.jimple.internal;

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

import soot.Unit;
import soot.Value;
import soot.baf.syntax.Baf;
import soot.jimple.ConvertToBaf;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;

import java.util.List;

public class JNegExpr extends AbstractNegExpr implements ConvertToBaf {

    public JNegExpr(Value op) {
        super(Jimple.v().newImmediateBox(op));
    }

    @Override
    public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
        ((ConvertToBaf) (getOp())).convertToBaf(context, out);
        Unit u = Baf.v().newNegInst(getType());
        u.addAllTagsOf(context.getCurrentUnit());
        out.add(u);
    }

    @Override
    public Object clone() {
        return new JNegExpr(Jimple.cloneIfNecessary(getOp()));
    }
}
