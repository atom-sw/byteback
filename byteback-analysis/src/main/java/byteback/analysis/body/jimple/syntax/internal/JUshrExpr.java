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

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.model.syntax.type.IntType;
import byteback.analysis.model.syntax.type.Type;
import soot.baf.syntax.Baf;
import byteback.analysis.body.jimple.syntax.ExprSwitch;
import byteback.analysis.body.jimple.syntax.Jimple;
import byteback.analysis.body.jimple.syntax.UshrExpr;
import soot.util.Switch;

public class JUshrExpr extends AbstractJimpleIntLongBinopExpr implements UshrExpr {

    public JUshrExpr(Value op1, Value op2) {
        super(op1, op2);
    }

    @Override
    public final String getSymbol() {
        return " >>> ";
    }

    @Override
    public void apply(Switch sw) {
        ((ExprSwitch) sw).caseUshrExpr(this);
    }

    @Override
    protected Unit makeBafInst(Type opType) {
        return Baf.v().newUshrInst(this.getOp1().getType());
    }

    @Override
    public Type getType() {
        if (isIntLikeType(op2Box.getValue().getType())) {
            final Type t1 = op1Box.getValue().getType();
            if (isIntLikeType(t1)) {
                return IntType.v();
            }
            final LongType tyLong = LongType.v();
            if (tyLong.equals(t1)) {
                return LongType.v();
            }
        }
        return UnknownType.v();
    }

    @Override
    public Object clone() {
        return new JUshrExpr(Jimple.cloneIfNecessary(getOp1()), Jimple.cloneIfNecessary(getOp2()));
    }
}
