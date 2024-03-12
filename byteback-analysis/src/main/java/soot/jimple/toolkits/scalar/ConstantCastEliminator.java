package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Steven Arzt
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

import soot.*;
import soot.jimple.*;

import java.util.Map;

/**
 * Transformer for removing unnecessary casts on primitive values. An assignment a = (float) 42 will for instance be
 * transformed to a = 42f;
 *
 * @author Steven Arzt
 */
public class ConstantCastEliminator extends BodyTransformer {

    public ConstantCastEliminator(Singletons.Global g) {
    }

    public static ConstantCastEliminator v() {
        return G.v().soot_jimple_toolkits_scalar_ConstantCastEliminator();
    }

    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        // Check for all assignments that perform casts on primitive constants
        for (Unit u : b.getUnits()) {
            if (u instanceof AssignStmt assign) {
                Value rightOp = assign.getRightOp();
                if (rightOp instanceof CastExpr ce) {
                    Value castOp = ce.getOp();
                    if (castOp instanceof IntConstant) {
                        Type castType = ce.getType();
                        if (castType instanceof FloatType) {
                            // a = (float) 42
                            assign.setRightOp(FloatConstant.v(((IntConstant) castOp).value));
                        } else if (castType instanceof DoubleType) {
                            // a = (double) 42
                            assign.setRightOp(DoubleConstant.v(((IntConstant) castOp).value));
                        }
                    }
                }
            }
        }
    }
}
