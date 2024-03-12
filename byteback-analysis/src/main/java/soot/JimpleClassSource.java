package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import soot.jimple.parser.lexer.LexerException;
//import soot.jimple.parser.parser.ParserException;


/**
 * A class source for resolving from .jimple files using the Jimple parser.
 */
public class JimpleClassSource extends ClassSource {
    private static final Logger logger = LoggerFactory.getLogger(JimpleClassSource.class);

    private InputSource inputSource;

    public JimpleClassSource(String className, InputSource inputSource) {
        super(className);
        if (inputSource == null) {
            throw new IllegalStateException("Error: The FoundFile must not be null.");
        }
        this.inputSource = inputSource;
    }

    @Override
    public Dependencies resolve(ClassModel sc) {
        return null;
    }

    @Override
    public void close() {
        if (inputSource != null) {
            inputSource.close();
            inputSource = null;
        }
    }
}
