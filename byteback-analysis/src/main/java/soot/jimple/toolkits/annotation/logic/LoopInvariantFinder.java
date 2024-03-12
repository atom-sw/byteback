package soot.jimple.toolkits.annotation.logic;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.tag.ColorTag;
import soot.tag.LoopInvariantTag;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SmartLocalDefs;
import soot.toolkits.scalar.SmartLocalDefsPool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class LoopInvariantFinder extends BodyTransformer {
    private static final Logger logger = LoggerFactory.getLogger(LoopInvariantFinder.class);

    private ArrayList constants;

    public LoopInvariantFinder(Singletons.Global g) {
    }

    public static LoopInvariantFinder v() {
        return G.v().soot_jimple_toolkits_annotation_logic_LoopInvariantFinder();
    }

    /**
     * this one uses the side effect tester
     */
    protected void internalTransform(Body b, String phaseName, Map options) {

        SmartLocalDefs sld = SmartLocalDefsPool.v().getSmartLocalDefsFor(b);
        UnitGraph g = sld.getGraph();
        NaiveSideEffectTester nset = new NaiveSideEffectTester();

        Collection<Loop> loops = new LoopFinder().getLoops(b);
        constants = new ArrayList();

        // no loop invariants if no loops
        if (loops.isEmpty()) {
            return;
        }

        Iterator<Loop> lIt = loops.iterator();
        while (lIt.hasNext()) {
            Loop loop = lIt.next();
            Stmt header = loop.getHead();
            Collection<Stmt> loopStmts = loop.getLoopStatements();
            Iterator<Stmt> bIt = loopStmts.iterator();
            while (bIt.hasNext()) {
                Stmt tStmt = bIt.next();
                // System.out.println("will test stmt: "+tStmt+" for loop header: "+header);
                // System.out.println("will test with loop stmts: "+loopStmts);
                handleLoopBodyStmt(tStmt, nset, loopStmts);
            }
        }
    }

    private void handleLoopBodyStmt(Stmt s, NaiveSideEffectTester nset, Collection<Stmt> loopStmts) {
        // need to do some checks for arrays - when there is an multi-dim array
        // --> for defs there is a get of one of the dims that claims to be
        // loop invariant

        // handle constants
        if (s instanceof DefinitionStmt ds) {
            if (ds.getLeftOp() instanceof Local && ds.getRightOp() instanceof Constant) {
                if (!constants.contains(ds.getLeftOp())) {
                    constants.add(ds.getLeftOp());
                } else {
                    constants.remove(ds.getLeftOp());
                }
            }
        }

        // ignore goto stmts
        // ignore invoke stmts
        if ((s instanceof GotoStmt) || (s instanceof InvokeStmt)) {
            return;
        }

        logger.debug("s : " + s + " use boxes: " + s.getUseBoxes() + " def boxes: " + s.getDefBoxes());
        // just use boxes here
        Iterator useBoxesIt = s.getUseBoxes().iterator();
        boolean result = true;
        uses:
        while (useBoxesIt.hasNext()) {
            ValueBox vb = (ValueBox) useBoxesIt.next();
            Value v = vb.getValue();
            // System.out.println("next vb: "+v+" is a: "+vb.getClass());
            // System.out.println("next vb: "+v+" class is a: "+v.getClass());
            // new's are not invariant
            if (v instanceof NewExpr) {
                result = false;
                logger.debug("break uses: due to new expr");
                break;
            }
            // invokes are not invariant
            if (v instanceof InvokeExpr) {
                result = false;
                logger.debug("break uses: due to invoke expr");
                break;
            }
            // side effect tester doesn't handle expr
            if (v instanceof Expr) {
                continue;
            }

            logger.debug("test: " + v + " of kind: " + v.getClass());
            Iterator loopStmtsIt = loopStmts.iterator();
            while (loopStmtsIt.hasNext()) {
                Stmt next = (Stmt) loopStmtsIt.next();
                if (nset.unitCanWriteTo(next, v)) {
                    if (!isConstant(next)) {
                        logger.debug("result = false unit can be written to by: " + next);
                        result = false;
                        break uses;
                    }
                }
            }

        }

        Iterator defBoxesIt = s.getDefBoxes().iterator();
        defs:
        while (defBoxesIt.hasNext()) {
            ValueBox vb = (ValueBox) defBoxesIt.next();
            Value v = vb.getValue();
            // new's are not invariant
            if (v instanceof NewExpr) {
                result = false;
                logger.debug("break defs due to new");
                break;
            }
            // invokes are not invariant
            if (v instanceof InvokeExpr) {
                result = false;
                logger.debug("break defs due to invoke");
                break;
            }
            // side effect tester doesn't handle expr
            if (v instanceof Expr) {
                continue;
            }

            logger.debug("test: " + v + " of kind: " + v.getClass());

            Iterator loopStmtsIt = loopStmts.iterator();
            while (loopStmtsIt.hasNext()) {
                Stmt next = (Stmt) loopStmtsIt.next();
                if (next.equals(s)) {
                    continue;
                }
                if (nset.unitCanWriteTo(next, v)) {
                    if (!isConstant(next)) {
                        logger.debug("result false: unit can be written to by: " + next);
                        result = false;
                        break defs;
                    }
                }
            }

        }
        logger.debug("stmt: " + s + " result: " + result);
        if (result) {
            s.addTag(new LoopInvariantTag("is loop invariant"));
            s.addTag(new ColorTag(ColorTag.RED, "Loop Invariant Analysis"));
        } else {
            // if loops are nested it might be invariant in one of them
            // so remove tag
            // if (s.hasTag(LoopInvariantTag.NAME)) {
            // s.removeTag(LoopInvariantTag.NAME);
            // }
        }
    }

    private boolean isConstant(Stmt s) {
        if (s instanceof DefinitionStmt ds) {
            return constants.contains(ds.getLeftOp());
        }
        return false;
    }
}
