package soot;

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

import byteback.analysis.model.ClassModel;
import byteback.analysis.model.MethodModel;

import java.util.List;

/**
 * The common interface of {@link MethodModel} (resolved method) and {@link SootMethodRef} (unresolved method). Therefore it
 * allows to access the properties independently whether the method is a resolved one or not.
 */
public interface SootMethodInterface {

    /**
     * @return The class which declares the current {@link MethodModel}/{@link SootMethodRef}
     */
    ClassModel getDeclaringClass();

    /**
     * @return Name of the method
     */
    String getName();

    List<Type> getParameterTypes();

    Type getParameterType(int i);

    Type getReturnType();

    boolean isStatic();

    /**
     * @return The Soot signature of this method. Used to refer to methods unambiguously.
     */
    String getSignature();

}
