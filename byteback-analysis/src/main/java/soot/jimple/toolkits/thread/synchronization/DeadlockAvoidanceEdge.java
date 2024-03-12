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

import soot.ClassModel;

public class DeadlockAvoidanceEdge extends NewStaticLock {
    public DeadlockAvoidanceEdge(ClassModel sc) {
        super(sc);
    }

    /**
     * Clones the object.
     */
    public Object clone() {
        return new DeadlockAvoidanceEdge(sc);
    }

    public boolean equals(Object c) {
        if (c instanceof DeadlockAvoidanceEdge) {
            return ((DeadlockAvoidanceEdge) c).idnum == idnum;
        }
        return false;
    }

    public String toString() {
        return "dae<" + sc.toString() + ">";
    }
}
