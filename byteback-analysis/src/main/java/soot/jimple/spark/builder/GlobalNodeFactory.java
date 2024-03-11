package soot.jimple.spark.builder;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.AnySubType;
import soot.ArrayType;
import soot.PointsToAnalysis;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.ArrayElement;
import soot.jimple.spark.pag.ContextVarNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.VarNode;
import soot.toolkits.scalar.Pair;

/**
 * Factory for nodes not specific to a given method.
 *
 * @author Ondrej Lhotak
 */
public class GlobalNodeFactory {

  protected final RefType rtObject;
  protected final RefType rtClassLoader;
  protected final RefType rtString;
  protected final RefType rtThread;
  protected final RefType rtThreadGroup;
  protected final RefType rtThrowable;

  public GlobalNodeFactory(PAG pag) {
    this.pag = pag;

    this.rtObject = Scene.v().getObjectType();
    this.rtClassLoader = RefType.v("java.lang.ClassLoader");
    this.rtString = RefType.v("java.lang.String");
    this.rtThread = RefType.v("java.lang.Thread");
    this.rtThreadGroup = RefType.v("java.lang.ThreadGroup");
    this.rtThrowable = Scene.v().getBaseExceptionType();
  }

  final public Node caseDefaultClassLoader() {
    AllocNode a = pag.makeAllocNode(PointsToAnalysis.DEFAULT_CLASS_LOADER, AnySubType.v(rtClassLoader), null);
    VarNode v = pag.makeGlobalVarNode(PointsToAnalysis.DEFAULT_CLASS_LOADER_LOCAL, rtClassLoader);
    pag.addEdge(a, v);
    return v;
  }

  final public Node caseMainClassNameString() {
    AllocNode a = pag.makeAllocNode(PointsToAnalysis.MAIN_CLASS_NAME_STRING, rtString, null);
    VarNode v = pag.makeGlobalVarNode(PointsToAnalysis.MAIN_CLASS_NAME_STRING_LOCAL, rtString);
    pag.addEdge(a, v);
    return v;
  }

  final public Node caseMainThreadGroup() {
    AllocNode threadGroupNode = pag.makeAllocNode(PointsToAnalysis.MAIN_THREAD_GROUP_NODE, rtThreadGroup, null);
    VarNode threadGroupNodeLocal = pag.makeGlobalVarNode(PointsToAnalysis.MAIN_THREAD_GROUP_NODE_LOCAL, rtThreadGroup);
    pag.addEdge(threadGroupNode, threadGroupNodeLocal);
    return threadGroupNodeLocal;
  }

  final public Node casePrivilegedActionException() {
    AllocNode a = pag.makeAllocNode(PointsToAnalysis.PRIVILEGED_ACTION_EXCEPTION,
        AnySubType.v(RefType.v("java.security.PrivilegedActionException")), null);
    VarNode v = pag.makeGlobalVarNode(PointsToAnalysis.PRIVILEGED_ACTION_EXCEPTION_LOCAL,
        RefType.v("java.security.PrivilegedActionException"));
    pag.addEdge(a, v);
    return v;
  }

  final public Node caseCanonicalPath() {
    AllocNode a = pag.makeAllocNode(PointsToAnalysis.CANONICAL_PATH, rtString, null);
    VarNode v = pag.makeGlobalVarNode(PointsToAnalysis.CANONICAL_PATH_LOCAL, rtString);
    pag.addEdge(a, v);
    return v;
  }

  final public Node caseMainThread() {
    AllocNode threadNode = pag.makeAllocNode(PointsToAnalysis.MAIN_THREAD_NODE, rtThread, null);
    VarNode threadNodeLocal = pag.makeGlobalVarNode(PointsToAnalysis.MAIN_THREAD_NODE_LOCAL, rtThread);
    pag.addEdge(threadNode, threadNodeLocal);
    return threadNodeLocal;
  }

  final public Node caseFinalizeQueue() {
    return pag.makeGlobalVarNode(PointsToAnalysis.FINALIZE_QUEUE, rtObject);
  }

  final public Node caseArgv() {
    ArrayType strArray = ArrayType.v(rtString, 1);
    AllocNode argv = pag.makeAllocNode(PointsToAnalysis.STRING_ARRAY_NODE, strArray, null);
    VarNode sanl = pag.makeGlobalVarNode(PointsToAnalysis.STRING_ARRAY_NODE_LOCAL, strArray);
    AllocNode stringNode = pag.makeAllocNode(PointsToAnalysis.STRING_NODE, rtString, null);
    VarNode stringNodeLocal = pag.makeGlobalVarNode(PointsToAnalysis.STRING_NODE_LOCAL, rtString);
    pag.addEdge(argv, sanl);
    pag.addEdge(stringNode, stringNodeLocal);
    pag.addEdge(stringNodeLocal, pag.makeFieldRefNode(sanl, ArrayElement.v()));
    return sanl;
  }

  final public Node caseNewInstance(VarNode cls) {
    if (cls instanceof ContextVarNode) {
      cls = pag.findLocalVarNode(cls.getVariable());
    }
    VarNode local = pag.makeGlobalVarNode(cls, rtObject);
    for (SootClass cl : Scene.v().dynamicClasses()) {
      AllocNode site = pag.makeAllocNode(new Pair<VarNode, SootClass>(cls, cl), cl.getType(), null);
      pag.addEdge(site, local);
    }
    return local;
  }

  public Node caseThrow() {
    VarNode ret = pag.makeGlobalVarNode(PointsToAnalysis.EXCEPTION_NODE, rtThrowable);
    ret.setInterProcTarget();
    ret.setInterProcSource();
    return ret;
  }
  /* End of public methods. */
  /* End of package methods. */

  protected PAG pag;
}
