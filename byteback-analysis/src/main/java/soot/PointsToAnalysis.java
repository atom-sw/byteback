package soot;

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

import byteback.analysis.model.FieldModel;

/**
 * A generic interface to any type of pointer analysis.
 *
 * @author Ondrej Lhotak
 */

public interface PointsToAnalysis {
    /**
     * Returns the set of objects pointed to by variable l.
     */
    PointsToSet reachingObjects(Local l);

    /**
     * Returns the set of objects pointed to by variable l in context c.
     */
    PointsToSet reachingObjects(Context c, Local l);

    /**
     * Returns the set of objects pointed to by static field f.
     */
    PointsToSet reachingObjects(FieldModel f);

    /**
     * Returns the set of objects pointed to by instance field f of the objects in the PointsToSet s.
     */
    PointsToSet reachingObjects(PointsToSet s, FieldModel f);

    /**
     * Returns the set of objects pointed to by instance field f of the objects pointed to by l.
     */
    PointsToSet reachingObjects(Local l, FieldModel f);

    /**
     * Returns the set of objects pointed to by instance field f of the objects pointed to by l in context c.
     */
    PointsToSet reachingObjects(Context c, Local l, FieldModel f);

    /**
     * Returns the set of objects pointed to by elements of the arrays in the PointsToSet s.
     */
    PointsToSet reachingObjectsOfArrayElement(PointsToSet s);

    String THIS_NODE = "THIS_NODE";
    int RETURN_NODE = -2;
    String THROW_NODE = "THROW_NODE";
    String ARRAY_ELEMENTS_NODE = "ARRAY_ELEMENTS_NODE";
    String CAST_NODE = "CAST_NODE";
    String STRING_ARRAY_NODE = "STRING_ARRAY_NODE";
    String STRING_NODE = "STRING_NODE";
    String STRING_NODE_LOCAL = "STRING_NODE_LOCAL";
    String EXCEPTION_NODE = "EXCEPTION_NODE";
    String RETURN_STRING_CONSTANT_NODE = "RETURN_STRING_CONSTANT_NODE";
    String STRING_ARRAY_NODE_LOCAL = "STRING_ARRAY_NODE_LOCAL";
    String MAIN_THREAD_NODE = "MAIN_THREAD_NODE";
    String MAIN_THREAD_NODE_LOCAL = "MAIN_THREAD_NODE_LOCAL";
    String MAIN_THREAD_GROUP_NODE = "MAIN_THREAD_GROUP_NODE";
    String MAIN_THREAD_GROUP_NODE_LOCAL = "MAIN_THREAD_GROUP_NODE_LOCAL";
    String MAIN_CLASS_NAME_STRING = "MAIN_CLASS_NAME_STRING";
    String MAIN_CLASS_NAME_STRING_LOCAL = "MAIN_CLASS_NAME_STRING_LOCAL";
    String DEFAULT_CLASS_LOADER = "DEFAULT_CLASS_LOADER";
    String DEFAULT_CLASS_LOADER_LOCAL = "DEFAULT_CLASS_LOADER_LOCAL";
    String FINALIZE_QUEUE = "FINALIZE_QUEUE";
    String CANONICAL_PATH = "CANONICAL_PATH";
    String CANONICAL_PATH_LOCAL = "CANONICAL_PATH_LOCAL";
    String PRIVILEGED_ACTION_EXCEPTION = "PRIVILEGED_ACTION_EXCEPTION";
    String PRIVILEGED_ACTION_EXCEPTION_LOCAL = "PRIVILEGED_ACTION_EXCEPTION_LOCAL";
    String PHI_NODE = "PHI_NODE";
}
