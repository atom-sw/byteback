package soot.dava.internal.javaRep;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2004 Ondrej Lhotak
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

import soot.RefType;
import soot.SootMethodRef;
import soot.grimp.Grimp;
import soot.grimp.internal.GNewInvokeExpr;

public class DNewInvokeExpr extends GNewInvokeExpr {
  public DNewInvokeExpr(RefType type, SootMethodRef methodRef, java.util.List args) {
    super(type, methodRef, args);
  }

  public Object clone() {
    ArrayList clonedArgs = new ArrayList(getArgCount());

    for (int i = 0; i < getArgCount(); i++) {
      clonedArgs.add(i, Grimp.cloneIfNecessary(getArg(i)));
    }

    return new DNewInvokeExpr((RefType) getType(), methodRef, clonedArgs);
  }
}
