package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import soot.jimple.SpecialInvokeExpr;
import soot.util.ArraySet;
import soot.util.Chain;

import java.util.*;

/**
 * Represents the class hierarchy. It is closely linked to a Scene, and must be recreated if the Scene changes.
 * <p>
 * The general convention is that if a method name contains "Including", then it returns the non-strict result; otherwise, it
 * does a strict query (e.g. strict superclass).
 */
public class Hierarchy {

    // These two maps are not filled in the constructor.
    protected Map<ClassModel, List<ClassModel>> classToSubclasses;
    protected Map<ClassModel, List<ClassModel>> interfaceToSubinterfaces;
    protected Map<ClassModel, List<ClassModel>> interfaceToSuperinterfaces;

    protected Map<ClassModel, List<ClassModel>> classToDirSubclasses;
    protected Map<ClassModel, List<ClassModel>> interfaceToDirSubinterfaces;
    protected Map<ClassModel, List<ClassModel>> interfaceToDirSuperinterfaces;

    // This holds the direct implementers.
    protected Map<ClassModel, List<ClassModel>> interfaceToDirImplementers;

    final Scene sc;
    final int state;

    /**
     * Constructs a hierarchy from the current scene.
     */
    public Hierarchy() {
        this.sc = Scene.v();
        this.state = sc.getState();

        // Well, this used to be describable by 'Duh'.
        // Construct the subclasses hierarchy and the subinterfaces hierarchy.
        {
            Chain<ClassModel> allClasses = sc.getClasses();
            final int mapSize = allClasses.size() * 2 + 1;

            this.classToSubclasses = new HashMap<ClassModel, List<ClassModel>>(mapSize, 0.7f);
            this.interfaceToSubinterfaces = new HashMap<ClassModel, List<ClassModel>>(mapSize, 0.7f);
            this.interfaceToSuperinterfaces = new HashMap<ClassModel, List<ClassModel>>(mapSize, 0.7f);

            this.classToDirSubclasses = new HashMap<ClassModel, List<ClassModel>>(mapSize, 0.7f);
            this.interfaceToDirSubinterfaces = new HashMap<ClassModel, List<ClassModel>>(mapSize, 0.7f);
            this.interfaceToDirSuperinterfaces = new HashMap<ClassModel, List<ClassModel>>(mapSize, 0.7f);
            this.interfaceToDirImplementers = new HashMap<ClassModel, List<ClassModel>>(mapSize, 0.7f);

            initializeHierarchy(allClasses);
        }
    }

    /**
     * Initializes the hierarchy given a chain of all classes that shall be included in the hierarchy
     *
     * @param allClasses The chain of all classes to be included in the hierarchy
     */
    protected void initializeHierarchy(Chain<ClassModel> allClasses) {
        for (ClassModel c : allClasses) {
            if (c.resolvingLevel() < ClassModel.HIERARCHY) {
                continue;
            }

            if (c.isInterface()) {
                interfaceToDirSubinterfaces.put(c, new ArrayList<ClassModel>());
                interfaceToDirSuperinterfaces.put(c, new ArrayList<ClassModel>());
                interfaceToDirImplementers.put(c, new ArrayList<ClassModel>());
            } else {
                classToDirSubclasses.put(c, new ArrayList<ClassModel>());
            }
        }

        for (ClassModel c : allClasses) {
            if (c.resolvingLevel() < ClassModel.HIERARCHY) {
                continue;
            }

            if (c.hasSuperclass()) {
                if (c.isInterface()) {
                    List<ClassModel> l2 = interfaceToDirSuperinterfaces.get(c);
                    for (ClassModel i : c.getInterfaces()) {
                        if (c.resolvingLevel() < ClassModel.HIERARCHY) {
                            continue;
                        }
                        List<ClassModel> l = interfaceToDirSubinterfaces.get(i);
                        if (l != null) {
                            l.add(c);
                        }
                        if (l2 != null) {
                            l2.add(i);
                        }
                    }
                } else {
                    List<ClassModel> l = classToDirSubclasses.get(c.getSuperclass());
                    if (l != null) {
                        l.add(c);
                    }

                    for (ClassModel i : c.getInterfaces()) {
                        if (c.resolvingLevel() < ClassModel.HIERARCHY) {
                            continue;
                        }
                        List<ClassModel> l2 = interfaceToDirImplementers.get(i);
                        if (l2 != null) {
                            l2.add(c);
                        }
                    }
                }
            }
        }

        // Fill the directImplementers lists with subclasses.
        for (ClassModel c : allClasses) {
            if (c.resolvingLevel() < ClassModel.HIERARCHY) {
                continue;
            }
            if (c.isInterface()) {
                Set<ClassModel> s = new ArraySet<ClassModel>();
                for (ClassModel c0 : interfaceToDirImplementers.get(c)) {
                    if (c.resolvingLevel() < ClassModel.HIERARCHY) {
                        continue;
                    }
                    s.addAll(getSubclassesOfIncluding(c0));
                }
                interfaceToDirImplementers.put(c, new ArrayList<ClassModel>(s));
            } else if (c.hasSuperclass()) {
                List<ClassModel> l = classToDirSubclasses.get(c);
                if (l != null) {
                    classToDirSubclasses.put(c, new ArrayList<>(l));
                }
            }
        }
    }

