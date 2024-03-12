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

import byteback.analysis.model.ClassModel;
import org.objectweb.asm.ClassReader;
import soot.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * ASM class source implementation.
 *
 * @author Aaloan Miftah
 */
public class AsmClassSource extends ClassSource {

    protected InputSource inputSource;

    /**
     * Constructs a new ASM class source.
     *
     * @param className   fully qualified name of the class.
     * @param inputSource input source pointing to the data for class.
     */
    public AsmClassSource(final String className, final InputSource inputSource) {
        super(className);

        if (inputSource == null) {
            throw new IllegalStateException("Error: The FoundFile must not be null.");
        }

        this.inputSource = inputSource;
    }

    @Override
    public Dependencies resolve(final ClassModel classModel) {
        try (final InputStream inputStream = inputSource.inputStream()) {
            final ClassReader classReader = new ClassReader(inputStream);
            final ClassModelBuilder classModelBuilder = new ClassModelBuilder(classModel);
            classReader.accept(classModelBuilder, ClassReader.SKIP_FRAMES);
            final Dependencies dependencies = new Dependencies();
            dependencies.typesToSignature.addAll(classModelBuilder.typeDependencies);

            return dependencies;
        } catch (IOException e) {
            throw new RuntimeException("Error: Failed to create class reader from class source.", e);
        }
    }

    @Override
    public void close() {
        if (inputSource != null) {
            inputSource.close();
        }
    }
}