package soot.jimple.toolkits.typing;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2000 Etienne Gagnon.
 * Copyright (C) 2008 Ben Bellamy
 * Copyright (C) 2008 Eric Bodden
 *
 * All rights reserved.
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
import soot.jimple.toolkits.scalar.ConstantPropagatorAndFolder;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.jimple.toolkits.scalar.LocalCreation;
import soot.options.JBTROptions;
import soot.options.Options;
import soot.toolkits.scalar.UnusedLocalEliminator;

import java.util.*;

/**
 * This transformer assigns types to local variables.
 *
 * @author Etienne Gagnon
 * @author Ben Bellamy
 * @author Eric Bodden
 */
public class TypeAssigner extends BodyTransformer {
    private static final Logger logger = LoggerFactory.getLogger(TypeAssigner.class);

    public TypeAssigner(Singletons.Global g) {
    }

    public static TypeAssigner v() {
        return G.v().soot_jimple_toolkits_typing_TypeAssigner();
    }

    /**
     * Assign types to local variables. *
     */
    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        if (b == null) {
            throw new NullPointerException();
        }

        final Date start;
        if (Options.v().verbose()) {
            start = new Date();
            logger.debug("[TypeAssigner] typing system started on " + start);
        } else {
            start = null;
        }

        final JBTROptions opt = new JBTROptions(options);
        final JimpleBody jb = (JimpleBody) b;

        //
        // Setting this guard to true enables comparison of the original and new type assigners.
        // This will be slow since type assignment will always happen twice. The actual types
        // used for Jimple are determined by the use-old-type-assigner option.
        //
        // Each comparison is written as a separate semicolon-delimited line to the standard
        // output, and the first field is always 'cmp' for use in grep. The format is:
        //
        // cmp;Method Name;Stmt Count;Old Inference Time (ms); New Inference Time (ms);Typing Comparison
        //
        // The Typing Comparison field compares the old and new typings:
        // -2 = Old typing contains fewer variables (BAD!)
        // -1 = Old typing is tighter (BAD!)
        // 0 = Typings are equal
        // 1 = New typing is tighter
        // 2 = New typing contains fewer variables
        // 3 = Typings are incomparable (inspect manually)
        //
        // In a final release this guard, and anything in the first branch, would probably be removed.
        //
        if (opt.compare_type_assigners()) {
            compareTypeAssigners(jb, opt.use_older_type_assigner());
        } else {
            if (opt.use_older_type_assigner()) {
                soot.jimple.toolkits.typing.TypeResolver.resolve(jb, Scene.v());
            } else {
                (new soot.jimple.toolkits.typing.fast.TypeResolver(jb)).inferTypes();
            }
        }

        if (Options.v().verbose()) {
            Date finish = new Date();
            long runtime = finish.getTime() - start.getTime();
            long mins = runtime / 60000;
            long secs = (runtime % 60000) / 1000;
            logger.debug("[TypeAssigner] typing system ended. It took " + mins + " mins and " + secs + " secs.");
        }

        if (!opt.ignore_nullpointer_dereferences()) {
            replaceNullType(jb);
        }

