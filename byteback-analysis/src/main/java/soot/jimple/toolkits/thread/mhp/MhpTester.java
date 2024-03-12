package soot.jimple.toolkits.thread.mhp;

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

import byteback.analysis.model.MethodModel;
import soot.Unit;
import soot.jimple.toolkits.thread.AbstractRuntimeThread;

import java.util.List;

/**
 * MhpTester written by Richard L. Halpert 2007-03-15 An interface for any object that can provide May-Happen-in-Parallel
 * info and a list of the program's threads (List of AbstractRuntimeThreads)
 */

public interface MhpTester {
    boolean mayHappenInParallel(MethodModel m1, MethodModel m2); // method level MHP

    boolean mayHappenInParallel(MethodModel m1, Unit u1, MethodModel m2, Unit u2); // stmt level MHP

    void printMhpSummary();

    List<AbstractRuntimeThread> getThreads();
}