    protected void checkState() {
        if (state != sc.getState()) {
            throw new ConcurrentModificationException("Scene changed for Hierarchy!");
        }
    }

    // This includes c in the list of subclasses.

    /**
     * Returns a list of subclasses of c, including itself.
     */
    public List<ClassModel> getSubclassesOfIncluding(ClassModel c) {
        c.checkLevel(ClassModel.HIERARCHY);
        if (c.isInterface()) {
            throw new RuntimeException("class needed!");
        }

        List<ClassModel> subclasses = getSubclassesOf(c);
        List<ClassModel> result = new ArrayList<ClassModel>(subclasses.size() + 1);
        result.addAll(subclasses);
        result.add(c);

        return Collections.unmodifiableList(result);
    }

    /**
     * Returns a list of subclasses of c, excluding itself.
     */
    public List<ClassModel> getSubclassesOf(ClassModel c) {
        c.checkLevel(ClassModel.HIERARCHY);
        if (c.isInterface()) {
            throw new RuntimeException("class needed!");
        }

        checkState();

        // If already cached, return the value.
        List<ClassModel> retVal = classToSubclasses.get(c);
        if (retVal != null) {
            return retVal;
        }

        // Otherwise, build up the hashmap.
        {
            ArrayList<ClassModel> l = new ArrayList<ClassModel>();
            for (ClassModel cls : classToDirSubclasses.get(c)) {
                if (cls.resolvingLevel() < ClassModel.HIERARCHY) {
                    continue;
                }
                l.addAll(getSubclassesOfIncluding(cls));
            }
            l.trimToSize();
            retVal = Collections.unmodifiableList(l);
        }

        classToSubclasses.put(c, retVal);
        return retVal;
    }

    /**
     * Returns a list of superclasses of {@code sootClass}, including itself.
     *
     * @param classModel the <strong>class</strong> of which superclasses will be taken. Must not be {@code null} or interface
     * @return list of superclasses, including itself
     * @throws IllegalArgumentException when passed class is an interface
     * @throws NullPointerException     when passed argument is {@code null}
     */
    public List<ClassModel> getSuperclassesOfIncluding(ClassModel classModel) {
        List<ClassModel> superclasses = getSuperclassesOf(classModel);
        List<ClassModel> result = new ArrayList<>(superclasses.size() + 1);
        result.add(classModel);
        result.addAll(superclasses);

        return Collections.unmodifiableList(result);
    }

    /**
     * Returns a list of <strong>direct</strong> superclasses of passed class in reverse order, starting with its parent.
     *
     * @param classModel the <strong>class</strong> of which superclasses will be taken. Must not be {@code null} or interface
     * @return list of superclasses
     * @throws IllegalArgumentException when passed class is an interface
     * @throws NullPointerException     when passed argument is {@code null}
     */
    public List<ClassModel> getSuperclassesOf(ClassModel classModel) {
        classModel.checkLevel(ClassModel.HIERARCHY);
        if (classModel.isInterface()) {
            throw new IllegalArgumentException(classModel.getName() + " is an interface, but class is expected");
        }

        checkState();

        final List<ClassModel> superclasses = new ArrayList<>();
        for (ClassModel current = classModel; current.hasSuperclass(); ) {
            ClassModel superclass = current.getSuperclass();
            superclasses.add(superclass);
            current = superclass;
        }

        return Collections.unmodifiableList(superclasses);
    }

