package soot.jimple.toolkits.thread.synchronization;

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

import byteback.analysis.model.ClassModel;
import byteback.analysis.model.FieldModel;
import byteback.analysis.model.MethodModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.infoflow.FakeJimpleLocal;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.*;

public class LockAllocationBodyTransformer extends BodyTransformer {
    private static final Logger logger = LoggerFactory.getLogger(LockAllocationBodyTransformer.class);
    private static final LockAllocationBodyTransformer instance = new LockAllocationBodyTransformer();

    private LockAllocationBodyTransformer() {
    }

    public static LockAllocationBodyTransformer v() {
        return instance;
    }

    private static boolean addedGlobalLockDefs = false;
    private static int throwableNum = 0; // doesn't matter if not reinitialized
    // to 0

    protected void internalTransform(Body b, String phase, Map opts) {
        throw new RuntimeException("Not Supported");
    }

    protected void internalTransform(Body b, FlowSet fs, List<CriticalSectionGroup> groups, boolean[] insertedGlobalLock) {
        //
        JimpleBody j = (JimpleBody) b;
        MethodModel thisMethod = b.getMethod();
        PatchingChain<Unit> units = b.getUnits();
        Iterator<Unit> unitIt = units.iterator();
        Unit firstUnit = j.getFirstNonIdentityStmt();
        Unit lastUnit = units.getLast();

        // Objects of synchronization, plus book keeping
        Local[] lockObj = new Local[groups.size()];
        boolean[] addedLocalLockObj = new boolean[groups.size()];
        FieldModel[] globalLockObj = new FieldModel[groups.size()];
        for (int i = 1; i < groups.size(); i++) {
            lockObj[i] = Jimple.v().newLocal("lockObj" + i, RefType.v("java.lang.Object"));
            addedLocalLockObj[i] = false;
            globalLockObj[i] = null;
        }

        // Add all global lock objects to the main class if not yet added.
        // Get references to them if they do already exist.
        for (int i = 1; i < groups.size(); i++) {
            CriticalSectionGroup tnGroup = groups.get(i);
            if (!tnGroup.useDynamicLock && !tnGroup.useLocksets) {
                if (!insertedGlobalLock[i]) {
                    // Add globalLockObj field if possible...

                    // Avoid name collision... if it's already there, then just
                    // use it!
                    try {
                        globalLockObj[i] = Scene.v().getMainClass().getFieldByName("globalLockObj" + i);
                        // field already exists
                    } catch (RuntimeException re) {
                        // field does not yet exist (or, as a pre-existing
                        // error, there is more than one field by this name)
                        globalLockObj[i] = Scene.v().makeSootField("globalLockObj" + i, RefType.v("java.lang.Object"),
                                Modifier.STATIC | Modifier.PUBLIC);
                        Scene.v().getMainClass().addFieldModel(globalLockObj[i]);
                    }

                    insertedGlobalLock[i] = true;
                } else {
                    globalLockObj[i] = Scene.v().getMainClass().getFieldByName("globalLockObj" + i);
                }
            }
        }

        // If the current method is the clinit method of the main class, for
        // each global lock object,
        // add a local lock object and assign it a new object. Copy the new
        // local lock object into the global lock object for use by other fns.
        if (!addedGlobalLockDefs)// thisMethod.getSubSignature().equals("void
        // <clinit>()") &&
        // thisMethod.getDeclaringClass() ==
        // Scene.v().getMainClass())
        {
            // Either get or add the <clinit> method to the main class
            ClassModel mainClass = Scene.v().getMainClass();
            MethodModel clinitMethod = null;
            JimpleBody clinitBody = null;
            Stmt firstStmt = null;
            boolean addingNewClinit = !mainClass.declaresMethod("void <clinit>()");
            if (addingNewClinit) {
                clinitMethod
                        = Scene.v().makeSootMethod("<clinit>", new ArrayList(), VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
                clinitBody = Jimple.v().newBody(clinitMethod);
                clinitMethod.setActiveBody(clinitBody);
                mainClass.addMethod(clinitMethod);
            } else {
                clinitMethod = mainClass.getMethodModel("void <clinit>()");
                clinitBody = (JimpleBody) clinitMethod.getActiveBody();
                firstStmt = clinitBody.getFirstNonIdentityStmt();
            }
            PatchingChain<Unit> clinitUnits = clinitBody.getUnits();

            for (int i = 1; i < groups.size(); i++) {
                CriticalSectionGroup tnGroup = groups.get(i);
                // if( useGlobalLock[i - 1] )
                if (!tnGroup.useDynamicLock && !tnGroup.useLocksets) {
                    // add local lock obj
                    // addedLocalLockObj[i] = true;
                    clinitBody.getLocals().add(lockObj[i]); // TODO: add name
                    // conflict
                    // avoidance code

                    // assign new object to lock obj
                    Stmt newStmt = Jimple.v().newAssignStmt(lockObj[i], Jimple.v().newNewExpr(RefType.v("java.lang.Object")));
                    if (addingNewClinit) {
                        clinitUnits.add(newStmt);
                    } else {
                        clinitUnits.insertBeforeNoRedirect(newStmt, firstStmt);
                    }

                    // initialize new object
                    ClassModel objectClass = Scene.v().loadClassAndSupport("java.lang.Object");
                    RefType type = RefType.v(objectClass);
                    MethodModel initMethod = objectClass.getMethodModel("void <init>()");
                    Stmt initStmt = Jimple.v()
                            .newInvokeStmt(Jimple.v().newSpecialInvokeExpr(lockObj[i], initMethod.makeRef(), Collections.EMPTY_LIST));
                    if (addingNewClinit) {
                        clinitUnits.add(initStmt);
                    } else {
                        clinitUnits.insertBeforeNoRedirect(initStmt, firstStmt);
                    }

                    // copy new object to global static lock object (for use by
                    // other fns)
                    Stmt assignStmt = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(globalLockObj[i].makeRef()), lockObj[i]);
                    if (addingNewClinit) {
                        clinitUnits.add(assignStmt);
                    } else {
                        clinitUnits.insertBeforeNoRedirect(assignStmt, firstStmt);
                    }
                }
            }
            if (addingNewClinit) {
                clinitUnits.add(Jimple.v().newReturnVoidStmt());
            }
            addedGlobalLockDefs = true;
        }

        int tempNum = 1;
        // Iterate through all of the transactions in the current method
        Iterator fsIt = fs.iterator();
        Stmt newPrep = null;
        while (fsIt.hasNext()) {
            CriticalSection tn = ((SynchronizedRegionFlowPair) fsIt.next()).tn;
            if (tn.setNumber == -1) {
                continue; // this tn should be deleted... for now just skip it!
            }

            if (tn.wholeMethod) {
                thisMethod.setModifiers(thisMethod.getModifiers() & ~(Modifier.SYNCHRONIZED)); // remove
                // synchronized
                // modifier
                // for
                // this
                // method
            }

            Local clo = null; // depends on type of locking
            SynchronizedRegion csr = null; // current synchronized region
            int lockNum = 0;
            boolean moreLocks = true;
            while (moreLocks) {
                // If this method does not yet have a reference to the lock
                // object
                // needed for this transaction, then create one.
                if (tn.group.useDynamicLock) {
                    Value lock = getLockFor((EquivalentValue) tn.lockObject); // adds
                    // local
                    // vars
                    // and
                    // global
                    // objects
                    // if
                    // needed
                    if (lock instanceof Ref) {
                        if (lock instanceof InstanceFieldRef ifr) {
                          if (ifr.getBase() instanceof FakeJimpleLocal) {
                                lock = reconstruct(b, units, ifr, (tn.entermonitor != null ? tn.entermonitor : tn.beginning),
                                        (tn.entermonitor != null));
                            }
                        }
                        if (!b.getLocals().contains(lockObj[tn.setNumber])) {
                            b.getLocals().add(lockObj[tn.setNumber]);
                        }

                        newPrep = Jimple.v().newAssignStmt(lockObj[tn.setNumber], lock);
                        if (tn.wholeMethod) {
                            units.insertBeforeNoRedirect(newPrep, firstUnit);
                        } else {
                            units.insertBefore(newPrep, tn.entermonitor);
                        }
                        clo = lockObj[tn.setNumber];
                    } else if (lock instanceof Local) {
                        clo = (Local) lock;
                    } else {
                        throw new RuntimeException("Unknown type of lock (" + lock + "): expected Ref or Local");
                    }
                    csr = tn;
                    moreLocks = false;
                } else if (tn.group.useLocksets) {
                    Value lock = getLockFor(tn.lockset.get(lockNum)); // adds
                    // local
                    // vars
                    // and
                    // global
                    // objects
                    // if
                    // needed
                    if (lock instanceof FieldRef) {
                        if (lock instanceof InstanceFieldRef ifr) {
                          if (ifr.getBase() instanceof FakeJimpleLocal) {
                                lock = reconstruct(b, units, ifr, (tn.entermonitor != null ? tn.entermonitor : tn.beginning),
                                        (tn.entermonitor != null));
                            }
                        }
                        // add a local variable for this lock
                        Local lockLocal = Jimple.v().newLocal("locksetObj" + tempNum, RefType.v("java.lang.Object"));
                        tempNum++;
                        b.getLocals().add(lockLocal);

                        // make it refer to the right lock object
                        newPrep = Jimple.v().newAssignStmt(lockLocal, lock);
                        if (tn.entermonitor != null) {
                            units.insertBefore(newPrep, tn.entermonitor);
                        } else {
                            units.insertBeforeNoRedirect(newPrep, tn.beginning);
                        }

                        // use it as the lock
                        clo = lockLocal;
                    } else if (lock instanceof Local) {
                        clo = (Local) lock;
                    } else {
                        throw new RuntimeException("Unknown type of lock (" + lock + "): expected FieldRef or Local");
                    }

                    moreLocks = lockNum + 1 < tn.lockset.size();

                    if (lockNum > 0) {
                        SynchronizedRegion nsr = new SynchronizedRegion();

                        nsr.beginning = csr.beginning;
                        for (Pair earlyEnd : csr.earlyEnds) {
                            Stmt earlyExitmonitor = (Stmt) earlyEnd.getO2();
                            nsr.earlyEnds.add(new Pair(earlyExitmonitor, null)); // <early
                            // exitmonitor,
                            // null>
                        }
                        nsr.last = csr.last; // last stmt before exception
                        // handling
                        if (csr.end != null) {
                            Stmt endExitmonitor = csr.end.getO2();
                            nsr.after = endExitmonitor;
                        }

                        csr = nsr;
                    } else {
                        csr = tn;
                    }
                } else // global lock
                {
                    if (!addedLocalLockObj[tn.setNumber]) {
                        b.getLocals().add(lockObj[tn.setNumber]);
                    }
                    addedLocalLockObj[tn.setNumber] = true;
                    newPrep = Jimple.v().newAssignStmt(lockObj[tn.setNumber],
                            Jimple.v().newStaticFieldRef(globalLockObj[tn.setNumber].makeRef()));
                    if (tn.wholeMethod) {
                        units.insertBeforeNoRedirect(newPrep, firstUnit);
                    } else {
                        units.insertBefore(newPrep, tn.entermonitor);
                    }
                    clo = lockObj[tn.setNumber];
                    csr = tn;
                    moreLocks = false;
                }

                // Add synchronization code
                // For transactions from synchronized methods, use
                // synchronizeSingleEntrySingleExitBlock()
                // to add all necessary code (including ugly exception handling)
                // For transactions from synchronized blocks, simply replace the
                // monitorenter/monitorexit statements with new ones
                if (true) {
                    // Remove old prep stmt
                    if (csr.prepStmt != null) {
                        // units.remove(clr.prepStmt); // seems to trigger bugs
                        // in code generation?
                    }

                    // Reuse old entermonitor or insert new one, and insert prep
                    Stmt newEntermonitor = Jimple.v().newEnterMonitorStmt(clo);
                    if (csr.entermonitor != null) {
                        units.insertBefore(newEntermonitor, csr.entermonitor);
                        // redirectTraps(b, clr.entermonitor, newEntermonitor);
                        // // EXPERIMENTAL
                        units.remove(csr.entermonitor);
                        csr.entermonitor = newEntermonitor;

                        // units.insertBefore(newEntermonitor, newPrep); //
                        // already inserted
                        // clr.prepStmt = newPrep;
                    } else {
                        units.insertBeforeNoRedirect(newEntermonitor, csr.beginning);
                        csr.entermonitor = newEntermonitor;

                        // units.insertBefore(newEntermonitor, newPrep); //
                        // already inserted
                        // clr.prepStmt = newPrep;
                    }

                    // For each early end, reuse or insert exitmonitor stmt
                    List<Pair<Stmt, Stmt>> newEarlyEnds = new ArrayList<Pair<Stmt, Stmt>>();
                    for (Pair<Stmt, Stmt> end : csr.earlyEnds) {
                        Stmt earlyEnd = end.getO1();
                        Stmt exitmonitor = end.getO2();

                        Stmt newExitmonitor = Jimple.v().newExitMonitorStmt(clo);
                        if (exitmonitor != null) {
                            if (newPrep != null) {
                                Stmt tmp = (Stmt) newPrep.clone();
                                units.insertBefore(tmp, exitmonitor); // seems
                                // to
                                // avoid
                                // code
                                // generation
                                // bugs?
                            }
                            units.insertBefore(newExitmonitor, exitmonitor);
                            // redirectTraps(b, exitmonitor, newExitmonitor); //
                            // EXPERIMENTAL
                            units.remove(exitmonitor);
                            newEarlyEnds.add(new Pair<Stmt, Stmt>(earlyEnd, newExitmonitor));
                        } else {
                            if (newPrep != null) {
                                Stmt tmp = (Stmt) newPrep.clone();
                                units.insertBefore(tmp, earlyEnd);
                            }
                            units.insertBefore(newExitmonitor, earlyEnd);
                            newEarlyEnds.add(new Pair<Stmt, Stmt>(earlyEnd, newExitmonitor));
                        }
                    }
                    csr.earlyEnds = newEarlyEnds;

                    // If fallthrough end, reuse or insert goto and exit
                    if (csr.after != null) {
                        Stmt newExitmonitor = Jimple.v().newExitMonitorStmt(clo);
                        if (csr.end != null) {
                            Stmt exitmonitor = csr.end.getO2();

                            if (newPrep != null) {
                                Stmt tmp = (Stmt) newPrep.clone();
                                units.insertBefore(tmp, exitmonitor);
                            }
                            units.insertBefore(newExitmonitor, exitmonitor);
                            // redirectTraps(b, exitmonitor, newExitmonitor); //
                            // EXPERIMENTAL
                            units.remove(exitmonitor);
                            csr.end = new Pair<Stmt, Stmt>(csr.end.getO1(), newExitmonitor);
                        } else {
                            if (newPrep != null) {
                                Stmt tmp = (Stmt) newPrep.clone();
                                units.insertBefore(tmp, csr.after);
                            }
                            units.insertBefore(newExitmonitor, csr.after); // steal
                            // jumps
                            // to
                            // end,
                            // send
                            // them
                            // to
                            // monitorexit
                            Stmt newGotoStmt = Jimple.v().newGotoStmt(csr.after);
                            units.insertBeforeNoRedirect(newGotoStmt, csr.after);
                            csr.end = new Pair<Stmt, Stmt>(newGotoStmt, newExitmonitor);
                            csr.last = newGotoStmt;
                        }
                    }

                    // If exceptional end, reuse it, else insert it and traps
                    Stmt newExitmonitor = Jimple.v().newExitMonitorStmt(clo);
                    if (csr.exceptionalEnd != null) {
                        Stmt exitmonitor = csr.exceptionalEnd.getO2();

                        if (newPrep != null) {
                            Stmt tmp = (Stmt) newPrep.clone();
                            units.insertBefore(tmp, exitmonitor);
                        }
                        units.insertBefore(newExitmonitor, exitmonitor);

                        units.remove(exitmonitor);
                        csr.exceptionalEnd = new Pair<Stmt, Stmt>(csr.exceptionalEnd.getO1(), newExitmonitor);
                    } else {
                        // insert after the last end
                        Stmt lastEnd = null; // last end stmt (not same as last
                        // stmt)
                        if (csr.end != null) {
                            lastEnd = csr.end.getO1();
                        } else {
                            for (Pair earlyEnd : csr.earlyEnds) {
                                Stmt end = (Stmt) earlyEnd.getO1();
                                if (lastEnd == null || (units.contains(lastEnd) && units.contains(end) && units.follows(end, lastEnd))) {
                                    lastEnd = end;
                                }
                            }
                        }
                        if (csr.last == null) {
                            csr.last = lastEnd; // last stmt and last end are
                        }
                        // the same
                        if (lastEnd == null) {
                            throw new RuntimeException("Lock Region has no ends!  Where should we put the exception handling???");
                        }

                        // Add throwable
                        Local throwableLocal
                                = Jimple.v().newLocal("throwableLocal" + (throwableNum++), RefType.v("java.lang.Throwable"));
                        b.getLocals().add(throwableLocal);
                        // Add stmts
                        Stmt newCatch = Jimple.v().newIdentityStmt(throwableLocal, Jimple.v().newCaughtExceptionRef());
                        if (csr.last == null) {
                            throw new RuntimeException("WHY IS clr.last NULL???");
                        }
                        if (newCatch == null) {
                            throw new RuntimeException("WHY IS newCatch NULL???");
                        }
                        units.insertAfter(newCatch, csr.last);
                        units.insertAfter(newExitmonitor, newCatch);
                        Stmt newThrow = Jimple.v().newThrowStmt(throwableLocal);
                        units.insertAfter(newThrow, newExitmonitor);
                        // Add traps
                        ClassModel throwableClass = Scene.v().loadClassAndSupport("java.lang.Throwable");
                        b.getTraps().addFirst(Jimple.v().newTrap(throwableClass, newExitmonitor, newThrow, newCatch));
                        b.getTraps().addFirst(Jimple.v().newTrap(throwableClass, csr.beginning, lastEnd, newCatch));
                        csr.exceptionalEnd = new Pair<Stmt, Stmt>(newThrow, newExitmonitor);
                    }
                }
                lockNum++;
            }

            // deal with waits and notifys
            {
                for (Unit uNotify : tn.notifys) {
                    Stmt sNotify = (Stmt) uNotify;
                    Stmt newNotify = Jimple.v()
                            .newInvokeStmt(Jimple.v().newVirtualInvokeExpr(clo,
                                    sNotify.getInvokeExpr().getMethodRef().declaringClass().getMethodModel("void notifyAll()").makeRef(),
                                    Collections.EMPTY_LIST));
                    if (newPrep != null) {
                        Stmt tmp = (Stmt) newPrep.clone();
                        units.insertBefore(tmp, sNotify);
                        units.insertBefore(newNotify, tmp);
                    } else {
                        units.insertBefore(newNotify, sNotify);
                    }

                    redirectTraps(b, sNotify, newNotify);
                    units.remove(sNotify);
                }

                // Replace base object of calls to wait with appropriate lockobj
                for (Unit uWait : tn.waits) {
                    Stmt sWait = (Stmt) uWait;
                    ((InstanceInvokeExpr) sWait.getInvokeExpr()).setBase(clo); // WHAT
                    // IF
                    // THIS
                    // IS
                    // THE
                    // WRONG
                    // LOCK
                    // IN
                    // A
                    // PAIR
                    // OF
                    // NESTED
                    // LOCKS???
                    if (newPrep != null) {
                        units.insertBefore((Stmt) newPrep.clone(), sWait);
                    }
                }
            }
        }
    }

