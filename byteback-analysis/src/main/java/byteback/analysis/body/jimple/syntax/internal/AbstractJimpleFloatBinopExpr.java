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

import byteback.analysis.model.syntax.type.Type;
import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.Jimple;

import java.util.List;

@SuppressWarnings("serial")
public abstract class AbstractJimpleFloatBinopExpr extends AbstractFloatBinopExpr implements ConvertToBaf {

    AbstractJimpleFloatBinopExpr(Value op1, Value op2) {
        this(Jimple.v().newArgBox(op1), Jimple.v().newArgBox(op2));
    }

    protected AbstractJimpleFloatBinopExpr(ValueBox op1Box, ValueBox op2Box) {
        super(op1Box, op2Box);
    }

    @Override
    public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
        ((ConvertToBaf) this.getOp1()).convertToBaf(context, out);
        ((ConvertToBaf) this.getOp2()).convertToBaf(context, out);

        Unit u = makeBafInst(this.getOp1().getType());
        out.add(u);
        u.addAllTagsOf(context.getCurrentUnit());
    }

    protected abstract Unit makeBafInst(Type opType);
}
