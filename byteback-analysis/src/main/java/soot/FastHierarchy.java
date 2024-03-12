package soot;

import byteback.analysis.model.ClassModel;
import byteback.analysis.model.MethodModel;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import soot.jimple.spark.internal.TypeManager;
import soot.options.Options;
import soot.util.ConcurrentHashMultiMap;
import soot.util.MultiMap;
import soot.util.NumberedString;

import java.util.*;

/**
 * Represents the class hierarchy. It is closely linked to a Scene, and must be recreated if the Scene changes.
 *
 * <p>
 * This version supercedes the old soot.Hierarchy class.
 *
 * @author Ondrej Lhotak
 * @author Manuel Benz 22.10.19 - Fixed concrete/abstract dispatch methods to car for default interface methods and account
 * for overwritten return types
 */
public class FastHierarchy {

    protected static final int USE_INTERVALS_BOUNDARY = 100;

    protected Table<ClassModel, NumberedString, MethodModel> typeToVtbl = HashBasedTable.create();

    /**
     * This map holds all key,value pairs such that value.getSuperclass() == key. This is one of the three maps that hold the
     * inverse of the relationships given by the getSuperclass and getInterfaces methods of SootClass.
     */
    protected MultiMap<ClassModel, ClassModel> classToSubclasses = new ConcurrentHashMultiMap<ClassModel, ClassModel>();

    /**
     * This map holds all key,value pairs such that value is an interface and key is in value.getInterfaces(). This is one of
     * the three maps that hold the inverse of the relationships given by the getSuperclass and getInterfaces methods of
     * SootClass.
     */
    protected MultiMap<ClassModel, ClassModel> interfaceToSubinterfaces = new ConcurrentHashMultiMap<ClassModel, ClassModel>();

    /**
     * This map holds all key,value pairs such that value is a class (NOT an interface) and key is in value.getInterfaces().
     * This is one of the three maps that hold the inverse of the relationships given by the getSuperclass and getInterfaces
     * methods of SootClass.
     */
    protected MultiMap<ClassModel, ClassModel> interfaceToImplementers = new ConcurrentHashMultiMap<ClassModel, ClassModel>();

    /**
     * This map is a transitive closure of interfaceToSubinterfaces, and each set contains its superinterface itself.
     */
    protected MultiMap<ClassModel, ClassModel> interfaceToAllSubinterfaces = new ConcurrentHashMultiMap<ClassModel, ClassModel>();

    /**
     * This map gives, for an interface, all concrete classes that implement that interface and all its subinterfaces, but NOT
     * their subclasses.
     */
    protected MultiMap<ClassModel, ClassModel> interfaceToAllImplementers = new ConcurrentHashMultiMap<ClassModel, ClassModel>();

    /**
     * For each class (NOT interface), this map contains a Interval, which is a pair of numbers giving a preorder and postorder
     * ordering of classes in the inheritance tree.
     */
    protected Map<ClassModel, Interval> classToInterval = new HashMap<ClassModel, Interval>();

    protected final Scene sc;
    protected final RefType rtObject;
    protected final RefType rtSerializable;
    protected final RefType rtCloneable;

    protected class Interval {
        int lower;
        int upper;

        public Interval() {
        }

        public Interval(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }

        public boolean isSubrange(Interval potentialSubrange) {
            return (potentialSubrange == this)
                    || (potentialSubrange != null && this.lower <= potentialSubrange.lower && this.upper >= potentialSubrange.upper);
        }
    }

    protected int dfsVisit(int start, ClassModel c) {
        Interval r = new Interval();
        r.lower = start++;
        Collection<ClassModel> col = classToSubclasses.get(c);
        if (col != null) {
            for (ClassModel sc : col) {
                // For some awful reason, Soot thinks interface are subclasses
                // of java.lang.Object
                if (sc.isInterface()) {
                    continue;
                }
                start = dfsVisit(start, sc);
            }
        }
        r.upper = start++;
        if (c.isInterface()) {
            throw new RuntimeException("Attempt to dfs visit interface " + c);
        }
        classToInterval.putIfAbsent(c, r);
        return start;
    }

