package soot.jimple.toolkits.typing.integer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2000 Etienne Gagnon.  All rights reserved.
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

import soot.Type;

class InternalTypingException extends RuntimeException {
  /**
   * 
   */
  private static final long serialVersionUID = 1874994601632508834L;
  private final Type unexpectedType;

  public InternalTypingException() {
    this.unexpectedType = null;
  }

  public InternalTypingException(Type unexpectedType) {
    this.unexpectedType = unexpectedType;
  }

  public Type getUnexpectedType() {
    return this.unexpectedType;
  }

  @Override
  public String getMessage() {
    return String.format("Unexpected type %s (%s)", unexpectedType,
        unexpectedType == null ? "-" : unexpectedType.getClass().getSimpleName());
  }

}