        if (typingFailed(jb)) {
            throw new RuntimeException("type inference failed!");
        }
    }

    /**
     * Insert a runtime exception before unit u of body b. Useful to analyze broken code (which make reference to inexisting
     * class for instance) exceptionType: e.g., "java.lang.RuntimeException"
     */
    public static void addExceptionAfterUnit(Body b, String exceptionType, Unit u, String m) {
        LocalCreation lc = Scene.v().createLocalCreation(b.getLocals());
        Local l = lc.newLocal(RefType.v(exceptionType));

        List<Unit> newUnits = new ArrayList<Unit>();
        Unit u1 = Jimple.v().newAssignStmt(l, Jimple.v().newNewExpr(RefType.v(exceptionType)));
        Unit u2
                = Jimple.v()
                .newInvokeStmt(Jimple.v().newSpecialInvokeExpr(l,
                        Scene.v().makeMethodRef(Scene.v().getSootClass(exceptionType), "<init>",
                                Collections.singletonList(RefType.v("java.lang.String")), VoidType.v(), false),
                        StringConstant.v(m)));
        Unit u3 = Jimple.v().newThrowStmt(l);
        newUnits.add(u1);
        newUnits.add(u2);
        newUnits.add(u3);

        b.getUnits().insertBefore(newUnits, u);
    }

    /**
     * Replace statements using locals with null_type type and that would throw a NullPointerException at runtime by a set of
     * instructions throwing a NullPointerException.
     * <p>
     * This is done to remove locals with null_type type.
     *
     * @param b
     */
    protected static void replaceNullType(Body b) {
        // check if any local has null_type
        boolean hasNullType = false;
        for (Local l : b.getLocals()) {
            if (l.getType() instanceof NullType) {
                hasNullType = true;
                break;
            }
        }

        // No local with null_type
        if (!hasNullType) {
            return;
        }

        // force to propagate null constants
        Map<String, String> opts = PhaseOptions.v().getPhaseOptions("jop.cpf");
        if (!opts.containsKey("enabled") || !"true".equals(opts.get("enabled"))) {
            logger.warn("Cannot run TypeAssigner.replaceNullType(Body). Try to enable jop.cfg.");
            return;
        }
        ConstantPropagatorAndFolder.v().transform(b);

        List<Unit> unitToReplaceByException = new ArrayList<Unit>();
        for (Unit u : b.getUnits()) {
            Stmt s = (Stmt) u;
            for (ValueBox vb : u.getUseBoxes()) {
                Value value = vb.getValue();
                if (value instanceof Local && value.getType() instanceof NullType) {

                    boolean replace = false;
                    if (s.containsArrayRef()) {
                        ArrayRef r = s.getArrayRef();
                        if (r.getBase() == value) {
                            replace = true;
                        }
                    } else if (s.containsFieldRef()) {
                        FieldRef r = s.getFieldRef();
                        if (r instanceof InstanceFieldRef ir) {
                            if (ir.getBase() == value) {
                                replace = true;
                            }
                        }
                    } else if (s.containsInvokeExpr()) {
                        InvokeExpr ie = s.getInvokeExpr();
                        if (ie instanceof InstanceInvokeExpr iie) {
                            if (iie.getBase() == value) {
                                replace = true;
                            }
                        }
                    }

                    if (replace) {
                        unitToReplaceByException.add(u);
                    }
                }
            }
        }

        for (Unit u : unitToReplaceByException) {
            addExceptionAfterUnit(b, "java.lang.NullPointerException", u,
                    "This statement would have triggered an Exception: " + u);
            b.getUnits().remove(u);
        }

        // should be done on a separate phase
        DeadAssignmentEliminator.v().transform(b);
        UnusedLocalEliminator.v().transform(b);

    }

    private void compareTypeAssigners(JimpleBody jb, boolean useOlderTypeAssigner) {
        int size = jb.getUnits().size();
        JimpleBody oldJb, newJb;
        long oldTime, newTime;
        if (useOlderTypeAssigner) {
            // Use old type assigner last
            newJb = (JimpleBody) jb.clone();
            newTime = System.currentTimeMillis();
            (new soot.jimple.toolkits.typing.fast.TypeResolver(newJb)).inferTypes();
            newTime = System.currentTimeMillis() - newTime;
            oldTime = System.currentTimeMillis();
            soot.jimple.toolkits.typing.TypeResolver.resolve(jb, Scene.v());
            oldTime = System.currentTimeMillis() - oldTime;
            oldJb = jb;
        } else {
            // Use new type assigner last
            oldJb = (JimpleBody) jb.clone();
            oldTime = System.currentTimeMillis();
            soot.jimple.toolkits.typing.TypeResolver.resolve(oldJb, Scene.v());
            oldTime = System.currentTimeMillis() - oldTime;
            newTime = System.currentTimeMillis();
            (new soot.jimple.toolkits.typing.fast.TypeResolver(jb)).inferTypes();
            newTime = System.currentTimeMillis() - newTime;
            newJb = jb;
        }

        int cmp;
        if (newJb.getLocals().size() < oldJb.getLocals().size()) {
            cmp = 2;
        } else if (newJb.getLocals().size() > oldJb.getLocals().size()) {
            cmp = -2;
        } else {
            cmp = compareTypings(oldJb, newJb);
        }

        logger.debug("cmp;" + jb.getMethod() + ";" + size + ";" + oldTime + ";" + newTime + ";" + cmp);
    }

    private boolean typingFailed(JimpleBody b) {
        // Check to see if any locals are untyped
        final UnknownType unknownType = UnknownType.v();
        final ErroneousType erroneousType = ErroneousType.v();
        for (Local l : b.getLocals()) {
            Type t = l.getType();
            if (unknownType.equals(t) || erroneousType.equals(t)) {
                return true;
            }
        }

        return false;
    }

    /* Returns -1 if a < b, +1 if b < a, 0 if a = b and 3 otherwise. */
    private static int compareTypings(JimpleBody a, JimpleBody b) {
        int r = 0;

        Iterator<Local> ib = b.getLocals().iterator();
        for (Local v : a.getLocals()) {
            Type ta = v.getType(), tb = ib.next().getType();

            if (soot.jimple.toolkits.typing.fast.TypeResolver.typesEqual(ta, tb)) {
                continue;
            } else if ((ta instanceof CharType && (tb instanceof ByteType || tb instanceof ShortType))
                    || (tb instanceof CharType && (ta instanceof ByteType || ta instanceof ShortType))) {
                continue;
            } else if (soot.jimple.toolkits.typing.fast.AugHierarchy.ancestor_(ta, tb)) {
                if (r == -1) {
                    return 3;
                } else {
                    r = 1;
                }
            } else if (soot.jimple.toolkits.typing.fast.AugHierarchy.ancestor_(tb, ta)) {
                if (r == 1) {
                    return 3;
                } else {
                    r = -1;
                }
            } else {
                return 3;
            }
        }

        return r;
    }
}
