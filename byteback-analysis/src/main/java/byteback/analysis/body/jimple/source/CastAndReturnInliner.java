package byteback.analysis.body.jimple.source;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import byteback.analysis.body.common.Body;
import byteback.analysis.body.common.syntax.*;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.jimple.syntax.expr.CastExpr;
import byteback.analysis.body.jimple.syntax.stmt.AssignStmt;
import byteback.analysis.body.jimple.syntax.stmt.GotoStmt;

import java.util.Iterator;
import java.util.List;

/**
 * Transformers that inlines returns that cast and return an object. We take a = .. goto l0;
 * <p>
 * l0: b = (B) a; return b;
 * <p>
 * and transform it into a = .. return a;
 * <p>
 * This makes it easier for the local splitter to split distinct uses of the same variable. Imagine that "a" can come from
 * different parts of the code and have different types. To be able to find a valid typing at all, we must break apart the
 * uses of "a".
 *
 * @author Steven Arzt
 */
public class CastAndReturnInliner extends BodyTransformer {

    @Override
    public void transformBody(final Body body) {

        final UnitPatchingChain units = body.getUnits();

        for (Iterator<Unit> it = units.snapshotIterator(); it.hasNext(); ) {
            Unit u = it.next();
            if (u instanceof GotoStmt gtStmt) {
                if (gtStmt.getTarget() instanceof AssignStmt assign) {
                    if (assign.getRightOp() instanceof CastExpr ce) {

                        // We have goto that ends up at a cast statement
                        Unit nextStmt = units.getSuccOf(assign);
                        if (nextStmt instanceof ReturnStmt retStmt) {
                            if (retStmt.getOp() == assign.getLeftOp()) {
                                // We need to replace the GOTO with the return
                                ReturnStmt newStmt = (ReturnStmt) retStmt.clone();
                                Local a = (Local) ce.getOp();

                                for (Trap t : body.getTraps()) {
                                    for (UnitBox ubox : t.getUnitBoxes()) {
                                        if (ubox.getUnit() == gtStmt) {
                                            ubox.setUnit(newStmt);
                                        }
                                    }
                                }
                                Jimple j = Jimple.v();
                                Local n = j.newLocal(a.getName() + "_ret", ce.getCastType());
                                body.getLocals().add(n);
                                newStmt.setOp(n);

                                final List<UnitBox> boxesRefGtStmt = gtStmt.getBoxesPointingToThis();
                                while (!boxesRefGtStmt.isEmpty()) {
                                    boxesRefGtStmt.get(0).setUnit(newStmt);
                                }
                                units.swapWith(gtStmt, newStmt);
                                ce = (CastExpr) ce.clone();
                                units.insertBefore(j.newAssignStmt(n, ce), newStmt);
                            }
                        }
                    }
                }
            }
        }
    }
}