    static int baseLocalNum = 0;

    public InstanceFieldRef reconstruct(Body b, PatchingChain<Unit> units, InstanceFieldRef lock, Stmt insertBefore,
                                        boolean redirect) {
        logger.debug("Reconstructing " + lock);

        if (!(lock.getBase() instanceof FakeJimpleLocal fakeBase)) {
            logger.debug("  base is not a FakeJimpleLocal");
            return lock;
        }

      if (!(fakeBase.getInfo() instanceof LockableReferenceAnalysis la)) {
            throw new RuntimeException("InstanceFieldRef cannot be reconstructed due to missing LocksetAnalysis info: " + lock);
        }

      EquivalentValue baseEqVal = la.baseFor(lock);
        if (baseEqVal == null) {
            throw new RuntimeException("InstanceFieldRef cannot be reconstructed due to lost base from Lockset");
        }
        Value base = baseEqVal.getValue();
        Local baseLocal;
        if (base instanceof InstanceFieldRef) {
            Value newBase = reconstruct(b, units, (InstanceFieldRef) base, insertBefore, redirect);
            baseLocal = Jimple.v().newLocal("baseLocal" + (baseLocalNum++), newBase.getType());
            b.getLocals().add(baseLocal);

            // make it equal to the right value
            Stmt baseAssign = Jimple.v().newAssignStmt(baseLocal, newBase);
            if (redirect) {
                units.insertBefore(baseAssign, insertBefore);
            } else {
                units.insertBeforeNoRedirect(baseAssign, insertBefore);
            }
        } else if (base instanceof Local) {
            baseLocal = (Local) base;
        } else {
            throw new RuntimeException("InstanceFieldRef cannot be reconstructed because it's base is of an unsupported type"
                    + base.getType() + ": " + base);
        }

        InstanceFieldRef newLock = Jimple.v().newInstanceFieldRef(baseLocal, lock.getField().makeRef());
        logger.debug("  as " + newLock);
        return newLock;
    }

