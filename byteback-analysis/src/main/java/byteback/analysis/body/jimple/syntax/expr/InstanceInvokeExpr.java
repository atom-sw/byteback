package byteback.analysis.body.jimple.syntax.expr;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
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

import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class InstanceInvokeExpr extends InvokeExpr {

    protected final ValueBox baseBox;

    protected InstanceInvokeExpr(SootMethodRef methodRef, ValueBox baseBox, ValueBox[] argBoxes) {
        super(methodRef, argBoxes);
        this.baseBox = baseBox;
    }

    public Value getBase() {
        return baseBox.getValue();
    }

    public ValueBox getBaseBox() {
        return baseBox;
    }

    public void setBase(Value base) {
        baseBox.setValue(base);
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        final var list = new ArrayList<ValueBox>(baseBox.getValue().getUseBoxes());
        list.add(baseBox);

        if (argBoxes != null) {
            Collections.addAll(list, argBoxes);

            for (ValueBox element : argBoxes) {
                list.addAll(element.getValue().getUseBoxes());
            }
        }

        return list;
    }
}
