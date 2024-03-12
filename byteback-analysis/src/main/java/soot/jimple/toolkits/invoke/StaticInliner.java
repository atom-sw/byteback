package soot.jimple.toolkits.invoke;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.*;
import soot.options.Options;
import soot.tag.Host;

import java.util.*;

/**
 * Uses the Scene's currently-active InvokeGraph to inline monomorphic call sites.
 */
public class StaticInliner extends SceneTransformer {
    private static final Logger logger = LoggerFactory.getLogger(StaticInliner.class);

    private final HashMap<MethodModel, Integer> methodToOriginalSize = new HashMap<MethodModel, Integer>();

    public StaticInliner(Singletons.Global g) {
    }

    public static StaticInliner v() {
        return G.v().soot_jimple_toolkits_invoke_StaticInliner();
    }

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        final Filter explicitInvokesFilter = new Filter(new ExplicitEdgesPred());
        if (Options.v().verbose()) {
            logger.debug("[" + phaseName + "] Inlining methods...");
        }

        computeAverageMethodSizeAndSaveOriginalSizes();

        final String modifierOptions = PhaseOptions.getString(options, "allowed-modifier-changes");
        final ArrayList<Host[]> sitesToInline = new ArrayList<Host[]>();

        // Visit each potential site in reverse pseudo topological order.
        {
            final CallGraph cg = Scene.v().getCallGraph();
            final TopologicalOrderer orderer = new TopologicalOrderer(cg);
            orderer.go();
            List<MethodModel> order = orderer.order();
            for (ListIterator<MethodModel> it = order.listIterator(order.size()); it.hasPrevious(); ) {
                MethodModel container = it.previous();
                if (!container.isConcrete() || !methodToOriginalSize.containsKey(container)
                        || !explicitInvokesFilter.wrap(cg.edgesOutOf(container)).hasNext()) {
                    continue;
                }

                for (Unit u : new ArrayList<Unit>(container.retrieveActiveBody().getUnits())) {
                    final Stmt s = (Stmt) u;
                    if (!s.containsInvokeExpr()) {
                        continue;
                    }

                    final Targets targets = new Targets(explicitInvokesFilter.wrap(cg.edgesOutOf(s)));
                    if (!targets.hasNext()) {
                        continue;
                    }
                    final MethodModel target = (MethodModel) targets.next();
                    if (targets.hasNext() || !target.isConcrete() || !target.getDeclaringClass().isApplicationClass()
                            || !InlinerSafetyManager.ensureInlinability(target, s, container, modifierOptions)) {
                        continue;
                    }

                    sitesToInline.add(new Host[]{target, s, container});
                }
            }
        }

        // Proceed to inline the sites, one at a time, keeping track of expansion rates.
        {
            final float expansionFactor = PhaseOptions.getFloat(options, "expansion-factor");
            final int maxContainerSize = PhaseOptions.getInt(options, "max-container-size");
            final int maxInlineeSize = PhaseOptions.getInt(options, "max-inlinee-size");
            final Pack jbPack = PhaseOptions.getBoolean(options, "rerun-jb") ? PackManager.v().getPack("jb") : null;
            for (Host[] site : sitesToInline) {
                MethodModel inlinee = (MethodModel) site[0];
                int inlineeSize = inlinee.retrieveActiveBody().getUnits().size();

                MethodModel container = (MethodModel) site[2];
                int containerSize = container.retrieveActiveBody().getUnits().size();

                if (inlineeSize > maxInlineeSize) {
                    continue;
                }

                int inlinedSize = inlineeSize + containerSize;
                if (inlinedSize > maxContainerSize || inlinedSize > expansionFactor * methodToOriginalSize.get(container)) {
                    continue;
                }

                Stmt invokeStmt = (Stmt) site[1];
                // Not that it is important to check right before inlining if the site is still valid.
                if (InlinerSafetyManager.ensureInlinability(inlinee, invokeStmt, container, modifierOptions)) {
                    SiteInliner.inlineSite(inlinee, invokeStmt, container, options);
                    if (jbPack != null) {
                        jbPack.apply(container.getActiveBody());
                    }
                }
            }
        }
    }

    private void computeAverageMethodSizeAndSaveOriginalSizes() {
        // long sum = 0, count = 0;
        for (ClassModel c : Scene.v().getApplicationClasses()) {
            for (Iterator<MethodModel> methodsIt = c.methodIterator(); methodsIt.hasNext(); ) {
                MethodModel m = methodsIt.next();
                if (m.isConcrete()) {
                    int size = m.retrieveActiveBody().getUnits().size();
                    // sum += size;
                    methodToOriginalSize.put(m, size);
                    // count++;
                }
            }
        }
        // if (count == 0) {
        // return;
        // }
    }
}
