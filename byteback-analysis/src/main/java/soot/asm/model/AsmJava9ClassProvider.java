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
package soot.asm.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.ClassProvider;
import soot.ClassSource;
import soot.InputSource;
import soot.ModulePathSourceLocator;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Objectweb ASM class provider.
 *
 * @author Andreas Dann
 */
public class AsmJava9ClassProvider implements ClassProvider {
    private static final Logger logger = LoggerFactory.getLogger(AsmJava9ClassProvider.class);

    @Override
    public Optional<ClassSource> find(final String className) {
        final String classPath = className.replace('.', '/') + ".class";

        // here we go through all modules, since we are in classpath mode
        final Path path = ModulePathSourceLocator.getRootModulesPathOfJDK();

        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (final Path entry : stream) {
                final Optional<InputSource> fileOption = ModulePathSourceLocator.v()
                        .lookUpInVirtualFileSystem(entry.toUri().toString(), classPath);

                if (fileOption.isPresent()) {
                    return fileOption.map((file) -> new AsmClassSource(classPath, file));
                }
            }
        } catch (FileSystemNotFoundException exception) {
            logger.debug("Could not read my modules (perhaps not Java 9?).");
        } catch (IOException e) {
            logger.debug(e.getMessage(), e);
        }

        return Optional.empty();
    }
}