    /**
     * Returns a list of subinterfaces of sootClass, including itself.
     *
     * @param classModel the <strong>interface</strong> of which subinterfaces will be taken. Must not be {@code null} or class
     * @return list of subinterfaces, including passed one
     * @throws IllegalArgumentException when passed class is a class
     * @throws NullPointerException     when passed argument is {@code null}
     */
    public List<ClassModel> getSubinterfacesOfIncluding(ClassModel classModel) {
        List<ClassModel> subinterfaces = getSubinterfacesOf(classModel);
        List<ClassModel> result = new ArrayList<>(subinterfaces.size() + 1);
        result.addAll(subinterfaces);
        result.add(classModel);

        return Collections.unmodifiableList(result);
    }

    /**
     * Returns a list of subinterfaces of sootClass, excluding itself.
     *
     * @param classModel the <strong>interface</strong> of which subinterfaces will be taken. Must not be {@code null} or class
     * @return list of subinterfaces, including passed one
     * @throws IllegalArgumentException when passed sootClass is a class
     * @throws NullPointerException     when passed argument is {@code null}
     */
    public List<ClassModel> getSubinterfacesOf(ClassModel classModel) {
        classModel.checkLevel(ClassModel.HIERARCHY);
        if (!classModel.isInterface()) {
            throw new IllegalArgumentException(classModel.getName() + " is a class, but interface is expected");
        }

        checkState();

        // If already cached, return the value.
        List<ClassModel> retVal = interfaceToSubinterfaces.get(classModel);
        if (retVal != null) {
            return retVal;
        }

        // Otherwise, build up the hashmap.
        {
            List<ClassModel> directSubInterfaces = interfaceToDirSubinterfaces.get(classModel);
            if (directSubInterfaces == null || directSubInterfaces.isEmpty()) {
                return Collections.emptyList();
            }
            final ArrayList<ClassModel> l = new ArrayList<>();
            for (ClassModel si : directSubInterfaces) {
                l.addAll(getSubinterfacesOfIncluding(si));
            }
            l.trimToSize();
            retVal = Collections.unmodifiableList(l);
        }

        interfaceToSubinterfaces.put(classModel, retVal);
        return retVal;
    }

    /**
     * Returns a list of superinterfaces of c, including itself.
     */
    public List<ClassModel> getSuperinterfacesOfIncluding(ClassModel c) {
        c.checkLevel(ClassModel.HIERARCHY);
        if (!c.isInterface()) {
            throw new RuntimeException("interface needed!");
        }

        List<ClassModel> superinterfaces = getSuperinterfacesOf(c);
        List<ClassModel> result = new ArrayList<ClassModel>(superinterfaces.size() + 1);
        result.addAll(superinterfaces);
        result.add(c);

        return Collections.unmodifiableList(result);
    }

    /**
     * Returns a list of superinterfaces of c, excluding itself.
     */
    public List<ClassModel> getSuperinterfacesOf(ClassModel c) {
        c.checkLevel(ClassModel.HIERARCHY);
        if (!c.isInterface()) {
            throw new RuntimeException("interface needed!");
        }

        checkState();

        // If already cached, return the value.
        List<ClassModel> retVal = interfaceToSuperinterfaces.get(c);
        if (retVal != null) {
            return retVal;
        }

        // Otherwise, build up the hashmap.
        {
            ArrayList<ClassModel> l = new ArrayList<ClassModel>();
            for (ClassModel si : interfaceToDirSuperinterfaces.get(c)) {
                l.addAll(getSuperinterfacesOfIncluding(si));
            }
            l.trimToSize();
            retVal = Collections.unmodifiableList(l);
        }

        interfaceToSuperinterfaces.put(c, retVal);
        return retVal;
    }

    /**
     * Returns a list of direct superclasses of c, excluding c.
     */
    public List<ClassModel> getDirectSuperclassesOf(ClassModel c) {
        throw new RuntimeException("Not implemented yet!");
    }

