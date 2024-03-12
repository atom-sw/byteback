package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrice Pominville
 * Copyright (C) 2004 Ondrej Lhotak, Ganesh Sittampalam
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
import byteback.analysis.model.FieldModel;
import byteback.analysis.model.MethodModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.options.Options;
import soot.util.ConcurrentHashMultiMap;
import soot.util.MultiMap;

import java.io.Serial;
import java.util.*;

/**
 * Loads symbols for SootClasses from either class files or jimple files.
 */
public class SootResolver {
    private static final Logger logger = LoggerFactory.getLogger(SootResolver.class);

    /**
     * Maps each resolved class to a list of all references in it.
     */
    protected MultiMap<ClassModel, Type> classToTypesSignature = new ConcurrentHashMultiMap<>();

    /**
     * Maps each resolved class to a list of all references in it.
     */
    protected MultiMap<ClassModel, Type> classToTypesHierarchy = new ConcurrentHashMultiMap<>();

    /**
     * SootClasses waiting to be resolved.
     */
    @SuppressWarnings("unchecked")
    private final Deque<ClassModel>[] nextClasses = new Deque[4];

    public SootResolver(Singletons.Global g) {
        nextClasses[ClassModel.HIERARCHY] = new ArrayDeque<>();
        nextClasses[ClassModel.SIGNATURES] = new ArrayDeque<>();
        nextClasses[ClassModel.BODIES] = new ArrayDeque<>();
    }

    public static SootResolver v() {
        G g = G.v();
        return g.soot_SootResolver();
    }

    /**
     * Returns true if we are resolving all class refs recursively.
     */
    protected boolean resolveEverything() {
        final Options opts = Options.v();
        if (opts.on_the_fly()) {
            return false;
        } else {
            return (opts.whole_program() || opts.whole_shimple() || opts.full_resolver());
        }
    }

    /**
     * Returns a (possibly not yet resolved) SootClass to be used in references to a class. If/when the class is resolved, it
     * will be resolved into this SootClass.
     */
    public ClassModel makeClassRef(String className) {
        if (className.isEmpty()) {
            throw new RuntimeException("Classname must not be empty!");
        }
        final Scene scene = Scene.v();
        if (scene.containsClass(className)) {
            return scene.getSootClass(className);
        } else {
            ClassModel newClass = new ClassModel(className);
            newClass.setResolvingLevel(ClassModel.DANGLING);
            scene.addClass(newClass);
            return newClass;
        }
    }

    /**
     * Resolves the given class. Depending on the resolver settings, may decide to resolve other classes as well. If the class
     * has already been resolved, just returns the class that was already resolved.
     */
    public ClassModel resolveClass(String className, int desiredLevel) {
        ClassModel resolvedClass = null;
        try {
            resolvedClass = makeClassRef(className);
            addToResolveWorklist(resolvedClass, desiredLevel);
            processResolveWorklist();
            return resolvedClass;
        } catch (SootClassNotFoundException e) {
            // remove unresolved class and rethrow
            if (resolvedClass != null) {
                assert (resolvedClass.getResolvingLevel() == ClassModel.DANGLING);
                Scene.v().removeClass(resolvedClass);
            }
            throw e;
        }
    }

