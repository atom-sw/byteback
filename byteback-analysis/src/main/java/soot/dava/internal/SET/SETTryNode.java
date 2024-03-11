package soot.dava.internal.SET;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import soot.Value;
import soot.dava.DavaBody;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.asg.AugmentedStmtGraph;
import soot.dava.toolkits.base.finders.ExceptionFinder;
import soot.dava.toolkits.base.finders.ExceptionNode;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.Stmt;
import soot.util.IterableSet;

public class SETTryNode extends SETNode {
  private ExceptionNode en;
  private DavaBody davaBody;
  private AugmentedStmtGraph asg;
  private final HashMap<IterableSet, IterableSet> cb2clone;

  public SETTryNode(IterableSet body, ExceptionNode en, AugmentedStmtGraph asg, DavaBody davaBody) {
    super(body);
    this.en = en;
    this.asg = asg;
    this.davaBody = davaBody;

    add_SubBody(en.get_TryBody());

    cb2clone = new HashMap<IterableSet, IterableSet>();

    Iterator it = en.get_CatchList().iterator();
    while (it.hasNext()) {
      IterableSet catchBody = (IterableSet) it.next();
      IterableSet clone = (IterableSet) catchBody.clone();

      cb2clone.put(catchBody, clone);
      add_SubBody(clone);
    }

    getEntryStmt: {
      entryStmt = null;

      it = body.iterator();
      while (it.hasNext()) {
        AugmentedStmt as = (AugmentedStmt) it.next();

        Iterator pit = as.cpreds.iterator();
        while (pit.hasNext()) {
          if (!body.contains(pit.next())) {
            entryStmt = as;
            break getEntryStmt;
          }
        }
      }
    }
  }

  public AugmentedStmt get_EntryStmt() {
    if (entryStmt != null) {
      return entryStmt;
    } else {
      return (AugmentedStmt) (en.get_TryBody()).getFirst();
    }

    // return ((SETNode) ((IterableSet) body2childChain.get(
    // en.get_TryBody())).getFirst()).get_EntryStmt();
  }

  public IterableSet get_NaturalExits() {
    IterableSet c = new IterableSet();

    Iterator<IterableSet> it = subBodies.iterator();
    while (it.hasNext()) {

      Iterator eit = ((SETNode) body2childChain.get(it.next()).getLast()).get_NaturalExits().iterator();
      while (eit.hasNext()) {
        Object o = eit.next();

        if (!c.contains(o)) {
          c.add(o);
        }
      }
    }

    return c;
  }

  public ASTNode emit_AST() {
    LinkedList<Object> catchList = new LinkedList<Object>();
    HashMap<Object, Object> exceptionMap = new HashMap<Object, Object>(), paramMap = new HashMap<Object, Object>();

    Iterator it = en.get_CatchList().iterator();
    while (it.hasNext()) {
      IterableSet originalCatchBody = (IterableSet) it.next();
      IterableSet catchBody = cb2clone.get(originalCatchBody);

      List<Object> astBody = emit_ASTBody(body2childChain.get(catchBody));
      exceptionMap.put(astBody, en.get_Exception(originalCatchBody));
      catchList.addLast(astBody);

      Iterator bit = catchBody.iterator();
      while (bit.hasNext()) {
        Stmt s = ((AugmentedStmt) bit.next()).get_Stmt();

        /*
         * 04.04.2006 mbatch if an implicit try due to a finally block, make sure to get the exception identifier from the
         * goto target (it's a different block)
         */

        // TODO: HOW the heck do you handle finallys with NO finally?
        // Semantics are
        // technically incorrect here
        if (s instanceof GotoStmt) {
          s = (Stmt) ((GotoStmt) s).getTarget();
          /* 04.04.2006 mbatch end */
        }

        if (s instanceof IdentityStmt) {
          IdentityStmt ids = (IdentityStmt) s;

          Value rightOp = ids.getRightOp(), leftOp = ids.getLeftOp();

          if (rightOp instanceof CaughtExceptionRef) {
            paramMap.put(astBody, leftOp);
            break;
          }
        }
      }
    }

    return new ASTTryNode(get_Label(), emit_ASTBody(body2childChain.get(en.get_TryBody())), catchList, exceptionMap,
        paramMap);
  }

  protected boolean resolve(SETNode parent) {
    Iterator<IterableSet> sbit = parent.get_SubBodies().iterator();
    while (sbit.hasNext()) {

      IterableSet subBody = sbit.next();
      if (subBody.intersects(en.get_TryBody())) {

        IterableSet childChain = parent.get_Body2ChildChain().get(subBody);
        Iterator ccit = childChain.iterator();
        while (ccit.hasNext()) {

          SETNode child = (SETNode) ccit.next();
          IterableSet childBody = child.get_Body();

          if (!childBody.intersects(en.get_TryBody()) || (childBody.isSubsetOf(en.get_TryBody()))) {
            continue;
          }

          if (childBody.isSupersetOf(get_Body())) {
            return true;
          }

          IterableSet newTryBody = childBody.intersection(en.get_TryBody());
          if (newTryBody.isStrictSubsetOf(en.get_TryBody())) {
            en.splitOff_ExceptionNode(newTryBody, asg, davaBody.get_ExceptionFacts());

            Iterator enlit = davaBody.get_ExceptionFacts().iterator();
            while (enlit.hasNext()) {
              ((ExceptionNode) enlit.next()).refresh_CatchBody(ExceptionFinder.v());
            }

            return false;
          }

          Iterator cit = en.get_CatchList().iterator();
          while (cit.hasNext()) {

            Iterator bit = cb2clone.get(cit.next()).snapshotIterator();
            while (bit.hasNext()) {
              AugmentedStmt as = (AugmentedStmt) bit.next();

              if (!childBody.contains(as)) {
                remove_AugmentedStmt(as);
              } else if ((child instanceof SETControlFlowNode) && !(child instanceof SETUnconditionalWhileNode)) {
                SETControlFlowNode scfn = (SETControlFlowNode) child;

                if ((scfn.get_CharacterizingStmt() == as) || ((as.cpreds.size() == 1) && (as.get_Stmt() instanceof GotoStmt)
                    && (scfn.get_CharacterizingStmt() == as.cpreds.get(0)))) {
                  remove_AugmentedStmt(as);
                }
              }
            }
          }

          return true;
        }
      }
    }
    return true;
  }
}
