package soot.dexpler.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
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

import java.util.HashSet;
import java.util.Set;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;

import soot.Type;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.ClassConstant;
import soot.jimple.Constant;
import soot.jimple.Jimple;

public class ConstClassInstruction extends DexlibAbstractInstruction {

  public ConstClassInstruction(Instruction instruction, int codeAdress) {
    super(instruction, codeAdress);
  }

  @Override
  public void jimplify(DexBody body) {
    if (!(instruction instanceof Instruction21c)) {
      throw new IllegalArgumentException("Expected Instruction21c but got: " + instruction.getClass());
    }

    ReferenceInstruction constClass = (ReferenceInstruction) this.instruction;

    TypeReference tidi = (TypeReference) (constClass.getReference());
    Constant cst = ClassConstant.v(tidi.getType());

    int dest = ((OneRegisterInstruction) instruction).getRegisterA();
    AssignStmt assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), cst);
    setUnit(assign);
    addTags(assign);
    body.add(assign);

    if (IDalvikTyper.ENABLE_DVKTYPER) {
      // DalvikTyper.v().captureAssign((JAssignStmt)assign, op); //TODO:
      // classtype could be null!
      DalvikTyper.v().setType(assign.getLeftOpBox(), cst.getType(), false);
    }
  }

  @Override
  boolean overridesRegister(int register) {
    OneRegisterInstruction i = (OneRegisterInstruction) instruction;
    int dest = i.getRegisterA();
    return register == dest;
  }

  @Override
  public Set<Type> introducedTypes() {
    ReferenceInstruction i = (ReferenceInstruction) instruction;

    Set<Type> types = new HashSet<Type>();
    types.add(DexType.toSoot((TypeReference) i.getReference()));
    return types;
  }
}
