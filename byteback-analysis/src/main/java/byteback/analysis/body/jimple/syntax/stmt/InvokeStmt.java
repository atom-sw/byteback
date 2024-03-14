package byteback.analysis.body.jimple.syntax.stmt;

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

import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.stmt.Stmt;
import byteback.analysis.body.jimple.syntax.expr.InvokeExpr;
import byteback.analysis.body.jimple.syntax.expr.InvokeExprBox;

import java.util.ArrayList;
import java.util.List;

public class InvokeStmt extends Stmt {

    protected final ValueBox invokeExprBox;

    public InvokeStmt(final Value c) {
        this(new InvokeExprBox(c));
    }

    protected InvokeStmt(ValueBox invokeExprBox) {
        this.invokeExprBox = invokeExprBox;
    }

    @Override
    public String toString() {
        return invokeExprBox.getValue().toString();
    }

    public InvokeExpr getInvokeExpr() {
        return (InvokeExpr) invokeExprBox.getValue();
    }

    public void setInvokeExpr(Value invokeExpr) {
        invokeExprBox.setValue(invokeExpr);
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        List<ValueBox> list = new ArrayList<ValueBox>(invokeExprBox.getValue().getUseBoxes());
        list.add(invokeExprBox);
        return list;
    }

    @Override
    public boolean fallsThrough() {
        return true;
    }

    @Override
    public boolean branches() {
        return false;
    }
}
