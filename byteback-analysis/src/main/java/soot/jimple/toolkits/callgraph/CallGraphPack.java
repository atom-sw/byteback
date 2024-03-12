package soot.jimple.toolkits.callgraph;

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

import byteback.analysis.model.ClassModel;
import byteback.analysis.model.MethodModel;
import soot.*;
import soot.options.CGOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * A radio pack implementation for the call graph pack that calls the intra-procedural clinit eliminator after the call graph
 * has been built.
 */
public class CallGraphPack extends RadioScenePack {

    public CallGraphPack(String name) {
        super(name);
    }

    @Override
    protected void internalApply() {
        CGOptions options = new CGOptions(PhaseOptions.v().getPhaseOptions(this));
        if (!Scene.v().hasCustomEntryPoints()) {
            if (!options.implicit_entry()) {
                Scene.v().setEntryPoints(EntryPoints.v().application());
            }
            if (options.all_reachable()) {
                List<MethodModel> entryPoints = new ArrayList<MethodModel>();
                entryPoints.addAll(EntryPoints.v().all());
                entryPoints.addAll(EntryPoints.v().methodsOfApplicationClasses());
                Scene.v().setEntryPoints(entryPoints);
            }
        }

        super.internalApply();

        if (options.trim_clinit()) {
            ClinitElimTransformer trimmer = new ClinitElimTransformer();
            for (ClassModel cl : Scene.v().getClasses(ClassModel.BODIES)) {
                for (MethodModel m : cl.getMethodModels()) {
                    if (m.isConcrete() && m.hasActiveBody()) {
                        trimmer.transform(m.getActiveBody());
                    }
                }
            }
        }
    }
}