    /**
     * Resolve all classes on toResolveWorklist.
     */
    protected void processResolveWorklist() {
        final Scene scene = Scene.v();
        final boolean resolveEverything = resolveEverything();
        final boolean no_bodies_for_excluded = Options.v().no_bodies_for_excluded();
        for (int i = ClassModel.BODIES; i >= ClassModel.HIERARCHY; i--) {
            Deque<ClassModel> currWorklist = nextClasses[i];
            while (!currWorklist.isEmpty()) {
                ClassModel sc = currWorklist.pop();
                if (resolveEverything) {
                    // Whole program mode
                    boolean onlySignatures
                            = sc.isPhantom() || (no_bodies_for_excluded && scene.isExcluded(sc) && !scene.isBasicClass(sc.getName()));
                    if (onlySignatures) {
                        bringToSignatures(sc);
                        sc.setPhantomClass();
                        for (MethodModel m : sc.getMethodModels()) {
                            m.setPhantom(true);
                        }
                        for (FieldModel f : sc.getFieldModels()) {
                            f.setPhantom(true);
                        }
                    } else {
                        bringToBodies(sc);
                    }
                } else { // No transitive
                    switch (i) {
                        case ClassModel.BODIES:
                            bringToBodies(sc);
                            break;
                        case ClassModel.SIGNATURES:
                            bringToSignatures(sc);
                            break;
                        case ClassModel.HIERARCHY:
                            bringToHierarchy(sc);
                            break;
                    }
                }
            }
            // The ArrayDeque can grow particularly large but the implementation will
            // never shrink the backing array, leaving a possibly large memory leak.
            nextClasses[i] = new ArrayDeque<ClassModel>(0);
        }
    }

    protected void addToResolveWorklist(Type type, int level) {
        // We go from Type -> SootClass directly, since RefType.getSootClass
        // calls makeClassRef anyway
        if (type instanceof RefType) {
            addToResolveWorklist(((RefType) type).getSootClass(), level);
        } else if (type instanceof ArrayType) {
            addToResolveWorklist(((ArrayType) type).baseType, level);
        }
        // Other types ignored
    }

    protected void addToResolveWorklist(ClassModel sc, int desiredLevel) {
        if (sc.getResolvingLevel() >= desiredLevel) {
            return;
        }
        nextClasses[desiredLevel].add(sc);
    }

    /**
     * Hierarchy - we know the hierarchy of the class and that's it requires at least Hierarchy for all supertypes and
     * enclosing types.
     */
    protected void bringToHierarchy(ClassModel sc) {
        if (sc.getResolvingLevel() >= ClassModel.HIERARCHY) {
            return;
        }

        if (Options.v().debug_resolver()) {
            logger.debug("bringing to HIERARCHY: " + sc);
        }

        sc.setResolvingLevel(ClassModel.HIERARCHY);

        bringToHierarchyUnchecked(sc);
    }

    protected void bringToHierarchyUnchecked(ClassModel sc) {
        final String className = sc.getName();
        final ClassSource classSource = SourceLocator.v().getClassSource(className);

        try {
            boolean modelAsPhantomRef = (classSource == null);
            if (modelAsPhantomRef) {
                if (!Scene.v().allowsPhantomRefs()) {
                    String suffix = "";
                    if ("java.lang.Object".equals(className)) {
                        suffix = " Try adding rt.jar to Soot's classpath, e.g.:\n" + "java -cp sootclasses.jar soot.Main -cp "
                                + ".:/path/to/jdk/jre/lib/rt.jar <other options>";
                    } else if ("javax.crypto.Cipher".equals(className)) {
                        suffix = " Try adding jce.jar to Soot's classpath, e.g.:\n" + "java -cp sootclasses.jar soot.Main -cp "
                                + ".:/path/to/jdk/jre/lib/rt.jar:/path/to/jdk/jre/lib/jce.jar <other options>";
                    }
                    throw new SootClassNotFoundException(
                            "couldn't find class: " + className + " (is your soot-class-path set properly?)" + suffix);
                } else {
                    // logger.warn(className + " is a phantom class!");
                    sc.setPhantomClass();
                }
            } else {
                Dependencies dependencies = classSource.resolve(sc);
                if (!dependencies.typesToSignature.isEmpty()) {
                    classToTypesSignature.putAll(sc, dependencies.typesToSignature);
                }
                if (!dependencies.typesToHierarchy.isEmpty()) {
                    classToTypesHierarchy.putAll(sc, dependencies.typesToHierarchy);
                }
            }
        } finally {
            if (classSource != null) {
                classSource.close();
            }
        }
        reResolveHierarchy(sc, ClassModel.HIERARCHY);
    }

