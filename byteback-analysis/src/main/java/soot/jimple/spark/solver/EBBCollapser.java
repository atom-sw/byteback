package soot.jimple.spark.solver;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Type;
import soot.jimple.spark.internal.TypeManager;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.FieldRefNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.VarNode;

/**
 * Collapses nodes that are members of simple trees (EBBs) in the pointer assignment graph.
 * 
 * @author Ondrej Lhotak
 */

public class EBBCollapser {
  private static final Logger logger = LoggerFactory.getLogger(EBBCollapser.class);

  /** Actually collapse the EBBs in the PAG. */
  public void collapse() {
    boolean verbose = pag.getOpts().verbose();
    if (verbose) {
      logger.debug("" + "Total VarNodes: " + pag.getVarNodeNumberer().size() + ". Collapsing EBBs...");
    }
    collapseAlloc();
    collapseLoad();
    collapseSimple();
    if (verbose) {
      logger.debug("" + "" + numCollapsed + " nodes were collapsed.");
    }
  }

  public EBBCollapser(PAG pag) {
    this.pag = pag;
  }

  /* End of public methods. */
  /* End of package methods. */

  protected int numCollapsed = 0;
  protected PAG pag;

  protected void collapseAlloc() {
    final boolean ofcg = (pag.getOnFlyCallGraph() != null);
    for (Object object : pag.allocSources()) {
      final AllocNode n = (AllocNode) object;
      Node[] succs = pag.allocLookup(n);
      VarNode firstSucc = null;
      for (Node element0 : succs) {
        VarNode succ = (VarNode) element0;
        if ((pag.allocInvLookup(succ).length > 1) || (pag.loadInvLookup(succ).length > 0)
            || (pag.simpleInvLookup(succ).length > 0)) {
          continue;
        }
        if (ofcg && succ.isInterProcTarget()) {
          continue;
        }
        if (firstSucc == null) {
          firstSucc = succ;
        } else {
          if (firstSucc.getType().equals(succ.getType())) {
            firstSucc.mergeWith(succ);
            numCollapsed++;
          }
        }
      }
    }
  }

  protected void collapseSimple() {
    final boolean ofcg = (pag.getOnFlyCallGraph() != null);
    final TypeManager typeManager = pag.getTypeManager();
    boolean change;
    do {
      change = false;
      for (Iterator<Object> nIt = new ArrayList<Object>(pag.simpleSources()).iterator(); nIt.hasNext();) {
        final VarNode n = (VarNode) nIt.next();
        Type nType = n.getType();
        Node[] succs = pag.simpleLookup(n);
        for (Node element : succs) {
          VarNode succ = (VarNode) element;
          Type sType = succ.getType();
          if (!typeManager.castNeverFails(nType, sType) || (pag.allocInvLookup(succ).length > 0)
              || (pag.loadInvLookup(succ).length > 0) || (pag.simpleInvLookup(succ).length > 1)) {
            continue;
          }
          if (ofcg && (succ.isInterProcTarget() || n.isInterProcSource())) {
            continue;
          }
          n.mergeWith(succ);
          change = true;
          numCollapsed++;
        }
      }
    } while (change);
  }

  protected void collapseLoad() {
    final boolean ofcg = (pag.getOnFlyCallGraph() != null);
    final TypeManager typeManager = pag.getTypeManager();
    for (Iterator<Object> nIt = new ArrayList<Object>(pag.loadSources()).iterator(); nIt.hasNext();) {
      final FieldRefNode n = (FieldRefNode) nIt.next();
      Type nType = n.getType();
      Node[] succs = pag.loadLookup(n);
      Node firstSucc = null;
      HashMap<Type, VarNode> typeToSucc = new HashMap<Type, VarNode>();
      for (Node element : succs) {
        VarNode succ = (VarNode) element;
        Type sType = succ.getType();
        if ((pag.allocInvLookup(succ).length > 0) || (pag.loadInvLookup(succ).length > 1)
            || (pag.simpleInvLookup(succ).length > 0)) {
          continue;
        }
        if (ofcg && succ.isInterProcTarget()) {
          continue;
        }
        if (typeManager.castNeverFails(nType, sType)) {
          if (firstSucc == null) {
            firstSucc = succ;
          } else {
            firstSucc.mergeWith(succ);
            numCollapsed++;
          }
        } else {
          VarNode rep = typeToSucc.get(succ.getType());
          if (rep == null) {
            typeToSucc.put(succ.getType(), succ);
          } else {
            rep.mergeWith(succ);
            numCollapsed++;
          }
        }
      }
    }
  }
}