    /**
     * Constructs a hierarchy from the current scene.
     */
    public FastHierarchy() {
        this.sc = Scene.v();

        this.rtObject = sc.getObjectType();
        this.rtSerializable = RefType.v("java.io.Serializable");
        this.rtCloneable = RefType.v("java.lang.Cloneable");

        /* First build the inverse maps. */
        buildInverseMaps();

        /* Now do a dfs traversal to get the Interval numbers. */
        int r = dfsVisit(0, sc.getObjectType().getSootClass());
        /*
         * also have to traverse for all phantom classes because they also can be roots of the type hierarchy
         */
        for (Iterator<ClassModel> phantomClassIt = sc.getPhantomClasses().snapshotIterator(); phantomClassIt.hasNext(); ) {
            ClassModel phantomClass = phantomClassIt.next();
            if (!phantomClass.isInterface()) {
                r = dfsVisit(r, phantomClass);
            }
        }
    }

    protected void buildInverseMaps() {
        for (ClassModel cl : sc.getClasses().getElementsUnsorted()) {
            if (cl.getResolvingLevel() < ClassModel.HIERARCHY) {
                continue;
            }
            if (!cl.isInterface()) {
                ClassModel superClass = cl.getSuperclassUnsafe();
                if (superClass != null) {
                    classToSubclasses.put(superClass, cl);
                }
            }
            for (ClassModel supercl : cl.getInterfaceTypes()) {
                if (cl.isInterface()) {
                    interfaceToSubinterfaces.put(supercl, cl);
                } else {
                    interfaceToImplementers.put(supercl, cl);
                }
            }
        }
    }

    /**
     * Return true if class child is a subclass of class parent, neither of them being allowed to be interfaces. If we don't
     * know any of the classes, we always return false
     */
    public boolean isSubclass(ClassModel child, ClassModel parent) {
        child.checkLevel(ClassModel.HIERARCHY);
        parent.checkLevel(ClassModel.HIERARCHY);

        Interval parentInterval = classToInterval.get(parent);
        Interval childInterval = classToInterval.get(child);
        return parentInterval != null && childInterval != null && parentInterval.isSubrange(childInterval);
    }

    /**
     * For an interface parent (MUST be an interface), returns set of all implementers of it but NOT their subclasses.
     *
     * <p>
     * This method can be used concurrently (is thread safe).
     *
     * @param parent the parent interface.
     * @return an set, possibly empty
     */
    public Set<ClassModel> getAllImplementersOfInterface(ClassModel parent) {
        parent.checkLevel(ClassModel.HIERARCHY);

        Set<ClassModel> result = interfaceToAllImplementers.get(parent);
        if (!result.isEmpty()) {
            return result;
        }
        result = new HashSet<>();
        for (ClassModel subinterface : getAllSubinterfaces(parent)) {
            if (subinterface == parent) {
                continue;
            }
            result.addAll(getAllImplementersOfInterface(subinterface));
        }
        result.addAll(interfaceToImplementers.get(parent));
        interfaceToAllImplementers.putAll(parent, result);
        return result;
    }

    /**
     * For an interface parent (MUST be an interface), returns set of all subinterfaces including <code>parent</code>.
     *
     * <p>
     * This method can be used concurrently (is thread safe).
     *
     * @param parent the parent interface.
     * @return an set, possibly empty
     */
    public Set<ClassModel> getAllSubinterfaces(ClassModel parent) {
        parent.checkLevel(ClassModel.HIERARCHY);

        if (!parent.isInterface()) {
            return Collections.emptySet();
        }
        Set<ClassModel> result = interfaceToAllSubinterfaces.get(parent);
        if (!result.isEmpty()) {
            return result;
        }
        result = new HashSet<>();
        result.add(parent);
        for (ClassModel si : interfaceToSubinterfaces.get(parent)) {
            result.addAll(getAllSubinterfaces(si));
        }
        interfaceToAllSubinterfaces.putAll(parent, result);
        return result;
    }