    public void reResolveHierarchy(ClassModel sc, int level) {
        // Bring superclasses to hierarchy
        ClassModel superClass = sc.getSuperclassUnsafe();
        if (superClass != null) {
            addToResolveWorklist(superClass, level);
        }
        ClassModel outerClass = sc.getOuterClassUnsafe();
        if (outerClass != null) {
            addToResolveWorklist(outerClass, level);
        }
        for (ClassModel iface : sc.getInterfaceTypes()) {
            addToResolveWorklist(iface, level);
        }
    }

    /**
     * Signatures - we know the signatures of all methods and fields requires at least Hierarchy for all referred to types in
     * these signatures.
     */
    protected void bringToSignatures(ClassModel sc) {
        if (sc.getResolvingLevel() >= ClassModel.SIGNATURES) {
            return;
        }
        bringToHierarchy(sc);
        if (Options.v().debug_resolver()) {
            logger.debug("bringing to SIGNATURES: " + sc);
        }
        sc.setResolvingLevel(ClassModel.SIGNATURES);

        bringToSignaturesUnchecked(sc);
    }

    protected void bringToSignaturesUnchecked(ClassModel sc) {
        for (FieldModel f : sc.getFieldModels()) {
            addToResolveWorklist(f.getType(), ClassModel.HIERARCHY);
        }
        for (MethodModel m : sc.getMethodModels()) {
            addToResolveWorklist(m.getReturnType(), ClassModel.HIERARCHY);
            for (Type ptype : m.getParameterTypes()) {
                addToResolveWorklist(ptype, ClassModel.HIERARCHY);
            }
            List<ClassModel> exceptions = m.getExceptionsUnsafe();
            if (exceptions != null) {
                for (ClassModel exception : exceptions) {
                    addToResolveWorklist(exception, ClassModel.HIERARCHY);
                }
            }
        }

        // Bring superclasses to signatures
        reResolveHierarchy(sc, ClassModel.SIGNATURES);
    }

    /**
     * Bodies - we can now start loading the bodies of methods for all referred to methods and fields in the bodies, requires
     * signatures for the method receiver and field container, and hierarchy for all other classes referenced in method
     * references. Current implementation does not distinguish between the receiver and other references. Therefore, it is
     * conservative and brings all of them to signatures. But this could/should be improved.
     */
    protected void bringToBodies(ClassModel sc) {
        if (sc.getResolvingLevel() >= ClassModel.BODIES) {
            return;
        }
        bringToSignatures(sc);
        if (Options.v().debug_resolver()) {
            logger.debug("bringing to BODIES: " + sc);
        }
        sc.setResolvingLevel(ClassModel.BODIES);

        bringToBodiesUnchecked(sc);
    }

    protected void bringToBodiesUnchecked(ClassModel sc) {
        {
            Collection<Type> references = classToTypesHierarchy.get(sc);
            if (references != null) {
                // This must be an iterator, not a for-all since the underlying
                // collection may change as we go
                for (Type t : references) {
                    addToResolveWorklist(t, ClassModel.HIERARCHY);
                }
            }
        }
        {
            Collection<Type> references = classToTypesSignature.get(sc);
            if (references != null) {
                // This must be an iterator, not a for-all since the underlying
                // collection may change as we go
                for (Type t : references) {
                    addToResolveWorklist(t, ClassModel.SIGNATURES);
                }
            }
        }
    }

    public void reResolve(ClassModel cl, int newResolvingLevel) {
        int resolvingLevel = cl.getResolvingLevel();
        if (resolvingLevel >= newResolvingLevel) {
            return;
        }
        reResolveHierarchy(cl, ClassModel.HIERARCHY);
        cl.setResolvingLevel(newResolvingLevel);
        addToResolveWorklist(cl, resolvingLevel);
        processResolveWorklist();
    }

    public void reResolve(ClassModel cl) {
        reResolve(cl, ClassModel.HIERARCHY);
    }


    public static class SootClassNotFoundException extends RuntimeException {
        /**
         *
         */
        @Serial
        private static final long serialVersionUID = 1563461446590293827L;

        public SootClassNotFoundException(String s) {
            super(s);
        }
    }
}
