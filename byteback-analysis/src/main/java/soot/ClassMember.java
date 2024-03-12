package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

/**
 * Provides methods common to Soot objects belonging to classes, namely SootField and SootMethod.
 */
public interface ClassMember {
    /**
     * Returns the SootClass declaring this one.
     */
    ClassModel getDeclaringClass();

    /**
     * Returns true when some SootClass object declares this object.
     */
    boolean isDeclared();

    /**
     * Returns true when this object is from a phantom class.
     */
    boolean isPhantom();

    /**
     * Sets the phantom flag
     */
    void setPhantom(boolean value);

    /**
     * Convenience method returning true if this class member is protected.
     */
    boolean isProtected();

    /**
     * Convenience method returning true if this class member is private.
     */
    boolean isPrivate();

    /**
     * Convenience method returning true if this class member is public.
     */
    boolean isPublic();

    /**
     * Convenience method returning true if this class member is static.
     */
    boolean isStatic();

    /**
     * Sets modifiers of this class member.
     */
    void setModifiers(int modifiers);

    /**
     * Returns modifiers of this class member.
     */
    int getModifiers();

}
