package soot.tag;

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

import soot.util.Switch;

public interface IAnnotationElemTypeSwitch extends Switch {
    void caseAnnotationAnnotationElem(AnnotationSubElement v);

    void caseAnnotationArrayElem(AnnotationArrayElement v);

    void caseAnnotationBooleanElem(AnnotationBooleanElement v);

    void caseAnnotationClassElem(AnnotationClassElement v);

    void caseAnnotationDoubleElem(AnnotationDoubleElement v);

    void caseAnnotationEnumElem(AnnotationEnumElement v);

    void caseAnnotationFloatElem(AnnotationFloatElement v);

    void caseAnnotationIntElem(AnnotationIntElement v);

    void caseAnnotationLongElem(AnnotationLongElement v);

    void caseAnnotationStringElem(AnnotationStringElement v);

    void defaultCase(Object object);

}