    /**
     * Returns a list of direct subclasses of c, excluding c.
     */
    public List<ClassModel> getDirectSubclassesOf(ClassModel c) {
        c.checkLevel(ClassModel.HIERARCHY);
        if (c.isInterface()) {
            throw new RuntimeException("class needed!");
        }

        checkState();

        return Collections.unmodifiableList(classToDirSubclasses.get(c));
    }

    // This includes c in the list of subclasses.

    /**
     * Returns a list of direct subclasses of c, including c.
     */
    public List<ClassModel> getDirectSubclassesOfIncluding(ClassModel c) {
        c.checkLevel(ClassModel.HIERARCHY);
        if (c.isInterface()) {
            throw new RuntimeException("class needed!");
        }

        checkState();

        List<ClassModel> subclasses = classToDirSubclasses.get(c);
        List<ClassModel> l = new ArrayList<ClassModel>(subclasses.size() + 1);
        l.addAll(subclasses);
        l.add(c);

        return Collections.unmodifiableList(l);
    }

    /**
     * Returns a list of direct superinterfaces of c.
     */
    public List<ClassModel> getDirectSuperinterfacesOf(ClassModel c) {
        throw new RuntimeException("Not implemented yet!");
    }

    /**
     * Returns a list of direct subinterfaces of c.
     */
    public List<ClassModel> getDirectSubinterfacesOf(ClassModel c) {
        c.checkLevel(ClassModel.HIERARCHY);
        if (!c.isInterface()) {
            throw new RuntimeException("interface needed!");
        }

        checkState();

        return Collections.unmodifiableList(interfaceToDirSubinterfaces.get(c));
    }

    /**
     * Returns a list of direct subinterfaces of c, including itself.
     */
    public List<ClassModel> getDirectSubinterfacesOfIncluding(ClassModel c) {
        c.checkLevel(ClassModel.HIERARCHY);
        if (!c.isInterface()) {
            throw new RuntimeException("interface needed!");
        }

        checkState();

        List<ClassModel> subinterfaces = interfaceToDirSubinterfaces.get(c);
        List<ClassModel> l = new ArrayList<ClassModel>(subinterfaces.size() + 1);
        l.addAll(subinterfaces);
        l.add(c);

        return Collections.unmodifiableList(l);
    }

    /**
     * Returns a list of direct implementers of c, excluding itself.
     */
    public List<ClassModel> getDirectImplementersOf(ClassModel i) {
        i.checkLevel(ClassModel.HIERARCHY);
        if (!i.isInterface()) {
            throw new RuntimeException("interface needed; got " + i);
        }

        checkState();

        return Collections.unmodifiableList(interfaceToDirImplementers.get(i));
    }

    /**
     * Returns a list of implementers of c, excluding itself.
     */
    public List<ClassModel> getImplementersOf(ClassModel i) {
        i.checkLevel(ClassModel.HIERARCHY);
        if (!i.isInterface()) {
            throw new RuntimeException("interface needed; got " + i);
        }

        checkState();

        ArraySet<ClassModel> set = new ArraySet<ClassModel>();
        for (ClassModel c : getSubinterfacesOfIncluding(i)) {
            set.addAll(getDirectImplementersOf(c));
        }
        return Collections.unmodifiableList(new ArrayList<ClassModel>(set));
    }

