package soot.asm.model;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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

import org.objectweb.asm.ModuleVisitor;
import org.objectweb.asm.Opcodes;
import soot.ClassModel;
import soot.ModuleInfoModel;
import soot.RefType;
import soot.SootModuleResolver;

import java.util.Optional;

/**
 * Builds Soot's representation for a module-info class.
 *
 * @author Andreas Dann
 */
public class SootModuleInfoBuilder extends ModuleVisitor {

    private final ClassModelBuilder scb;
    private final ModuleInfoModel klass;
    private final String name;

    public SootModuleInfoBuilder(String name, ModuleInfoModel klass, ClassModelBuilder scb) {
        super(Opcodes.ASM8);
        this.klass = klass;
        this.name = name;
        this.scb = scb;
    }

    @Override
    public void visitRequire(String module, int access, String version) {
        ClassModel moduleInfo = SootModuleResolver.v().makeClassRef(ModuleInfoModel.MODULE_INFO, Optional.of(module));
        klass.getRequiredModules().put((ModuleInfoModel) moduleInfo, access);
        scb.addDep(RefType.v(moduleInfo));
    }

}