    /**
     * Given an object of declared type child, returns true if the object can be stored in a variable of type parent. If child
     * is an interface that is not a subinterface of parent, this method will return false even though some objects
     * implementing the child interface may also implement the parent interface.
     */
    public boolean canStoreType(final Type child, final Type parent) {
        if (child == parent || child.equals(parent)) {
            return true;
        } else if (parent instanceof NullType) {
            return false;
        } else if (child instanceof NullType) {
            return parent instanceof RefLikeType;
        } else if (child instanceof RefType) {
            if (parent == rtObject) {
                return true;
            } else if (parent instanceof RefType) {
                return canStoreClass(((RefType) child).getSootClass(), ((RefType) parent).getSootClass());
            } else {
                return false;
            }
        } else if (child instanceof AnySubType) {
            if (!(parent instanceof RefLikeType)) {
                throw new RuntimeException("Unhandled type " + parent + "! Type " + child + " cannot be stored in type " + parent);
            } else if (parent instanceof ArrayType) {
                Type base = ((AnySubType) child).getBase();
                return base == rtObject || base == rtSerializable || base == rtCloneable;
            } else {
                Deque<ClassModel> worklist = new ArrayDeque<ClassModel>();
                ClassModel base = ((AnySubType) child).getBase().getSootClass();
                if (base.isInterface()) {
                    worklist.addAll(getAllImplementersOfInterface(base));
                } else {
                    worklist.add(base);
                }
                final ClassModel parentClass = ((RefType) parent).getSootClass();
                {
                    Set<ClassModel> workset = new HashSet<>();
                    ClassModel cl;
                    while ((cl = worklist.poll()) != null) {
                        if (!workset.add(cl)) {
                            continue;
                        } else if (cl.isConcrete() && canStoreClass(cl, parentClass)) {
                            return true;
                        }
                        worklist.addAll(getSubclassesOf(cl));
                    }
                }
                return false;
            }
        } else if (child instanceof ArrayType) {
            if (parent instanceof RefType) {
                // From Java Language Spec 2nd ed., Chapter 10, Arrays
                return parent == rtObject || parent == rtSerializable || parent == rtCloneable;
            } else if (parent instanceof ArrayType aparent) {
                // You can store a int[][] in a Object[]. Yuck!
                // Also, you can store a Interface[] in a Object[]
              final ArrayType achild = (ArrayType) child;
                if (achild.numDimensions == aparent.numDimensions) {
                    final Type pBaseType = aparent.baseType;
                    final Type cBaseType = achild.baseType;
                    if (cBaseType.equals(pBaseType)) {
                        return true;
                    } else if ((cBaseType instanceof RefType) && (pBaseType instanceof RefType)) {
                        return canStoreType(cBaseType, pBaseType);
                    } else {
                        return false;
                    }
                } else if (achild.numDimensions > aparent.numDimensions) {
                    final Type pBaseType = aparent.baseType;
                    return pBaseType == rtObject || pBaseType == rtSerializable || pBaseType == rtCloneable;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Given an object of declared type child, returns true if the object can be stored in a variable of type parent. If child
     * is an interface that is not a subinterface of parent, this method will return false even though some objects
     * implementing the child interface may also implement the parent interface.
     */
    public boolean canStoreClass(ClassModel child, ClassModel parent) {
        parent.checkLevel(ClassModel.HIERARCHY);
        child.checkLevel(ClassModel.HIERARCHY);

        Interval parentInterval = classToInterval.get(parent);
        Interval childInterval = classToInterval.get(child);
        if (parentInterval != null && childInterval != null) {
            return parentInterval.isSubrange(childInterval);
        } else if (childInterval == null) { // child is interface
            if (parentInterval != null) { // parent is not interface
                return parent == rtObject.getSootClass();
            } else {
                return getAllSubinterfaces(parent).contains(child);
            }
        } else {
            final Set<ClassModel> impl = getAllImplementersOfInterface(parent);
            if (impl.size() > USE_INTERVALS_BOUNDARY) {
                // If we have more than 100 entries it is quite time consuming to check each and every
                // implementing class
                // if it is the "child" class. Therefore we use an alternative implementation which just
                // checks the client
                // class it's super classes and all the interfaces it implements.

                return canStoreClassClassic(child, parent);
            } else {
                // If we only have a few entries, you can't beat the performance of a plain old loop
                // in combination with the interval approach.
                for (ClassModel c : impl) {
                    Interval interval = classToInterval.get(c);
                    if (interval != null && interval.isSubrange(childInterval)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    /**
     * "Classic" implementation using the intuitive approach (without using {@link Interval}) to check whether
     * <code>child</code> can be stored in a type of <code>parent</code>:
     *
     * <p>
     * If <code>parent</code> is not an interface we simply traverse and check the super-classes of <code>child</code>.
     *
     * <p>
     * If <code>parent</code> is an interface we traverse the super-classes of <code>child</code> and check each interface
     * implemented by this class. Also each interface is checked recursively for super interfaces it implements.
     *
     * <p>
     * This implementation can be much faster (compared to the interval based implementation of
     * {@link #canStoreClass(ClassModel, ClassModel)} in cases where one interface is implemented in thousands of classes.
     *
     * @param child
     * @param parent
     * @return
     */
    protected boolean canStoreClassClassic(final ClassModel child, final ClassModel parent) {
        final boolean parentIsInterface = parent.isInterface();
        ArrayDeque<ClassModel> children = new ArrayDeque<>();
        children.add(child);
        for (ClassModel p; (p = children.poll()) != null; ) {
            for (ClassModel sc = p; sc != null; ) {
                if (sc == parent) {
                    return true;
                }
                if (parentIsInterface) {
                    for (ClassModel interf : sc.getInterfaceTypes()) {
                        if (interf == parent) {
                            return true;
                        }
                        children.push(interf);
                    }
                }
                sc = sc.getSuperclassUnsafe();
            }
        }
        return false;
    }

    public Collection<MethodModel> resolveConcreteDispatchWithoutFailing(Collection<Type> concreteTypes, MethodModel m,
                                                                         RefType declaredTypeOfBase) {

        final ClassModel declaringClass = declaredTypeOfBase.getSootClass();
        declaringClass.checkLevel(ClassModel.HIERARCHY);

        Set<MethodModel> ret = new HashSet<MethodModel>();
        for (final Type t : concreteTypes) {
            if (t instanceof AnySubType) {
                HashSet<ClassModel> s = new HashSet<ClassModel>();
                s.add(declaringClass);
                while (!s.isEmpty()) {
                    final ClassModel c = s.iterator().next();
                    s.remove(c);
                    if (!c.isInterface() && !c.isAbstract() && canStoreClass(c, declaringClass)) {
                        MethodModel concreteM = resolveConcreteDispatch(c, m);
                        if (concreteM != null) {
                            ret.add(concreteM);
                        }
                    }
                    {
                        Set<ClassModel> subclasses = classToSubclasses.get(c);
                        if (subclasses != null) {
                            s.addAll(subclasses);
                        }
                    }
                    {
                        Set<ClassModel> subinterfaces = interfaceToSubinterfaces.get(c);
                        if (subinterfaces != null) {
                            s.addAll(subinterfaces);
                        }
                    }
                    {
                        Set<ClassModel> implementers = interfaceToImplementers.get(c);
                        if (implementers != null) {
                            s.addAll(implementers);
                        }
                    }
                }
                return ret;
            } else if (t instanceof RefType) {
                ClassModel concreteClass = ((RefType) t).getSootClass();
                if (!canStoreClass(concreteClass, declaringClass)) {
                    continue;
                }
                MethodModel concreteM;
                try {
                    concreteM = resolveConcreteDispatch(concreteClass, m);
                } catch (Exception e) {
                    concreteM = null;
                }
                if (concreteM != null) {
                    ret.add(concreteM);
                }
            } else if (t instanceof ArrayType) {
                MethodModel concreteM;
                try {
                    concreteM = resolveConcreteDispatch(sc.getObjectType().getSootClass(), m);
                } catch (Exception e) {
                    concreteM = null;
                }
                if (concreteM != null) {
                    ret.add(concreteM);
                }
            } else {
                throw new RuntimeException("Unrecognized reaching type " + t);
            }
        }
        return ret;
    }

    public Collection<MethodModel> resolveConcreteDispatch(Collection<Type> concreteTypes, MethodModel m,
                                                           RefType declaredTypeOfBase) {

        final ClassModel declaringClass = declaredTypeOfBase.getSootClass();
        declaringClass.checkLevel(ClassModel.HIERARCHY);

        Set<MethodModel> ret = new HashSet<MethodModel>();
        for (final Type t : concreteTypes) {
            if (t instanceof AnySubType) {
                HashSet<ClassModel> s = new HashSet<ClassModel>();
                s.add(declaringClass);
                while (!s.isEmpty()) {
                    final ClassModel c = s.iterator().next();
                    s.remove(c);
                    if (!c.isInterface() && !c.isAbstract() && canStoreClass(c, declaringClass)) {
                        MethodModel concreteM = resolveConcreteDispatch(c, m);
                        if (concreteM != null) {
                            ret.add(concreteM);
                        }
                    }
                    {
                        Set<ClassModel> subclasses = classToSubclasses.get(c);
                        if (subclasses != null) {
                            s.addAll(subclasses);
                        }
                    }
                    {
                        Set<ClassModel> subinterfaces = interfaceToSubinterfaces.get(c);
                        if (subinterfaces != null) {
                            s.addAll(subinterfaces);
                        }
                    }
                    {
                        Set<ClassModel> implementers = interfaceToImplementers.get(c);
                        if (implementers != null) {
                            s.addAll(implementers);
                        }
                    }
                }
                return ret;
            } else if (t instanceof RefType) {
                ClassModel concreteClass = ((RefType) t).getSootClass();
                if (!canStoreClass(concreteClass, declaringClass)) {
                    continue;
                }
                MethodModel concreteM = resolveConcreteDispatch(concreteClass, m);
                if (concreteM != null) {
                    ret.add(concreteM);
                }
            } else if (t instanceof ArrayType) {
                MethodModel concreteM = resolveConcreteDispatch(rtObject.getSootClass(), m);
                if (concreteM != null) {
                    ret.add(concreteM);
                }
            } else {
                throw new RuntimeException("Unrecognized reaching type " + t);
            }
        }
        return ret;
    }

    /**
     * Returns true if a method defined in declaringClass with the given modifiers is visible from the class from.
     */
    private boolean isVisible(ClassModel from, ClassModel declaringClass, int modifier) {
        from.checkLevel(ClassModel.HIERARCHY);

        if (Modifier.isPublic(modifier)) {
            return true;
        }

        // If two inner classes are (transitively) inside the same outer class, such as A$B$C and A$D$E they can override methods
        // from one another, even if all methods are private. In the example, it's perfectly fine for private class A$D$E to
        // extend private class A$B$C and override a method in it.
        for (ClassModel curDecl = declaringClass; curDecl.hasOuterClass(); ) {
            curDecl = curDecl.getOuterClass();
            if (from.equals(curDecl)) {
                return true;
            }

            for (ClassModel curFrom = from; curFrom.hasOuterClass(); ) {
                curFrom = curFrom.getOuterClass();
                if (curDecl.equals(curFrom)) {
                    return true;
                }
            }
        }

        if (Modifier.isPrivate(modifier)) {
            return from.equals(declaringClass);
        }
        if (Modifier.isProtected(modifier)) {
            return canStoreClass(from, declaringClass);
        }
        // m is package
        return from.getJavaPackageName().equals(declaringClass.getJavaPackageName());
    }

    /**
     * Given an object of declared type C, returns the methods which could be called on an o.f() invocation.
     *
     * @param baseType The declared type C
     */
    public Set<MethodModel> resolveAbstractDispatch(ClassModel baseType, MethodModel m) {
        return resolveAbstractDispatch(baseType, m.makeRef());
    }

    /**
     * Given an object of declared type C, returns the methods which could be called on an o.f() invocation.
     *
     * @param baseType The declared type C
     */
    public Set<MethodModel> resolveAbstractDispatch(ClassModel baseType, SootMethodRef m) {
        HashSet<ClassModel> resolved = new HashSet<>();
        HashSet<MethodModel> ret = new HashSet<>();

        ArrayDeque<ClassModel> worklist = new ArrayDeque<ClassModel>() {
            @Override
            public boolean addAll(Collection<? extends ClassModel> c) {
                boolean b = false;
                for (ClassModel e : c) {
                    if (add(e)) {
                        b = true;
                    }
                }
                return b;
            }

            @Override
            public boolean add(ClassModel e) {
                if (resolved.contains(e) && classToSubclasses.get(e).isEmpty()) {
                    return false;
                }
                return super.add(e);
            }
        };
        worklist.add(baseType);
        while (true) {
            ClassModel concreteType = worklist.poll();
            if (concreteType == null) {
                break;
            }

            if (concreteType.isInterface()) {
                worklist.addAll(getAllImplementersOfInterface(concreteType));
                continue;
            } else {
                Collection<ClassModel> c = classToSubclasses.get(concreteType);
                if (c != null) {
                    worklist.addAll(c);
                }
            }

            if (!resolved.contains(concreteType)) {
                MethodModel resolvedMethod = resolveMethod(concreteType, m, false, resolved);
                if (resolvedMethod != null) {
                    ret.add(resolvedMethod);
                }
            }
        }

        return ret;
    }

    /**
     * Given an object of actual type C (o = new C()), returns the method which will be called on an o.f() invocation.
     *
     * @param baseType The actual type C
     */
    public MethodModel resolveConcreteDispatch(ClassModel baseType, MethodModel m) {
        return resolveConcreteDispatch(baseType, m.makeRef());
    }

    /**
     * Given an object of actual type C (o = new C()), returns the method which will be called on an o.f() invocation.
     *
     * @param baseType The actual type C
     */
    public MethodModel resolveConcreteDispatch(ClassModel baseType, SootMethodRef m) {
        baseType.checkLevel(ClassModel.HIERARCHY);
        if (baseType.isInterface()) {
            throw new RuntimeException("A concrete type cannot be an interface: " + baseType);
        }

        return resolveMethod(baseType, m, false);
    }

    /**
     * Conducts the actual dispatch by searching up the baseType's superclass hierarchy and interface hierarchy if the
     * sourcecode level is beyond Java 7 (due to default interface methods.) Given an object of actual type C (o = new C()),
     * returns the method which will be called on an o.f() invocation.
     *
     * <p>
     * If abstract methods are allowed, it will just resolve to the first method found according to javas method resolution
     * process: https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-5.html#jvms-5.4.3.3
     *
     * @param baseType The type C
     * @param m        The method f to resolve
     * @return The concrete method o.f() to call
     */
    public MethodModel resolveMethod(ClassModel baseType, MethodModel m, boolean allowAbstract) {
        return resolveMethod(baseType, m.makeRef(), allowAbstract);
    }

    /**
     * Conducts the actual dispatch by searching up the baseType's superclass hierarchy and interface hierarchy if the
     * sourcecode level is beyond Java 7 (due to default interface methods.) Given an object of actual type C (o = new C()),
     * returns the method which will be called on an o.f() invocation.
     *
     * <p>
     * If abstract methods are allowed, it will just resolve to the first method found according to javas method resolution
     * process: https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-5.html#jvms-5.4.3.3
     *
     * @param baseType The type C
     * @param m        The method f to resolve
     * @return The concrete method o.f() to call
     */
    public MethodModel resolveMethod(ClassModel baseType, SootMethodRef m, boolean allowAbstract) {
        return resolveMethod(baseType, m, allowAbstract, new HashSet<>());
    }

    /**
     * Conducts the actual dispatch by searching up the baseType's superclass hierarchy and interface hierarchy if the
     * sourcecode level is beyond Java 7 (due to default interface methods.) Given an object of actual type C (o = new C()),
     * returns the method which will be called on an o.f() invocation.
     *
     * <p>
     * *
     *
     * <p>
     * If abstract methods are allowed, it will just resolve to the first method found according to * javas method resolution
     * process: * https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-5.html#jvms-5.4.3.3
     *
     * @param baseType   The type C
     * @param m          The method f to resolve
     * @param ignoreList A set of classes that should be ignored during dispatch. This set will also be modified since every traversed
     *                   class/interface will be added. This is required for the abstract dispatch to not do additional resolving effort
     *                   by resolving the same classes multiple times.
     * @return The concrete method o.f() to call
     */
    private MethodModel resolveMethod(ClassModel baseType, SootMethodRef m, boolean allowAbstract, Set<ClassModel> ignoreList) {
        return resolveMethod(baseType, m.getDeclaringClass(), m.getName(), m.getParameterTypes(), m.getReturnType(),
                allowAbstract, ignoreList, m.getSubSignature());
    }

    /**
     * Conducts the actual dispatch by searching up the baseType's superclass hierarchy and interface hierarchy if the
     * sourcecode level is beyond Java 7 (due to default interface methods.) Given an object of actual type C (o = new C()),
     * returns the method which will be called on an o.f() invocation.
     *
     * <p>
     * If abstract methods are allowed, it will just resolve to the first method found according to javas method resolution
     * process: https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-5.html#jvms-5.4.3.3
     *
     * @param baseType       The type C
     * @param declaringClass declaring class of the method to resolve
     * @param name           Name of the method to resolve
     * @return The concrete method o.f() to call
     */
    public MethodModel resolveMethod(ClassModel baseType, ClassModel declaringClass, String name, List<Type> parameterTypes,
                                     Type returnType, boolean allowAbstract) {
        return resolveMethod(baseType, declaringClass, name, parameterTypes, returnType, allowAbstract, new HashSet<>(), null);
    }

    /**
     * Conducts the actual dispatch by searching up the baseType's superclass hierarchy and interface hierarchy if the
     * sourcecode level is beyond Java 7 (due to default interface methods.) Given an object of actual type C (o = new C()),
     * returns the method which will be called on an o.f() invocation.
     *
     * <p>
     * If abstract methods are allowed, it will just resolve to the first method found according to javas method resolution
     * process: https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-5.html#jvms-5.4.3.3
     *
     * @param baseType       The type C
     * @param declaringClass declaring class of the method to resolve
     * @param name           Name of the method to resolve
     * @param subsignature   The subsignature (can be null) to speed up the resolving process.
     * @return The concrete method o.f() to call
     */
    public MethodModel resolveMethod(ClassModel baseType, ClassModel declaringClass, String name, List<Type> parameterTypes,
                                     Type returnType, boolean allowAbstract, NumberedString subsignature) {
        return resolveMethod(baseType, declaringClass, name, parameterTypes, returnType, allowAbstract, new HashSet<>(),
                subsignature);
    }

    /**
     * Conducts the actual dispatch by searching up the baseType's superclass hierarchy and interface hierarchy if the
     * sourcecode level is beyond Java 7 (due to default interface methods.) Given an object of actual type C (o = new C()),
     * returns the method which will be called on an o.f() invocation.
     *
     * <p>
     * If abstract methods are allowed, it will just resolve to the first method found according to javas method resolution
     * process: https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-5.html#jvms-5.4.3.3
     *
     * @param baseType       The type C
     * @param declaringClass declaring class of the method to resolve
     * @param name           Name of the method to resolve
     * @param ignoreList     A set of classes that should be ignored during dispatch. This set will also be modified since every traversed
     *                       class/interface will be added. This is required for the abstract dispatch to not do additional resolving effort
     *                       by resolving the same classes multiple times.
     * @param subsignature   The subsignature (can be null) to speed up the resolving process.
     * @return The concrete method o.f() to call
     */
    private MethodModel resolveMethod(final ClassModel baseType, final ClassModel declaringClass, final String name,
                                      final List<Type> parameterTypes, final Type returnType, final boolean allowAbstract, final Set<ClassModel> ignoreList,
                                      NumberedString subsignature) {
        final NumberedString methodSignature;
        if (subsignature == null) {
            methodSignature
                    = Scene.v().getSubSigNumberer().findOrAdd(MethodModel.getSubSignature(name, parameterTypes, returnType));
        } else {
            methodSignature = subsignature;
        }

        {
            MethodModel resolvedMethod = typeToVtbl.get(baseType, methodSignature);
            if (resolvedMethod != null) {
                return resolvedMethod;
            }
        }

        // When there is no proper dispatch found, we simply return null to let the caller decide what to do
        MethodModel candidate = null;
        boolean calleeExist = declaringClass.getMethodUnsafe(subsignature) != null;
        for (ClassModel concreteType = baseType; concreteType != null && ignoreList.add(concreteType); ) {
            candidate = getSignaturePolymorphicMethod(concreteType, name, parameterTypes, returnType);
            if (candidate != null) {
                if (!calleeExist || isVisible(concreteType, declaringClass, candidate.getModifiers())) {
                    if (!allowAbstract && candidate.isAbstract()) {
                        candidate = null;
                        break;
                    }

                    if (!candidate.isAbstract()) {
                        typeToVtbl.put(baseType, methodSignature, candidate);
                    }
                    return candidate;
                }
            }

            concreteType = concreteType.getSuperclassUnsafe();
        }

        // for java > 7 we have to go through the interface hierarchy after the superclass hierarchy to
        // look for default methods:
        // https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-5.html#jvms-5.4.3.3
        if (isHandleDefaultMethods()) {
            // keep our own ignorelist here so we are not restricted to already hit suinterfaces when
            // determining the most specific super interface
            HashSet<ClassModel> interfaceIgnoreList = new HashSet<>();
            for (ClassModel concreteType = baseType; concreteType != null; ) {
                Queue<ClassModel> worklist = new ArrayDeque<>(concreteType.getInterfaceTypes());
                // we have to determine the "most specific super interface"
                while (!worklist.isEmpty()) {
                    ClassModel iFace = worklist.poll();

                    if (!interfaceIgnoreList.add(iFace)) {
                        continue;
                    }

                    MethodModel method = getSignaturePolymorphicMethod(iFace, name, parameterTypes, returnType);
                    if (method != null && isVisible(declaringClass, iFace, method.getModifiers())) {
                        if (!allowAbstract && method.isAbstract()) {
                            // abstract method cannot be dispatched
                        } else if (candidate == null || canStoreClass(method.getDeclaringClass(), candidate.getDeclaringClass())) {
                            // the found method is more specific than our current candidate
                            candidate = method;
                        }
                    } else {
                        // go up the interface hierarchy
                        worklist.addAll(iFace.getInterfaceTypes());
                    }
                }

                // we also have to search upwards the class hierarchy again to find the most specific
                // super interface
                concreteType = concreteType.getSuperclassUnsafe();
            }

            ignoreList.addAll(interfaceIgnoreList);
        }

        if (candidate != null) {
            typeToVtbl.put(baseType, methodSignature, candidate);
        }
        return candidate;
    }

    protected boolean isHandleDefaultMethods() {
        int version = Options.v().java_version();
        return version == 0 || version > 7;
    }

    /**
     * Returns the target for the given SpecialInvokeExpr.
     */
    public MethodModel resolveSpecialDispatch(MethodModel callee, MethodModel container) {
        /*
         * This is a bizarre condition! Hopefully the implementation is correct. See VM Spec, 2nd Edition, Chapter 6, in the
         * definition of invokespecial.
         */
        final ClassModel containerClass = container.getDeclaringClass();
        final ClassModel calleeClass = callee.getDeclaringClass();
        if (containerClass.getClassType() != calleeClass.getClassType() && canStoreType(containerClass.getClassType(), calleeClass.getClassType())
                && !MethodModel.constructorName.equals(callee.getName()) && !MethodModel.staticInitializerName.equals(callee.getName())
                // default interface methods are explicitly dispatched to the default
                // method with a specialinvoke instruction (i.e. do not dispatch to an
                // overwritten version of that method)
                && !calleeClass.isInterface()) {
            return resolveConcreteDispatch(containerClass, callee);
        } else {
            return callee;
        }
    }

    /**
     * Searches the given class for a method that is signature polymorphic according to the given facts, i.e., matches name and
     * parameter types and ensures that the return type is a an equal or subtype of the given method's subtype.
     *
     * @param concreteType
     * @return
     */
    private MethodModel getSignaturePolymorphicMethod(ClassModel concreteType, String name, List<Type> parameterTypes,
                                                      Type returnType) {
        if (concreteType == null) {
            throw new RuntimeException("The concreteType cannot not be null!");
        }

        MethodModel candidate = null;

        for (MethodModel method : concreteType.getMethodsByNameAndParamCount(name, parameterTypes.size())) {
            if (method.getParameterTypes().equals(parameterTypes) && canStoreType(method.getReturnType(), returnType)) {
                candidate = method;
                returnType = method.getReturnType();
            }
        }

        return candidate;
    }

    /**
     * Gets the direct subclasses of a given class. The class needs to be resolved at least at the HIERARCHY level.
     *
     * @param c the class
     * @return a collection (possibly empty) of the direct subclasses
     */
    public Collection<ClassModel> getSubclassesOf(ClassModel c) {
        c.checkLevel(ClassModel.HIERARCHY);
        Set<ClassModel> ret = classToSubclasses.get(c);
        return (ret == null) ? Collections.emptySet() : ret;
    }

    /**
     * Returns a list of types which can be used to store the given type
     *
     * @param nt the given type
     * @return the list of types which can be used to store the given type
     */
    public Iterable<Type> canStoreTypeList(final Type nt) {
        return new Iterable<Type>() {

            @Override
            public Iterator<Type> iterator() {
                Iterator<Type> it = Scene.v().getTypeNumberer().iterator();
                return new Iterator<Type>() {

                    Type crt = null;

                    @Override
                    public boolean hasNext() {
                        if (crt != null) {
                            return true;
                        }
                        Type c = null;
                        while (it.hasNext()) {
                            c = it.next();
                            if (TypeManager.isUnresolved(c)) {
                                continue;
                            }
                            if (canStoreType(nt, c)) {
                                crt = c;
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public Type next() {
                        Type old = crt;
                        crt = null;
                        hasNext();
                        return old;
                    }

                };
            }
        };
    }

}