    /**
     * Returns true if child is a subclass of possibleParent. If one of the known parent classes is phantom, we conservatively
     * assume that the current class might be a child.
     */
    public boolean isClassSubclassOf(ClassModel child, ClassModel possibleParent) {
        child.checkLevel(ClassModel.HIERARCHY);
        possibleParent.checkLevel(ClassModel.HIERARCHY);

        List<ClassModel> parentClasses = getSuperclassesOf(child);
        if (parentClasses.contains(possibleParent)) {
            return true;
        }

        for (ClassModel sc : parentClasses) {
            if (sc.isPhantom()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if child is, or is a subclass of, possibleParent. If one of the known parent classes is phantom, we
     * conservatively assume that the current class might be a child.
     */
    public boolean isClassSubclassOfIncluding(ClassModel child, ClassModel possibleParent) {
        child.checkLevel(ClassModel.HIERARCHY);
        possibleParent.checkLevel(ClassModel.HIERARCHY);

        List<ClassModel> parentClasses = getSuperclassesOfIncluding(child);
        if (parentClasses.contains(possibleParent)) {
            return true;
        }

        for (ClassModel sc : parentClasses) {
            if (sc.isPhantom()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if child is a direct subclass of possibleParent.
     */
    public boolean isClassDirectSubclassOf(ClassModel c, ClassModel c2) {
        throw new RuntimeException("Not implemented yet!");
    }

    /**
     * Returns true if child is a superclass of possibleParent.
     */
    public boolean isClassSuperclassOf(ClassModel parent, ClassModel possibleChild) {
        parent.checkLevel(ClassModel.HIERARCHY);
        possibleChild.checkLevel(ClassModel.HIERARCHY);
        return getSubclassesOf(parent).contains(possibleChild);
    }

    /**
     * Returns true if parent is, or is a superclass of, possibleChild.
     */
    public boolean isClassSuperclassOfIncluding(ClassModel parent, ClassModel possibleChild) {
        parent.checkLevel(ClassModel.HIERARCHY);
        possibleChild.checkLevel(ClassModel.HIERARCHY);
        return getSubclassesOfIncluding(parent).contains(possibleChild);
    }

    /**
     * Returns true if child is a subinterface of possibleParent.
     */
    public boolean isInterfaceSubinterfaceOf(ClassModel child, ClassModel possibleParent) {
        child.checkLevel(ClassModel.HIERARCHY);
        possibleParent.checkLevel(ClassModel.HIERARCHY);
        return getSubinterfacesOf(possibleParent).contains(child);
    }

    /**
     * Returns true if child is a direct subinterface of possibleParent.
     */
    public boolean isInterfaceDirectSubinterfaceOf(ClassModel child, ClassModel possibleParent) {
        child.checkLevel(ClassModel.HIERARCHY);
        possibleParent.checkLevel(ClassModel.HIERARCHY);
        return getDirectSubinterfacesOf(possibleParent).contains(child);
    }

    /**
     * Returns true if parent is a superinterface of possibleChild.
     */
    public boolean isInterfaceSuperinterfaceOf(ClassModel parent, ClassModel possibleChild) {
        parent.checkLevel(ClassModel.HIERARCHY);
        possibleChild.checkLevel(ClassModel.HIERARCHY);
        return getSuperinterfacesOf(possibleChild).contains(parent);
    }

    /**
     * Returns true if parent is a direct superinterface of possibleChild.
     */
    public boolean isInterfaceDirectSuperinterfaceOf(ClassModel parent, ClassModel possibleChild) {
        parent.checkLevel(ClassModel.HIERARCHY);
        possibleChild.checkLevel(ClassModel.HIERARCHY);
        return getDirectSuperinterfacesOf(possibleChild).contains(parent);
    }

    /**
     * Returns the most specific type which is an ancestor of both c1 and c2.
     */
    public ClassModel getLeastCommonSuperclassOf(ClassModel c1, ClassModel c2) {
        c1.checkLevel(ClassModel.HIERARCHY);
        c2.checkLevel(ClassModel.HIERARCHY);
        throw new RuntimeException("Not implemented yet!");
    }

    // Questions about method invocation.

    /**
     * Checks whether check is a visible class in view of the from class. It assumes that protected and private classes do not
     * exit. If they exist and check is either protected or private, the check will return false.
     */
    public boolean isVisible(ClassModel from, ClassModel check) {
        if (check.isPublic()) {
            return true;
        }

        if (check.isProtected() || check.isPrivate()) {
            return false;
        }

        // package visibility
        return from.getJavaPackageName().equals(check.getJavaPackageName());
    }

    /**
     * Returns true if the classmember m is visible from code in the class from.
     */
    public boolean isVisible(ClassModel from, ClassMember m) {
        from.checkLevel(ClassModel.HIERARCHY);
        final ClassModel declaringClass = m.getDeclaringClass();
        declaringClass.checkLevel(ClassModel.HIERARCHY);

        if (!isVisible(from, declaringClass)) {
            return false;
        }
        if (m.isPublic()) {
            return true;
        }
        if (m.isPrivate()) {
            return from.equals(declaringClass);
        }
        if (m.isProtected()) {
            return isClassSubclassOfIncluding(from, declaringClass)
                    || from.getJavaPackageName().equals(declaringClass.getJavaPackageName());
        }
        // m is package
        return from.getJavaPackageName().equals(declaringClass.getJavaPackageName());
    }

    /**
     * Given an object of actual type C (o = new C()), returns the method which will be called on an o.f() invocation.
     */
    public SootMethod resolveConcreteDispatch(ClassModel concreteType, SootMethod m) {
        concreteType.checkLevel(ClassModel.HIERARCHY);
        m.getDeclaringClass().checkLevel(ClassModel.HIERARCHY);
        checkState();

        if (concreteType.isInterface()) {
            throw new RuntimeException("class needed!");
        }

        final String methodSig = m.getSubSignature();
        for (ClassModel c : getSuperclassesOfIncluding(concreteType)) {
            SootMethod sm = c.getMethodUnsafe(methodSig);
            if (sm != null && isVisible(c, m)) {
                return sm;
            }
        }
        throw new RuntimeException("could not resolve concrete dispatch!\nType: " + concreteType + "\nMethod: " + m);
    }

    /**
     * Given a set of definite receiver types, returns a list of possible targets.
     */
    public List<SootMethod> resolveConcreteDispatch(List<Type> classes, SootMethod m) {
        m.getDeclaringClass().checkLevel(ClassModel.HIERARCHY);
        checkState();

        Set<SootMethod> s = new ArraySet<SootMethod>();
        for (Type cls : classes) {
            if (cls instanceof RefType) {
                s.add(resolveConcreteDispatch(((RefType) cls).getSootClass(), m));
            } else if (cls instanceof ArrayType) {
                s.add(resolveConcreteDispatch(Scene.v().getObjectType().getSootClass(), m));
            } else {
                throw new RuntimeException("Unable to resolve concrete dispatch of type " + cls);
            }
        }

        return Collections.unmodifiableList(new ArrayList<SootMethod>(s));
    }

    // what can get called for c & all its subclasses

    /**
     * Given an abstract dispatch to an object of type c and a method m, gives a list of possible receiver methods.
     */
    public List<SootMethod> resolveAbstractDispatch(ClassModel c, SootMethod m) {
        c.checkLevel(ClassModel.HIERARCHY);
        m.getDeclaringClass().checkLevel(ClassModel.HIERARCHY);
        checkState();

        Collection<ClassModel> classes;
        if (c.isInterface()) {
            classes = new HashSet<ClassModel>();
            for (ClassModel classModel : getImplementersOf(c)) {
                classes.addAll(getSubclassesOfIncluding(classModel));
            }
        } else {
            classes = getSubclassesOfIncluding(c);
        }

        Set<SootMethod> s = new ArraySet<SootMethod>();
        for (ClassModel cl : classes) {
            if (!Modifier.isAbstract(cl.getModifiers())) {
                s.add(resolveConcreteDispatch(cl, m));
            }
        }

        return Collections.unmodifiableList(new ArrayList<SootMethod>(s));
    }

    // what can get called if you have a set of possible receiver types

    /**
     * Returns a list of possible targets for the given method and set of receiver types.
     */
    public List<SootMethod> resolveAbstractDispatch(List<ClassModel> classes, SootMethod m) {
        m.getDeclaringClass().checkLevel(ClassModel.HIERARCHY);

        Set<SootMethod> s = new ArraySet<SootMethod>();
        for (ClassModel classModel : classes) {
            s.addAll(resolveAbstractDispatch(classModel, m));
        }

        return Collections.unmodifiableList(new ArrayList<SootMethod>(s));
    }

    /**
     * Returns the target for the given SpecialInvokeExpr.
     */
    public SootMethod resolveSpecialDispatch(SpecialInvokeExpr ie, SootMethod container) {
        final ClassModel containerClass = container.getDeclaringClass();
        containerClass.checkLevel(ClassModel.HIERARCHY);
        final SootMethod target = ie.getMethod();
        final ClassModel targetClass = target.getDeclaringClass();
        targetClass.checkLevel(ClassModel.HIERARCHY);

        /*
         * This is a bizarre condition! Hopefully the implementation is correct. See VM Spec, 2nd Edition, Chapter 6, in the
         * definition of invokespecial.
         */
        if ("<init>".equals(target.getName()) || target.isPrivate()) {
            return target;
        } else if (isClassSubclassOf(targetClass, containerClass)) {
            return resolveConcreteDispatch(containerClass, target);
        } else {
            return target;
        }
    }
}
