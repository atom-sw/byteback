package byteback.analysis.body.jimple.syntax;

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

import byteback.analysis.model.syntax.type.ArrayType;
import soot.ArrayType;
import byteback.analysis.model.syntax.type.Type;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import soot.util.Switch;

import java.util.List;

public interface NewMultiArrayExpr extends Expr, AnyNewExpr {
    ArrayType getBaseType();

    void setBaseType(ArrayType baseType);

    ValueBox getSizeBox(int index);

    int getSizeCount();

    Value getSize(int index);

    List<Value> getSizes();

    void setSize(int index, Value size);

    Type getType();

    void apply(Switch sw);
}
