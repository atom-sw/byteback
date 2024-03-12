package soot;

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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.jimple.spark.pag.MethodPAG;
import soot.jimple.spark.pag.Parm;
import soot.jimple.spark.sets.P2SetFactory;
import soot.jimple.toolkits.annotation.arraycheck.Array2ndDimensionSymbol;
import soot.jimple.toolkits.pointer.UnionFactory;
import soot.jimple.toolkits.typing.ClassHierarchy;
import soot.toolkits.scalar.Pair;

/** A class to group together all the global variables in Soot. */
public class G extends Singletons {

  public static interface GlobalObjectGetter {
    G getG();
    void reset();
  }

  public static G v() {
    return objectGetter.getG();
  }

  public static void reset() {
    objectGetter.reset();
  }

  private static GlobalObjectGetter objectGetter = new GlobalObjectGetter() {

    private G instance = new G();

    @Override
    public G getG() {
      return instance;
    }

    @Override
    public void reset() {
      instance = new G();
    }
  };

  /**
   * Deprecated use logging via slf4j instead
   */
  @Deprecated
  public PrintStream out = System.out;

  public class Global {
  }

  public P2SetFactory newSetFactory;
  public P2SetFactory oldSetFactory;
  public Map<Pair<SootMethod, Integer>, Parm> Parm_pairToElement = new HashMap<Pair<SootMethod, Integer>, Parm>();
  public int SparkNativeHelper_tempVar = 0;
  public boolean PointsToSetInternal_warnedAlready = false;
  public HashMap<SootMethod, MethodPAG> MethodPAG_methodToPag = new HashMap<SootMethod, MethodPAG>();
  public Set MethodRWSet_allGlobals = new HashSet();
  public Set MethodRWSet_allFields = new HashSet();
  public int GeneralConstObject_counter = 0;
  public UnionFactory Union_factory = null;
  public HashMap<Object, Array2ndDimensionSymbol> Array2ndDimensionSymbol_pool
      = new HashMap<Object, Array2ndDimensionSymbol>();
  public List<Timer> Timer_outstandingTimers = new ArrayList<Timer>();
  public boolean Timer_isGarbageCollecting;
  public Timer Timer_forcedGarbageCollectionTimer = new Timer("gc");
  public int Timer_count;
  public final Map<Scene, ClassHierarchy> ClassHierarchy_classHierarchyMap = new HashMap<Scene, ClassHierarchy>();
  public final Map<MethodContext, MethodContext> MethodContext_map = new HashMap<MethodContext, MethodContext>();
}
