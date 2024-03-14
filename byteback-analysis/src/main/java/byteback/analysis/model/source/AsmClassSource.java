package byteback.analysis.model.source;

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

import byteback.analysis.body.common.source.ClassSource;
import byteback.analysis.model.syntax.ClassModel;
import byteback.analysis.model.syntax.type.ClassType;
import byteback.analysis.model.syntax.type.Type;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * ASM class source implementation.
 *
 * @author Aaloan Miftah
 */
public class AsmClassSource extends ClassSource {

    private final InputStream inputStream;

    public AsmClassSource(final String className, final InputStream inputStream) {
        super(className);
        this.inputStream = inputStream;
    }

    @Override
    public Set<ClassType> resolve(final ClassModel classModel) {
        try (inputStream) {
            final ClassReader classReader = new ClassReader(inputStream);
            final ClassModelBuilder classModelBuilder = new ClassModelBuilder(classModel);
            classReader.accept(classModelBuilder, ClassReader.SKIP_FRAMES);
            final var dependencies = new HashSet<Type>();
            dependencies.addAll(classModelBuilder.typeDependencies);

            return dependencies;
        } catch (IOException e) {
            throw new RuntimeException("Error: Failed to create class reader from class source.", e);
        }
    }
}