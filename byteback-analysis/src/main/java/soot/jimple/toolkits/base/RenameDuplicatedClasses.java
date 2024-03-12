package soot.jimple.toolkits.base;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.options.Options;

import java.io.File;
import java.util.*;

/**
 * A scene transformer that renames the duplicated class names.
 * <p>
 * The definition of duplicated class names. if (className1.equalsIgnoreCase(className2) { //className1 and className2 are
 * duplicated class names. }
 * <p>
 * Because some file systems are case-insensitive (e.g., Mac OS). When file a.b.c.class exists, a.b.C.class will over-write
 * the content of a.b.c.class and case inconsistent that a.b.c.class file contains the content of a.b.C.class.
 * <p>
 * However, in some case, at lest in Android applications, the duplicated class names exist. For example, an app (Sha256:
 * 0015AE7C27688D45F79170DCEA16131CE557912A1A0C5F3B6B0465EE0774A452) in the Genome project contains duplicated class names.
 * When transforming the app to classes, some classes are missing and consequently case problems for other analysis tools
 * that relay on Soot (e.g., Error: class com.adwo.adsdk.s read in from a classfile in which com.adwo.adsdk.S was expected).
 */
public class RenameDuplicatedClasses extends SceneTransformer {
    private static final Logger logger = LoggerFactory.getLogger(RenameDuplicatedClasses.class);

    private static final String FIXED_CLASS_NAME_SPERATOR = "-";

    public RenameDuplicatedClasses(Singletons.Global g) {
    }

    public static RenameDuplicatedClasses v() {
        return G.v().soot_jimple_toolkits_base_RenameDuplicatedClasses();
    }

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        // If the file system is case sensitive, no need to rename the classes
        if (isFileSystemCaseSensitive()) {
            return;
        }

        final Set<String> fixedClassNames
                = new HashSet<>(Arrays.asList(PhaseOptions.getString(options, "fixedClassNames").split(FIXED_CLASS_NAME_SPERATOR)));
        duplicatedCheck(fixedClassNames);

        if (Options.v().verbose()) {
            logger.debug("The fixed class names are: " + fixedClassNames);
        }

        int count = 0;
        Map<String, String> lowerCaseClassNameToReal = new HashMap<String, String>();
        for (Iterator<ClassModel> iter = Scene.v().getClasses().snapshotIterator(); iter.hasNext(); ) {
            ClassModel classModel = iter.next();
            String className = classModel.getName();

            if (lowerCaseClassNameToReal.containsKey(className.toLowerCase())) {
                if (fixedClassNames.contains(className)) {
                    classModel = Scene.v().getSootClass(lowerCaseClassNameToReal.get(className.toLowerCase()));
                    className = lowerCaseClassNameToReal.get(className.toLowerCase());
                }

                String newClassName = className + (count++);
                classModel.rename(newClassName);

                // if(Options.v().verbose())
                // {
                logger.debug("Rename duplicated class " + className + " to class " + newClassName);
                // }
            } else {
                lowerCaseClassNameToReal.put(className.toLowerCase(), className);
            }
        }
    }

    public void duplicatedCheck(Iterable<String> classNames) {
        Set<String> classNameSet = new HashSet<String>();
        for (String className : classNames) {
            if (classNameSet.contains(className.toLowerCase())) {
                throw new RuntimeException("The fixed class names cannot contain duplicated class names.");
            } else {
                classNameSet.add(className.toLowerCase());
            }
        }
    }

    /**
     * An naive approach to check whether the file system is case sensitive or not
     *
     * @return
     */
    public boolean isFileSystemCaseSensitive() {
        File[] allFiles = (new File(".")).listFiles();
        if (allFiles != null) {
            for (File f : allFiles) {
                if (f.isFile()) {
                    if (!(new File(f.getAbsolutePath().toLowerCase())).exists()
                            || !(new File(f.getAbsolutePath().toUpperCase())).exists()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
