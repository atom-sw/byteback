package soot.dava.internal.javaRep;

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

import java.util.ArrayList;
import java.util.List;

import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.Expr;
import soot.util.Switch;

public class DShortcutIf implements Expr {
  ValueBox testExprBox;
  ValueBox trueExprBox;
  ValueBox falseExprBox;
  Type exprType;

  public DShortcutIf(ValueBox test, ValueBox left, ValueBox right) {
    testExprBox = test;
    trueExprBox = left;
    falseExprBox = right;
  }

  public Object clone() {
    // does not work
    return this;
  }

  public List getUseBoxes() {
    List toReturn = new ArrayList();
    toReturn.addAll(testExprBox.getValue().getUseBoxes());
    toReturn.add(testExprBox);
    toReturn.addAll(trueExprBox.getValue().getUseBoxes());
    toReturn.add(trueExprBox);
    toReturn.addAll(falseExprBox.getValue().getUseBoxes());
    toReturn.add(falseExprBox);
    return toReturn;
  }

  public Type getType() {
    return exprType;
  }

  public String toString() {
    String toReturn = "";
    toReturn += testExprBox.getValue().toString();
    toReturn += " ? ";
    toReturn += trueExprBox.getValue().toString();
    toReturn += " : ";
    toReturn += falseExprBox.getValue().toString();
    return toReturn;
  }

  public void toString(UnitPrinter up) {
    testExprBox.getValue().toString(up);
    up.literal(" ? ");
    trueExprBox.getValue().toString(up);
    up.literal(" : ");
    falseExprBox.getValue().toString(up);
  }

  public void apply(Switch sw) {
    // TODO Auto-generated method stub

  }

  public boolean equivTo(Object o) {
    // TODO Auto-generated method stub
    return false;
  }

  public int equivHashCode() {
    int toReturn = 0;
    toReturn += testExprBox.getValue().equivHashCode();
    toReturn += trueExprBox.getValue().equivHashCode();
    toReturn += falseExprBox.getValue().equivHashCode();
    return toReturn;
  }

}
