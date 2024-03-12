package soot.options;

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

/* THIS FILE IS AUTO-GENERATED FROM soot_options.xml. DO NOT MODIFY. */

import java.util.Map;

/**
 * Option parser for Lock Allocator.
 */
public class LockAllocator {

    private final Map<String, String> options;

    public LockAllocator(Map<String, String> options) {
        this.options = options;
    }

    /**
     * Enabled
     */
    public boolean enabled() {
        return soot.PhaseOptions.getBoolean(options, "enabled");
    }

    /**
     * Perform Deadlock Avoidance --
     * Perform Deadlock Avoidance.
     * <p>
     * Perform Deadlock Avoidance by enforcing a lock ordering where
     * necessary.
     */
    public boolean avoid_deadlock() {
        return soot.PhaseOptions.getBoolean(options, "avoid-deadlock");
    }

    /**
     * Use Open Nesting --
     * Use an open nesting model.
     * <p>
     * Use an open nesting model, where inner transactions are allowed
     * to commit independently of any outer transaction.
     */
    public boolean open_nesting() {
        return soot.PhaseOptions.getBoolean(options, "open-nesting");
    }

    /**
     * Perform May-Happen-in-Parallel Analysis --
     * Perform a May-Happen-in-Parallel analysis.
     * <p>
     * Perform a May-Happen-in-Parallel analysis to assist in
     * allocating locks.
     */
    public boolean do_mhp() {
        return soot.PhaseOptions.getBoolean(options, "do-mhp");
    }

    /**
     * Perform Local Objects Analysis --
     * Perform a Local-Objects analysis.
     * <p>
     * Perform a Local-Objects analysis to assist in allocating locks.
     */
    public boolean do_tlo() {
        return soot.PhaseOptions.getBoolean(options, "do-tlo");
    }

    /**
     * Print Topological Graph --
     * Print topological graph of transactions.
     * <p>
     * Print a topological graph of the program's transactions in the
     * format used by the graphviz package.
     */
    public boolean print_graph() {
        return soot.PhaseOptions.getBoolean(options, "print-graph");
    }

    /**
     * Print Table --
     * Print table of transactions.
     * <p>
     * Print a table of information about the program's transactions.
     */
    public boolean print_table() {
        return soot.PhaseOptions.getBoolean(options, "print-table");
    }

    /**
     * Print Debugging Info --
     * Print debugging info.
     * <p>
     * Print debugging info, including every statement visited.
     */
    public boolean print_debug() {
        return soot.PhaseOptions.getBoolean(options, "print-debug");
    }

    public static final int locking_scheme_medium_grained = 1;
    public static final int locking_scheme_coarse_grained = 2;
    public static final int locking_scheme_single_static = 3;
    public static final int locking_scheme_leave_original = 4;

    /**
     * Locking Scheme --
     * Selects the granularity of the generated lock allocation.
     * <p>
     * Selects the granularity of the generated lock allocation
     */
    public int locking_scheme() {
        String s = soot.PhaseOptions.getString(options, "locking-scheme");
        if (s == null || s.isEmpty())
            return locking_scheme_medium_grained;

        if (s.equalsIgnoreCase("medium-grained"))
            return locking_scheme_medium_grained;
        if (s.equalsIgnoreCase("coarse-grained"))
            return locking_scheme_coarse_grained;
        if (s.equalsIgnoreCase("single-static"))
            return locking_scheme_single_static;
        if (s.equalsIgnoreCase("leave-original"))
            return locking_scheme_leave_original;

        throw new RuntimeException(String.format("Invalid value %s of phase option locking-scheme", s));
    }

}
