package soot.jimple.toolkits.annotation.profiling;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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
import byteback.analysis.model.MethodModel;
import soot.*;
import soot.jimple.*;
import soot.options.ProfilingOptions;
import soot.util.Chain;

import java.util.Iterator;
import java.util.Map;

public class ProfilingGenerator extends BodyTransformer {
    public ProfilingGenerator(Singletons.Global g) {
    }

    public static ProfilingGenerator v() {
        return G.v().soot_jimple_toolkits_annotation_profiling_ProfilingGenerator();
    }

    public String mainSignature = "void main(java.lang.String[])";

    // private String mainSignature = "long runBenchmark(java.lang.String[])";

    protected void internalTransform(Body body, String phaseName, Map opts) {
        ProfilingOptions options = new ProfilingOptions(opts);
        if (options.notmainentry()) {
            mainSignature = "long runBenchmark(java.lang.String[])";
        }

        {
            MethodModel m = body.getMethod();

            ClassModel counterClass = Scene.v().loadClassAndSupport("MultiCounter");
            MethodModel reset = counterClass.getMethodModel("void reset()");
            MethodModel report = counterClass.getMethodModel("void report()");

            boolean isMainMethod = m.getSubSignature().equals(mainSignature);

            Chain units = body.getUnits();

            if (isMainMethod) {
                units.addFirst(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(reset.makeRef())));
            }

            Iterator stmtIt = body.getUnits().snapshotIterator();
            while (stmtIt.hasNext()) {
                Stmt stmt = (Stmt) stmtIt.next();

                if (stmt instanceof InvokeStmt) {
                    InvokeExpr iexpr = stmt.getInvokeExpr();

                    if (iexpr instanceof StaticInvokeExpr) {
                        MethodModel tempm = iexpr.getMethod();

                        if (tempm.getSignature().equals("<java.lang.System: void exit(int)>")) {
                            units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(report.makeRef())), stmt);

                        }
                    }
                } else if (isMainMethod && (stmt instanceof ReturnStmt || stmt instanceof ReturnVoidStmt)) {
                    units.insertBefore(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(report.makeRef())), stmt);
                }
            }
        }
    }
}
