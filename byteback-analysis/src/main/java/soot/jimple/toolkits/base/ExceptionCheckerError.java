package soot.jimple.toolkits.base;

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

import byteback.analysis.model.ClassModel;
import byteback.analysis.model.MethodModel;
import soot.jimple.Stmt;
import soot.tag.SourceLnPosTag;

public class ExceptionCheckerError extends Exception {

    private MethodModel method;
    private ClassModel excType;
    private Stmt throwing;
    private SourceLnPosTag position;

    public ExceptionCheckerError(MethodModel m, ClassModel sc, Stmt s, SourceLnPosTag pos) {
        method(m);
        excType(sc);
        throwing(s);
        position(pos);
    }

    public MethodModel method() {
        return method;
    }

    public void method(MethodModel sm) {
        method = sm;
    }

    public ClassModel excType() {
        return excType;
    }

    public void excType(ClassModel sc) {
        excType = sc;
    }

    public Stmt throwing() {
        return throwing;
    }

    public void throwing(Stmt s) {
        throwing = s;
    }

    public SourceLnPosTag position() {
        return position;
    }

    public void position(SourceLnPosTag pos) {
        position = pos;
    }
}
