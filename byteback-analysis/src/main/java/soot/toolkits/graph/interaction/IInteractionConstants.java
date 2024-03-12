package soot.toolkits.graph.interaction;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

public interface IInteractionConstants {

    int NEW_ANALYSIS = 0;
    int WANT_ANALYSIS = 1;
    int NEW_CFG = 2;
    int CONTINUE = 3;
    int NEW_BEFORE_ANALYSIS_INFO = 4;
    int NEW_AFTER_ANALYSIS_INFO = 5;
    int DONE = 6;
    int FORWARDS = 7;
    int BACKWARDS = 8;
    int CLEARTO = 9;
    int REPLACE = 10;
    int NEW_BEFORE_ANALYSIS_INFO_AUTO = 11;
    int NEW_AFTER_ANALYSIS_INFO_AUTO = 12;
    int STOP_AT_NODE = 13;

    int CALL_GRAPH_START = 50;
    int CALL_GRAPH_NEXT_METHOD = 51;
    int CALL_GRAPH_PART = 52;
    int CALL_GRAPH_DONE = 53;
}