    static int lockNumber = 0;
    static Map<EquivalentValue, StaticFieldRef> lockEqValToLock = new HashMap<EquivalentValue, StaticFieldRef>();

    static public Value getLockFor(EquivalentValue lockEqVal) {
        Value lock = lockEqVal.getValue();

        if (lock instanceof InstanceFieldRef) {
            return lock;
        }

        if (lock instanceof ArrayRef) {
            // ref for each value of the index!
            return ((ArrayRef) lock).getBase();
        }

        if (lock instanceof Local) {
            return lock;
        }

        if (lock instanceof StaticFieldRef || lock instanceof NewStaticLock) {
            if (lockEqValToLock.containsKey(lockEqVal)) {
                return lockEqValToLock.get(lockEqVal);
            }

            ClassModel lockClass = null;
            if (lock instanceof StaticFieldRef sfrLock) {
              lockClass = sfrLock.getField().getDeclaringClass();
            } else if (lock instanceof NewStaticLock) {
                DeadlockAvoidanceEdge dae = (DeadlockAvoidanceEdge) lock;
                lockClass = dae.getLockClass();
            }
            MethodModel clinitMethod = null;
            JimpleBody clinitBody = null;
            Stmt firstStmt = null;
            boolean addingNewClinit = !lockClass.declaresMethod("void <clinit>()");
            if (addingNewClinit) {
                clinitMethod
                        = Scene.v().makeSootMethod("<clinit>", new ArrayList(), VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
                clinitBody = Jimple.v().newBody(clinitMethod);
                clinitMethod.setActiveBody(clinitBody);
                lockClass.addMethod(clinitMethod);
            } else {
                clinitMethod = lockClass.getMethodModel("void <clinit>()");
                clinitBody = (JimpleBody) clinitMethod.getActiveBody();
                firstStmt = clinitBody.getFirstNonIdentityStmt();
            }
            PatchingChain<Unit> clinitUnits = clinitBody.getUnits();

            Local lockLocal = Jimple.v().newLocal("objectLockLocal" + lockNumber, RefType.v("java.lang.Object"));
            // lockNumber is increased below
            clinitBody.getLocals().add(lockLocal); // TODO: add name conflict
            // avoidance code

            // assign new object to lock obj
            Stmt newStmt = Jimple.v().newAssignStmt(lockLocal, Jimple.v().newNewExpr(RefType.v("java.lang.Object")));
            if (addingNewClinit) {
                clinitUnits.add(newStmt);
            } else {
                clinitUnits.insertBeforeNoRedirect(newStmt, firstStmt);
            }

            // initialize new object
            ClassModel objectClass = Scene.v().loadClassAndSupport("java.lang.Object");
            RefType type = RefType.v(objectClass);
            MethodModel initMethod = objectClass.getMethodModel("void <init>()");
            Stmt initStmt = Jimple.v()
                    .newInvokeStmt(Jimple.v().newSpecialInvokeExpr(lockLocal, initMethod.makeRef(), Collections.EMPTY_LIST));
            if (addingNewClinit) {
                clinitUnits.add(initStmt);
            } else {
                clinitUnits.insertBeforeNoRedirect(initStmt, firstStmt);
            }

            // copy new object to global static lock object (for use by other
            // fns)
            FieldModel actualLockObject = Scene.v().makeSootField("objectLockGlobal" + lockNumber, RefType.v("java.lang.Object"),
                    Modifier.STATIC | Modifier.PUBLIC);
            lockNumber++;
            lockClass.addFieldModel(actualLockObject);

            StaticFieldRef actualLockSfr = Jimple.v().newStaticFieldRef(actualLockObject.makeRef());
            Stmt assignStmt = Jimple.v().newAssignStmt(actualLockSfr, lockLocal);
            if (addingNewClinit) {
                clinitUnits.add(assignStmt);
            } else {
                clinitUnits.insertBeforeNoRedirect(assignStmt, firstStmt);
            }

            if (addingNewClinit) {
                clinitUnits.add(Jimple.v().newReturnVoidStmt());
            }

            lockEqValToLock.put(lockEqVal, actualLockSfr);
            return actualLockSfr;
        }

        throw new RuntimeException("Unknown type of lock (" + lock + "): expected FieldRef, ArrayRef, or Local");
    }

    public void redirectTraps(Body b, Unit oldUnit, Unit newUnit) {
        Chain<Trap> traps = b.getTraps();
        for (Trap trap : traps) {
            if (trap.getHandlerUnit() == oldUnit) {
                trap.setHandlerUnit(newUnit);
            }
            if (trap.getBeginUnit() == oldUnit) {
                trap.setBeginUnit(newUnit);
            }
            if (trap.getEndUnit() == oldUnit) {
                trap.setEndUnit(newUnit);
            }
        }
    }

}
