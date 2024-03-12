package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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
import byteback.analysis.model.MethodModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.jimple.spark.pag.AllocDotField;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis;
import soot.jimple.toolkits.callgraph.ConstantArrayAnalysis.ArrayTypes;
import soot.jimple.toolkits.callgraph.VirtualEdgesSummaries.DirectTarget;
import soot.jimple.toolkits.callgraph.VirtualEdgesSummaries.IndirectTarget;
import soot.jimple.toolkits.callgraph.VirtualEdgesSummaries.VirtualEdge;
import soot.jimple.toolkits.callgraph.VirtualEdgesSummaries.VirtualEdgeTarget;
import soot.jimple.toolkits.reflection.ReflectionTraceInfo;
import soot.options.CGOptions;
import soot.options.Options;
import soot.options.SparkOptions;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.util.HashMultiMap;
import soot.util.MultiMap;
import soot.util.NumberedString;
import soot.util.StringNumberer;
import soot.util.queue.ChunkedQueue;
import soot.util.queue.QueueReader;

import java.util.*;

/**
 * Models the call graph.
 *
 * @author Ondrej Lhotak
 */
public class OnFlyCallGraphBuilder {
    private static final Logger logger = LoggerFactory.getLogger(OnFlyCallGraphBuilder.class);

    // NOTE: this field must be static to avoid adding the transformation again if the call graph is rebuilt.
    static boolean registeredGuardsTransformation = false;

    private static final PrimType[] CHAR_NARROWINGS;
    private static final PrimType[] INT_NARROWINGS;
    private static final PrimType[] SHORT_NARROWINGS;
    private static final PrimType[] LONG_NARROWINGS;
    private static final ByteType[] BYTE_NARROWINGS;
    private static final PrimType[] FLOAT_NARROWINGS;
    private static final PrimType[] BOOLEAN_NARROWINGS;
    private static final PrimType[] DOUBLE_NARROWINGS;

    static {
        final CharType cT = CharType.v();
        final IntType iT = IntType.v();
        final ShortType sT = ShortType.v();
        final ByteType bT = ByteType.v();
        final LongType lT = LongType.v();
        final FloatType fT = FloatType.v();
        CHAR_NARROWINGS = new PrimType[]{cT};
        INT_NARROWINGS = new PrimType[]{iT, cT, sT, bT, sT};
        SHORT_NARROWINGS = new PrimType[]{sT, bT};
        LONG_NARROWINGS = new PrimType[]{lT, iT, cT, sT, bT, sT};
        BYTE_NARROWINGS = new ByteType[]{bT};
        FLOAT_NARROWINGS = new PrimType[]{fT, lT, iT, cT, sT, bT, sT};
        BOOLEAN_NARROWINGS = new PrimType[]{BooleanType.v()};
        DOUBLE_NARROWINGS = new PrimType[]{DoubleType.v(), fT, lT, iT, cT, sT, bT, sT};
    }

    protected final NumberedString sigFinalize;
    protected final NumberedString sigInit;
    protected final NumberedString sigForName;

    protected final RefType clRunnable = RefType.v("java.lang.Runnable");
    protected final RefType clAsyncTask = RefType.v("android.os.AsyncTask");
    protected final RefType clHandler = RefType.v("android.os.Handler");

    /**
     * context-insensitive stuff
     */
    private final CallGraph cicg = Scene.v().internalMakeCallGraph();

    // end type based reflection resolution
    protected final Map<Local, List<VirtualCallSite>> receiverToSites;
    protected final Map<MethodModel, List<Local>> methodToReceivers;
    protected final Map<MethodModel, List<Local>> methodToInvokeBases;
    protected final Map<MethodModel, List<Local>> methodToInvokeArgs;
    protected final Map<MethodModel, List<Local>> methodToStringConstants;
    protected final Map<Local, List<VirtualCallSite>> stringConstToSites;

    protected final HashSet<MethodModel> analyzedMethods = new HashSet<MethodModel>();
    protected final MultiMap<Local, InvokeCallSite> baseToInvokeSite = new HashMultiMap<>();
    protected final MultiMap<Local, InvokeCallSite> invokeArgsToInvokeSite = new HashMultiMap<>();
    protected final Map<Local, BitSet> invokeArgsToSize = new IdentityHashMap<>();
    protected final MultiMap<AllocDotField, Local> allocDotFieldToLocal = new HashMultiMap<>();
    protected final MultiMap<Local, Type> reachingArgTypes = new HashMultiMap<>();
    protected final MultiMap<Local, Type> reachingBaseTypes = new HashMultiMap<>();
    protected final ChunkedQueue<MethodModel> targetsQueue = new ChunkedQueue<MethodModel>();
    protected final QueueReader<MethodModel> targets = targetsQueue.reader();

    protected final ReflectionModel reflectionModel;
    protected final CGOptions options;
    protected boolean appOnly;

    /**
     * context-sensitive stuff
     */
    protected final ReachableMethods rm;
    protected final QueueReader<MethodOrMethodContext> worklist;
    protected final ContextManager cm;

    protected final VirtualEdgesSummaries virtualEdgeSummaries = initializeEdgeSummaries();

    protected NullnessAnalysis nullnessCache = null;
    protected ConstantArrayAnalysis arrayCache = null;
    protected MethodModel analysisKey = null;

    public OnFlyCallGraphBuilder(ContextManager cm, ReachableMethods rm, boolean appOnly) {
        final Scene sc = Scene.v();
        {
            final StringNumberer nmbr = sc.getSubSigNumberer();
            this.sigFinalize = nmbr.findOrAdd(JavaMethods.SIG_FINALIZE);
            this.sigInit = nmbr.findOrAdd(JavaMethods.SIG_INIT);
            this.sigForName = nmbr.findOrAdd(JavaMethods.SIG_INIT);
        }
        {
            this.receiverToSites = new HashMap<Local, List<VirtualCallSite>>();
            this.methodToReceivers = new HashMap<MethodModel, List<Local>>();
            this.methodToInvokeBases = new HashMap<MethodModel, List<Local>>();
            this.methodToInvokeArgs = new HashMap<MethodModel, List<Local>>();
            this.methodToStringConstants = new HashMap<MethodModel, List<Local>>();
            this.stringConstToSites = new HashMap<Local, List<VirtualCallSite>>();
        }

        this.cm = cm;
        this.rm = rm;
        this.worklist = rm.listener();
        this.options = new CGOptions(PhaseOptions.v().getPhaseOptions("cg"));
        if (!options.verbose()) {
            logger.debug("[Call Graph] For information on where the call graph may be incomplete,"
                    + " use the verbose option to the cg phase.");
        }

        if (options.reflection_log() == null || options.reflection_log().length() == 0) {
            if (options.types_for_invoke() && new SparkOptions(PhaseOptions.v().getPhaseOptions("cg.spark")).enabled()) {
                this.reflectionModel = new TypeBasedReflectionModel();
            } else {
                this.reflectionModel = new DefaultReflectionModel();
            }
        } else {
            this.reflectionModel = new TraceBasedReflectionModel();
        }
        this.appOnly = appOnly;
    }

    public OnFlyCallGraphBuilder(ContextManager cm, ReachableMethods rm) {
        this(cm, rm, false);
    }

    /**
     * Initializes the edge summaries that model callbacks in library classes. Custom implementations may override this method
     * to supply a specialized summary provider.
     *
     * @return A provider object for virtual edge summaries
     */
    protected VirtualEdgesSummaries initializeEdgeSummaries() {
        return new VirtualEdgesSummaries();
    }

    public ContextManager getContextManager() {
        return cm;
    }

    public Map<MethodModel, List<Local>> methodToReceivers() {
        return methodToReceivers;
    }

    public Map<MethodModel, List<Local>> methodToInvokeArgs() {
        return methodToInvokeArgs;
    }

    public Map<MethodModel, List<Local>> methodToInvokeBases() {
        return methodToInvokeBases;
    }

    public Map<MethodModel, List<Local>> methodToStringConstants() {
        return methodToStringConstants;
    }

    public void processReachables() {
        while (true) {
            if (!worklist.hasNext()) {
                rm.update();
                if (!worklist.hasNext()) {
                    break;
                }
            }
            MethodOrMethodContext momc = worklist.next();
            if (momc == null) {
                continue;
            }
            MethodModel m = momc.method();
            if (appOnly && !m.getDeclaringClass().isApplicationClass()) {
                continue;
            }
            if (analyzedMethods.add(m)) {
                processNewMethod(m);
            }
            processNewMethodContext(momc);
        }
    }

    public boolean wantTypes(Local receiver) {
        return receiverToSites.get(receiver) != null || baseToInvokeSite.get(receiver) != null;
    }

    public void addBaseType(Local base, Context context, Type ty) {
        assert (context == null);
        final Set<InvokeCallSite> invokeSites = baseToInvokeSite.get(base);
        if (invokeSites != null) {
            if (reachingBaseTypes.put(base, ty) && !invokeSites.isEmpty()) {
                resolveInvoke(invokeSites);
            }
        }
    }

    public void addInvokeArgType(Local argArray, Context context, Type t) {
        assert (context == null);
        final Set<InvokeCallSite> invokeSites = invokeArgsToInvokeSite.get(argArray);
        if (invokeSites != null) {
            if (reachingArgTypes.put(argArray, t)) {
                resolveInvoke(invokeSites);
            }
        }
    }

    public void setArgArrayNonDetSize(Local argArray, Context context) {
        assert (context == null);
        final Set<InvokeCallSite> invokeSites = invokeArgsToInvokeSite.get(argArray);
        if (invokeSites != null) {
            if (!invokeArgsToSize.containsKey(argArray)) {
                invokeArgsToSize.put(argArray, null);
                resolveInvoke(invokeSites);
            }
        }
    }

    public void addPossibleArgArraySize(Local argArray, int value, Context context) {
        assert (context == null);
        final Set<InvokeCallSite> invokeSites = invokeArgsToInvokeSite.get(argArray);
        if (invokeSites != null) {
            // non-det size
            BitSet sizeSet = invokeArgsToSize.get(argArray);
            if (sizeSet == null || !sizeSet.isEmpty()) {
                if (sizeSet == null) {
                    invokeArgsToSize.put(argArray, sizeSet = new BitSet());
                }
                if (!sizeSet.get(value)) {
                    sizeSet.set(value);
                    resolveInvoke(invokeSites);
                }
            }
        }
    }

    private static Set<RefLikeType> resolveToClasses(Set<Type> rawTypes) {
        Set<RefLikeType> toReturn = new HashSet<>();
        if (!rawTypes.isEmpty()) {
            final FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
            for (Type ty : rawTypes) {
                if (ty instanceof AnySubType anySubType) {
                  RefType base = anySubType.getBase();
                    Set<ClassModel> classRoots;
                    if (base.getSootClass().isInterface()) {
                        classRoots = fh.getAllImplementersOfInterface(base.getSootClass());
                    } else {
                        classRoots = Collections.singleton(base.getSootClass());
                    }
                    toReturn.addAll(getTransitiveSubClasses(classRoots));
                } else if (ty instanceof RefType) {
                    toReturn.add((RefType) ty);
                }
            }
        }
        return toReturn;
    }

    private static Collection<RefLikeType> getTransitiveSubClasses(Set<ClassModel> classRoots) {
        Set<RefLikeType> resolved = new HashSet<>();
        if (!classRoots.isEmpty()) {
            final FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
            for (LinkedList<ClassModel> worklist = new LinkedList<>(classRoots); !worklist.isEmpty(); ) {
                ClassModel cls = worklist.removeFirst();
                if (resolved.add(cls.getClassType())) {
                    worklist.addAll(fh.getSubclassesOf(cls));
                }
            }
        }
        return resolved;
    }

    private void resolveInvoke(Collection<InvokeCallSite> list) {
        for (InvokeCallSite ics : list) {
            Set<Type> s = reachingBaseTypes.get(ics.base());
            if (s == null || s.isEmpty()) {
                continue;
            }
            if (ics.reachingTypes() != null) {
                assert (ics.nullnessCode() != InvokeCallSite.MUST_BE_NULL);
                resolveStaticTypes(s, ics);
                continue;
            }
            boolean mustNotBeNull = ics.nullnessCode() == InvokeCallSite.MUST_NOT_BE_NULL;
            boolean mustBeNull = ics.nullnessCode() == InvokeCallSite.MUST_BE_NULL;
            // if the arg array may be null and we haven't seen a size or type
            // yet, then generate nullary methods
            if (mustBeNull || (ics.nullnessCode() == InvokeCallSite.MAY_BE_NULL
                    && (!invokeArgsToSize.containsKey(ics.argArray()) || !reachingArgTypes.containsKey(ics.argArray())))) {
                for (Type bType : resolveToClasses(s)) {
                    assert (bType instanceof RefType);
                    // do not handle array reflection
                    if (bType instanceof ArrayType) {
                        continue;
                    }
                    ClassModel baseClass = ((RefType) bType).getSootClass();
                    assert (!baseClass.isInterface());
                    for (Iterator<MethodModel> mIt = getPublicNullaryMethodIterator(baseClass); mIt.hasNext(); ) {
                        MethodModel sm = mIt.next();
                        cm.addVirtualEdge(ics.getContainer(), ics.getStmt(), sm, Kind.REFL_INVOKE, null);
                    }
                }
            } else {
                /*
                 * In this branch, either the invoke arg must not be null, or may be null and we have size and type information.
                 * Invert the above condition: ~mustBeNull && (~mayBeNull || (has-size && has-type)) => (~mustBeNull && ~mayBeNull)
                 * || (~mustBeNull && has-size && has-type) => mustNotBeNull || (~mustBeNull && has-types && has-size) =>
                 * mustNotBeNull || (mayBeNull && has-types && has-size)
                 */
                Set<Type> reachingTypes = reachingArgTypes.get(ics.argArray());
                /*
                 * the path condition allows must-not-be null without type and size info. Do nothing in this case. THIS IS UNSOUND if
                 * default null values in an argument array are used.
                 */
                if (reachingTypes == null || !invokeArgsToSize.containsKey(ics.argArray())) {
                    assert (ics.nullnessCode() == InvokeCallSite.MUST_NOT_BE_NULL) : ics;
                    return;
                }
                BitSet methodSizes = invokeArgsToSize.get(ics.argArray());
                for (Type bType : resolveToClasses(s)) {
                    assert (bType instanceof RefLikeType);
                    // we do not handle static methods or array reflection
                    if (!(bType instanceof NullType) && !(bType instanceof ArrayType)) {
                        ClassModel baseClass = ((RefType) bType).getSootClass();
                        Iterator<MethodModel> mIt = getPublicMethodIterator(baseClass, reachingTypes, methodSizes, mustNotBeNull);
                        while (mIt.hasNext()) {
                            MethodModel sm = mIt.next();
                            cm.addVirtualEdge(ics.container(), ics.stmt(), sm, Kind.REFL_INVOKE, null);
                        }
                    }
                }
            }
        }
    }

    /* End of public methods. */

    private void resolveStaticTypes(Set<Type> s, InvokeCallSite ics) {
        ArrayTypes at = ics.reachingTypes();
        for (Type bType : resolveToClasses(s)) {
            // do not handle array reflection
            if (bType instanceof ArrayType) {
                continue;
            }
            ClassModel baseClass = ((RefType) bType).getSootClass();
            for (Iterator<MethodModel> mIt = getPublicMethodIterator(baseClass, at); mIt.hasNext(); ) {
                MethodModel sm = mIt.next();
                cm.addVirtualEdge(ics.getContainer(), ics.getStmt(), sm, Kind.REFL_INVOKE, null);
            }
        }
    }

    private static Iterator<MethodModel> getPublicMethodIterator(ClassModel baseClass, final ArrayTypes at) {
        return new AbstractMethodIterator(baseClass) {
            @Override
            protected boolean acceptMethod(MethodModel m) {
                if (!at.possibleSizes.contains(m.getParameterCount())) {
                    return false;
                }
                for (int i = 0; i < m.getParameterCount(); i++) {
                    Set<Type> possibleType = at.possibleTypes[i];
                    if (possibleType.isEmpty()) {
                        continue;
                    }
                    if (!isReflectionCompatible(m.getParameterType(i), possibleType)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private static PrimType[] narrowings(PrimType f) {
        if (f instanceof IntType) {
            return INT_NARROWINGS;
        } else if (f instanceof ShortType) {
            return SHORT_NARROWINGS;
        } else if (f instanceof LongType) {
            return LONG_NARROWINGS;
        } else if (f instanceof ByteType) {
            return BYTE_NARROWINGS;
        } else if (f instanceof FloatType) {
            return FLOAT_NARROWINGS;
        } else if (f instanceof BooleanType) {
            return BOOLEAN_NARROWINGS;
        } else if (f instanceof DoubleType) {
            return DOUBLE_NARROWINGS;
        } else if (f instanceof CharType) {
            return CHAR_NARROWINGS;
        } else {
            throw new RuntimeException("Unexpected primitive type: " + f);
        }
    }

    private static boolean isReflectionCompatible(Type paramType, Set<Type> reachingTypes) {
        /*
         * attempting to pass in a null will match any type (although attempting to pass it to a primitive arg will give an NPE)
         */
        if (reachingTypes.contains(NullType.v())) {
            return true;
        }
        if (paramType instanceof RefLikeType) {
            final FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
            for (Type rType : reachingTypes) {
                if (fh.canStoreType(rType, paramType)) {
                    return true;
                }
            }
            return false;
        } else if (paramType instanceof PrimType) {
            /*
             * It appears, java reflection allows for unboxing followed by widening, so if there is a wrapper type that whose
             * corresponding primitive type can be widened into the expected primitive type, we're set
             */
            for (PrimType narrowings : narrowings((PrimType) paramType)) {
                if (reachingTypes.contains(narrowings.boxedType())) {
                    return true;
                }
            }
            return false;
        } else {
            // impossible?
            return false;
        }
    }

    private static Iterator<MethodModel> getPublicMethodIterator(final ClassModel baseClass, final Set<Type> reachingTypes,
                                                                 final BitSet methodSizes, final boolean mustNotBeNull) {
        if (baseClass.isPhantom()) {
            return Collections.emptyIterator();
        }
        return new AbstractMethodIterator(baseClass) {
            @Override
            protected boolean acceptMethod(MethodModel n) {
                if (methodSizes != null) {
                    // if the arg array can be null we have to still allow for nullary methods
                    int nParams = n.getParameterCount();
                    boolean compatibleSize = methodSizes.get(nParams) || (!mustNotBeNull && nParams == 0);
                    if (!compatibleSize) {
                        return false;
                    }
                }
                for (Type pTy : n.getParameterTypes()) {
                    if (!isReflectionCompatible(pTy, reachingTypes)) {
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private static Iterator<MethodModel> getPublicNullaryMethodIterator(final ClassModel baseClass) {
        if (baseClass.isPhantom()) {
            return Collections.emptyIterator();
        }
        return new AbstractMethodIterator(baseClass) {
            @Override
            protected boolean acceptMethod(MethodModel n) {
                return n.getParameterCount() == 0;
            }
        };
    }

    public void addType(Local receiver, Context srcContext, Type type, Context typeContext) {
        final List<VirtualCallSite> rcvrToCallSites = receiverToSites.get(receiver);
        if (rcvrToCallSites != null) {
            final VirtualCalls virtualCalls = VirtualCalls.v();
            final Scene sc = Scene.v();
            final FastHierarchy fh = sc.getOrMakeFastHierarchy();
            for (final VirtualCallSite site : rcvrToCallSites) {
                if (skipSite(site, fh, type)) {
                    continue;
                }

                final InstanceInvokeExpr iie = site.iie();
                if (iie instanceof SpecialInvokeExpr && !Kind.isFake(site.kind())) {
                    MethodModel target = virtualCalls.resolveSpecial(iie.getMethodRef(), site.getContainer(), appOnly);
                    // if the call target resides in a phantom class then "target" will be null;
                    // simply do not add the target in that case
                    if (target != null) {
                        targetsQueue.add(target);
                    }
                } else {
                    SootMethodRef ref = null;
                    Type receiverType = receiver.getType();

                    // Fake edges map to a different method signature, e.g., from execute(a) to a.run()
                    if (receiverType instanceof RefType) {
                        ClassModel receiverClass = ((RefType) receiverType).getSootClass();

                        MethodSubSignature subsig = site.subSig();
                        ref = sc.makeMethodRef(receiverClass, subsig.methodName, subsig.parameterTypes, subsig.getReturnType(),
                                Kind.isStatic(site.kind()));
                    } else {
                        ref = site.getStmt().getInvokeExpr().getMethodRef();
                    }

                    if (ref != null) {
                        virtualCalls.resolve(type, receiver.getType(), ref, site.getContainer(), targetsQueue, appOnly);
                        if (!targets.hasNext() && options.resolve_all_abstract_invokes()) {
                            /*
                             * In the situation where we find nothing to resolve an invoke to in the first call, this might be because the
                             * type for the invoking object is a abstract class and the method is declared in a parent class. In this
                             * situation, when the abstract class has no classes that extend it in the scene, resolve would not find any
                             * targets for the invoke, even if the parent contained a possible target.
                             *
                             * This may have been by design since without a concrete class, we have no idea if the method in the parent
                             * class is overridden. However, the same could be said for any non private method in the abstract class (and
                             * these all resolve fine inside the abstract class even though there are no sub classes of the abstract
                             * class). This makes this situation a corner case.
                             *
                             * Where as, it used to not resolve any targets in this situation, I want to at least resolve the method in the
                             * parent class if there is one (as this is technically a possibility and the only information we have).
                             */
                            virtualCalls.resolveSuperType(type, receiver.getType(), iie.getMethodRef(), targetsQueue, appOnly);
                        }
                    }
                }
                while (targets.hasNext()) {
                    MethodModel target = targets.next();
                    cm.addVirtualEdge(MethodContext.v(site.getContainer(), srcContext), site.getStmt(), target, site.kind(),
                            typeContext);
                }
            }
        }
        if (baseToInvokeSite.get(receiver) != null) {
            addBaseType(receiver, srcContext, type);
        }
    }

    protected boolean skipSite(VirtualCallSite site, FastHierarchy fh, Type type) {
        Kind k = site.kind();
        if (k == Kind.THREAD) {
            return !fh.canStoreType(type, clRunnable);
        } else if (k == Kind.EXECUTOR) {
            return !fh.canStoreType(type, clRunnable);
        } else if (k == Kind.ASYNCTASK) {
            return !fh.canStoreType(type, clAsyncTask);
        } else if (k == Kind.HANDLER) {
            return !fh.canStoreType(type, clHandler);
        } else {
            return false;
        }
    }

    public boolean wantStringConstants(Local stringConst) {
        return stringConstToSites.get(stringConst) != null;
    }

    public void addStringConstant(Local l, Context srcContext, String constant) {
        if (constant != null) {
            final Scene sc = Scene.v();
            for (Iterator<VirtualCallSite> siteIt = stringConstToSites.get(l).iterator(); siteIt.hasNext(); ) {
                final VirtualCallSite site = siteIt.next();
                final int constLen = constant.length();
                if (constLen > 0 && constant.charAt(0) == '[') {
                    if (constLen > 2 && constant.charAt(1) == 'L' && constant.charAt(constLen - 1) == ';') {
                        constant = constant.substring(2, constLen - 1);
                    } else {
                        continue;
                    }
                }
                if (sc.containsClass(constant)) {
                    ClassModel sootcls = sc.getSootClass(constant);
                    if (!sootcls.isApplicationClass() && !sootcls.isPhantom()) {
                        sootcls.setLibraryClass();
                    }
                    for (MethodModel clinit : EntryPoints.v().clinitsOf(sootcls)) {
                        cm.addStaticEdge(MethodContext.v(site.getContainer(), srcContext), site.getStmt(), clinit, Kind.CLINIT);
                    }
                } else if (options.verbose()) {
                    logger.warn("Class " + constant + " is a dynamic class and was not specified as such; graph will be incomplete!");
                }
            }
        } else if (options.verbose()) {
            for (Iterator<VirtualCallSite> siteIt = stringConstToSites.get(l).iterator(); siteIt.hasNext(); ) {
                final VirtualCallSite site = siteIt.next();
                logger.warn("Method " + site.getContainer() + " is reachable, and calls Class.forName on a non-constant"
                        + " String; graph will be incomplete! Use safe-forname option for a conservative result.");
            }
        }
    }

    public boolean wantArrayField(AllocDotField df) {
        return allocDotFieldToLocal.containsKey(df);
    }

    public void addInvokeArgType(AllocDotField df, Context context, Type type) {
        if (allocDotFieldToLocal.containsKey(df)) {
            for (Local l : allocDotFieldToLocal.get(df)) {
                addInvokeArgType(l, context, type);
            }
        }
    }

    public boolean wantInvokeArg(Local receiver) {
        return invokeArgsToInvokeSite.containsKey(receiver);
    }

    public void addInvokeArgDotField(Local receiver, AllocDotField dot) {
        allocDotFieldToLocal.put(dot, receiver);
    }

    /*
     * How type based reflection resolution works:
     *
     * In general, for each call to invoke(), we record the local of the receiver argument and the argument array. Whenever a
     * new type is added to the points to set of the receiver argument we add that type to the reachingBaseTypes and try to
     * resolve the reflective method call (see addType, addBaseType, and updatedNode() in OnFlyCallGraph).
     *
     * For added precision, we also record the second argument to invoke. If it is always null, this means the invoke() call
     * resolves only to nullary methods.
     *
     * When the second argument is a variable that must not be null we can narrow down the called method based on the possible
     * sizes of the argument array and the types it contains. Whenever a new allocation reaches this variable we record the
     * possible size of the array (by looking at the allocation site) and the possible types stored in the array (see
     * updatedNode in OnFlyCallGraph in the branch wantInvokeArg()). If the size of the array isn't statically known, the
     * analysis considers methods of all possible arities. In addition, we track the PAG node corresponding to the array
     * contents. If a new type reaches this node, we update the possible argument types. (see propagate() in PropWorklist and
     * the visitor, and updatedFieldRef in OnFlyCallGraph).
     *
     * For details on the method resolution process, see resolveInvoke()
     *
     * Finally, for cases like o.invoke(b, foo, bar, baz); it is very easy to statically determine precisely which types are in
     * which argument positions. This is computed using the ConstantArrayAnalysis and are resolved using resolveStaticTypes().
     */
    private void addInvokeCallSite(Stmt s, MethodModel container, InstanceInvokeExpr d) {
        Local l = (Local) d.getArg(0);
        Value argArray = d.getArg(1);
        InvokeCallSite ics;
        if (argArray instanceof NullConstant) {
            ics = new InvokeCallSite(s, container, d, l);
        } else {
            if (analysisKey != container) {
                ExceptionalUnitGraph graph = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(container.getActiveBody());
                nullnessCache = new NullnessAnalysis(graph);
                arrayCache = new ConstantArrayAnalysis(graph, container.getActiveBody());
                analysisKey = container;
            }
            Local argLocal = (Local) argArray;
            int nullnessCode;
            if (nullnessCache.isAlwaysNonNullBefore(s, argLocal)) {
                nullnessCode = InvokeCallSite.MUST_NOT_BE_NULL;
            } else if (nullnessCache.isAlwaysNullBefore(s, argLocal)) {
                nullnessCode = InvokeCallSite.MUST_BE_NULL;
            } else {
                nullnessCode = InvokeCallSite.MAY_BE_NULL;
            }
            if (nullnessCode != InvokeCallSite.MUST_BE_NULL && arrayCache.isConstantBefore(s, argLocal)) {
                ArrayTypes reachingArgTypes = arrayCache.getArrayTypesBefore(s, argLocal);
                if (nullnessCode == InvokeCallSite.MAY_BE_NULL) {
                    reachingArgTypes.possibleSizes.add(0);
                }
                ics = new InvokeCallSite(s, container, d, l, reachingArgTypes, nullnessCode);
            } else {
                ics = new InvokeCallSite(s, container, d, l, argLocal, nullnessCode);
                invokeArgsToInvokeSite.put(argLocal, ics);
            }
        }
        baseToInvokeSite.put(l, ics);
    }

    private void addVirtualCallSite(Stmt s, MethodModel m, Local receiver, InstanceInvokeExpr iie, MethodSubSignature subSig,
                                    Kind kind) {
        List<VirtualCallSite> sites = receiverToSites.get(receiver);
        if (sites == null) {
            receiverToSites.put(receiver, sites = new ArrayList<VirtualCallSite>());
            List<Local> receivers = methodToReceivers.get(m);
            if (receivers == null) {
                methodToReceivers.put(m, receivers = new ArrayList<Local>());
            }
            receivers.add(receiver);
        }
        sites.add(new VirtualCallSite(s, m, iie, subSig, kind));
    }

    protected void processNewMethod(MethodModel m) {
        if (m.isConcrete()) {
            Body b = m.retrieveActiveBody();
            getImplicitTargets(m);
            findReceivers(m, b);
        }
    }

    protected void findReceivers(MethodModel m, Body b) {
        for (final Unit u : b.getUnits()) {
            final Stmt s = (Stmt) u;
            if (s.containsInvokeExpr()) {
                InvokeExpr ie = s.getInvokeExpr();
                if (ie instanceof InstanceInvokeExpr iie) {
                  Local receiver = (Local) iie.getBase();
                    MethodSubSignature subSig = new MethodSubSignature(iie.getMethodRef());
                    addVirtualCallSite(s, m, receiver, iie, new MethodSubSignature(iie.getMethodRef()), Edge.ieToKind(iie));

                    VirtualEdge virtualEdge = virtualEdgeSummaries.getVirtualEdgesMatchingSubSig(subSig);
                    if (virtualEdge != null) {
                        for (VirtualEdgeTarget t : virtualEdge.targets) {
                            processVirtualEdgeSummary(m, s, receiver, t, virtualEdge.edgeType);
                        }
                    }
                } else if (ie instanceof DynamicInvokeExpr) {
                    if (options.verbose()) {
                        logger.warn("InvokeDynamic to " + ie + " not resolved during call-graph construction.");
                    }
                } else {
                    MethodModel tgt = ie.getMethod();
                    if (tgt != null) {
                        addEdge(m, s, tgt);
                        String signature = tgt.getSignature();
                        VirtualEdge virtualEdge = virtualEdgeSummaries.getVirtualEdgesMatchingFunction(signature);
                        if (virtualEdge != null) {
                            for (VirtualEdgeTarget t : virtualEdge.targets) {
                                if (t instanceof DirectTarget directTarget) {
                                  if (t.isBase()) {
                                        // this should not happen
                                    } else {
                                        Value runnable = ie.getArg(t.argIndex);
                                        if (runnable instanceof Local) {
                                            addVirtualCallSite(s, m, (Local) runnable, null, directTarget.targetMethod, Kind.GENERIC_FAKE);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (!Options.v().ignore_resolution_errors()) {
                        throw new InternalError(
                                "Unresolved target " + ie.getMethod() + ". Resolution error should have occured earlier.");
                    }
                }
            }
        }
    }

    protected void processVirtualEdgeSummary(MethodModel m, final Stmt s, Local receiver, VirtualEdgeTarget target,
                                             Kind edgeType) {
        processVirtualEdgeSummary(m, s, s, receiver, target, edgeType);
    }

    private Local getLocalForTarget(InvokeExpr ie, VirtualEdgeTarget target) {
        if (target.isBase() && ie instanceof InstanceInvokeExpr) {
            return (Local) ((InstanceInvokeExpr) ie).getBase();
        }

        int index = target.getArgIndex();
        if (index < ie.getArgCount()) {
            Value arg = ie.getArg(index);
            if (arg instanceof Local) {
                return (Local) arg;
            }
        }

        return null;
    }

    /**
     * Returns all values that should be mapped to this in the edge target.
     **/
    public Set<Local> getReceiversOfVirtualEdge(VirtualEdgeTarget edgeTarget, InvokeExpr invokeExpr) {
        if (edgeTarget instanceof IndirectTarget indirectTarget) {
          // Recursion case: We have an indirect target, which leads us to the statement where the local,
            // that gets $this inside the callee, resides.

            // First find the receiver of another call
            Local l = getLocalForTarget(invokeExpr, edgeTarget);
            if (l == null) {
                return Collections.emptySet();
            }

            List<VirtualCallSite> sites = receiverToSites.get(l);
            if (sites == null) {
                return Collections.emptySet();
            }
            Set<Local> results = new HashSet<>();
            MethodSubSignature methodName = edgeTarget.getTargetMethod();
            // Lookup all call sites on the receiver to find the indirect target method
            for (VirtualCallSite site : sites) {
                if (methodName.equals(site.subSig())) {
                    for (VirtualEdgeTarget subTargets : indirectTarget.getTargets()) {
                        // We have found the indirect target, recursively go down till we have a direct target,
                        // where we can get the local that finally gets converted to $this inside the callee.
                        results.addAll(getReceiversOfVirtualEdge(subTargets, site.iie()));
                        // We might have multiple calls of the same method on the receiver (e.g. if else)
                        // as well as multiple sub-targets, thus, we can't break here.
                    }
                }
            }
            return results;
        }

        assert edgeTarget instanceof DirectTarget;
        // Base case: Lookup the value based on the index referenced by the VirtualEdgeTarget.
        // That local represents the $this local inside the callee.
        Local l = getLocalForTarget(invokeExpr, edgeTarget);
        return l == null ? Collections.emptySet() : Collections.singleton(l);
    }

    protected void processVirtualEdgeSummary(MethodModel callSiteMethod, Stmt callSite, final Stmt curStmt, Local receiver,
                                             VirtualEdgeTarget target, Kind edgeType) {
        // Get the target object referenced by this edge summary
        InvokeExpr ie = curStmt.getInvokeExpr();
        Local targetLocal = getLocalForTarget(ie, target);
        if (targetLocal == null) {
            return;
        }

        if (target instanceof DirectTarget directTarget) {
            // A direct target means that we need to build an edge from the call site to a method on the current base object or a
            // parameter argument
          addVirtualCallSite(callSite, callSiteMethod, targetLocal, (InstanceInvokeExpr) ie, directTarget.targetMethod,
                    edgeType);
        } else if (target instanceof IndirectTarget w) {
            // For an indirect target, we need to find out where the base object or a specific parameter argument was
            // constructed. We then either have a direct target on that statement, or again an indirect one for searching further
            // up in the code.

          // addVirtualCallSite() may change receiverToSites, which may lead to a ConcurrentModificationException
            // I'm not entirely sure whether we ought to deal with the new call sites that are being added, instead of
            // just working on a snapshot, though.

            List<VirtualCallSite> indirectSites = receiverToSites.get(targetLocal);
            if (indirectSites != null) {
                for (final VirtualCallSite site : new ArrayList<>(indirectSites)) {
                    if (w.getTargetMethod().equals(site.subSig())) {
                        for (VirtualEdgeTarget siteTarget : w.getTargets()) {
                            Stmt siteStmt = site.getStmt();
                            if (siteStmt.containsInvokeExpr()) {
                                processVirtualEdgeSummary(callSiteMethod, callSite, siteStmt, receiver, siteTarget, edgeType);
                            }
                        }
                    }
                }
            }
        }
    }

    private void getImplicitTargets(MethodModel source) {
        final ClassModel scl = source.getDeclaringClass();
        if (!source.isConcrete()) {
            return;
        }
        if (source.getSubSignature().contains("<init>")) {
            handleInit(source, scl);
        }
        for (Unit u : source.retrieveActiveBody().getUnits()) {
            final Stmt s = (Stmt) u;
            if (s.containsInvokeExpr()) {
                InvokeExpr ie = s.getInvokeExpr();
                SootMethodRef methodRef = ie.getMethodRef();
                switch (methodRef.getDeclaringClass().getName()) {
                    case "java.lang.reflect.Method":
                        if ("java.lang.Object invoke(java.lang.Object,java.lang.Object[])"
                                .equals(methodRef.getSubSignature().getString())) {
                            reflectionModel.methodInvoke(source, s);
                        }
                        break;
                    case "java.lang.Class":
                        if ("java.lang.Object newInstance()".equals(methodRef.getSubSignature().getString())) {
                            reflectionModel.classNewInstance(source, s);
                        }
                        break;
                    case "java.lang.reflect.Constructor":
                        if ("java.lang.Object newInstance(java.lang.Object[])".equals(methodRef.getSubSignature().getString())) {
                            reflectionModel.contructorNewInstance(source, s);
                        }
                        break;
                }
                if (methodRef.getSubSignature() == sigForName) {
                    reflectionModel.classForName(source, s);
                }
                if (ie instanceof StaticInvokeExpr) {
                    ClassModel cl = ie.getMethodRef().getDeclaringClass();
                    for (MethodModel clinit : EntryPoints.v().clinitsOf(cl)) {
                        addEdge(source, s, clinit, Kind.CLINIT);
                    }
                }
            }
            if (s.containsFieldRef()) {
                FieldRef fr = s.getFieldRef();
                if (fr instanceof StaticFieldRef) {
                    ClassModel cl = fr.getFieldRef().declaringClass();
                    for (MethodModel clinit : EntryPoints.v().clinitsOf(cl)) {
                        addEdge(source, s, clinit, Kind.CLINIT);
                    }
                }
            }
            if (s instanceof AssignStmt) {
                Value rhs = ((AssignStmt) s).getRightOp();
                if (rhs instanceof NewExpr r) {
                  ClassModel cl = r.getBaseType().getSootClass();
                    for (MethodModel clinit : EntryPoints.v().clinitsOf(cl)) {
                        addEdge(source, s, clinit, Kind.CLINIT);
                    }
                } else if (rhs instanceof NewArrayExpr || rhs instanceof NewMultiArrayExpr) {
                    Type t = rhs.getType();
                    if (t instanceof ArrayType) {
                        t = ((ArrayType) t).baseType;
                    }
                    if (t instanceof RefType) {
                        ClassModel cl = ((RefType) t).getSootClass();
                        for (MethodModel clinit : EntryPoints.v().clinitsOf(cl)) {
                            addEdge(source, s, clinit, Kind.CLINIT);
                        }
                    }
                }
            }
        }
    }

    protected void processNewMethodContext(MethodOrMethodContext momc) {
        MethodModel m = momc.method();
        for (Iterator<Edge> it = cicg.edgesOutOf(m); it.hasNext(); ) {
            Edge e = it.next();
            cm.addStaticEdge(momc, e.srcUnit(), e.tgt(), e.kind());
        }
    }

    private void handleInit(MethodModel source, final ClassModel scl) {
        addEdge(source, null, scl, sigFinalize, Kind.FINALIZE);
    }

    private void constantForName(final String cls, MethodModel src, Stmt srcUnit) {
        final int clsLen = cls.length();
        if (clsLen > 0 && cls.charAt(0) == '[') {
            if (clsLen > 2 && cls.charAt(1) == 'L' && cls.charAt(clsLen - 1) == ';') {
                constantForName(cls.substring(2, clsLen - 1), src, srcUnit);
            }
        } else {
            final Scene sc = Scene.v();
            if (sc.containsClass(cls)) {
                ClassModel sootcls = sc.getSootClass(cls);
                if (!sootcls.isPhantomClass()) {
                    if (!sootcls.isApplicationClass()) {
                        sootcls.setLibraryClass();
                    }
                    for (MethodModel clinit : EntryPoints.v().clinitsOf(sootcls)) {
                        addEdge(src, srcUnit, clinit, Kind.CLINIT);
                    }
                }
            } else if (options.verbose()) {
                logger.warn("Class " + cls + " is a dynamic class and was not specified as such; graph will be incomplete!");
            }
        }
    }

    private void addEdge(MethodModel src, Stmt stmt, MethodModel tgt, Kind kind) {
        if (src.equals(tgt) && src.isStaticInitializer()) {
            return;
        }
        cicg.addEdge(new Edge(src, stmt, tgt, kind));
    }

    private void addEdge(MethodModel src, Stmt stmt, ClassModel cls, NumberedString methodSubSig, Kind kind) {
        MethodModel sm = cls.getMethodUnsafe(methodSubSig);
        if (sm != null) {
            addEdge(src, stmt, sm, kind);
        }
    }

    private void addEdge(MethodModel src, Stmt stmt, MethodModel tgt) {
        InvokeExpr ie = stmt.getInvokeExpr();
        addEdge(src, stmt, tgt, Edge.ieToKind(ie));
    }

    public class DefaultReflectionModel implements ReflectionModel {

        protected final CGOptions options = new CGOptions(PhaseOptions.v().getPhaseOptions("cg"));

        protected final HashSet<MethodModel> warnedAlready = new HashSet<MethodModel>();

        @Override
        public void classForName(MethodModel source, Stmt s) {
            List<Local> stringConstants = methodToStringConstants.get(source);
            if (stringConstants == null) {
                methodToStringConstants.put(source, stringConstants = new ArrayList<Local>());
            }
            Value className = s.getInvokeExpr().getArg(0);
            if (className instanceof StringConstant) {
                String cls = ((StringConstant) className).value;
                constantForName(cls, source, s);
            } else if (className instanceof Local constant) {
              if (options.safe_forname()) {
                    for (MethodModel tgt : EntryPoints.v().clinits()) {
                        addEdge(source, s, tgt, Kind.CLINIT);
                    }
                } else {
                    final EntryPoints ep = EntryPoints.v();
                    for (ClassModel cls : Scene.v().dynamicClasses()) {
                        for (MethodModel clinit : ep.clinitsOf(cls)) {
                            addEdge(source, s, clinit, Kind.CLINIT);
                        }
                    }
                    VirtualCallSite site = new VirtualCallSite(s, source, null, null, Kind.CLINIT);
                    List<VirtualCallSite> sites = stringConstToSites.get(constant);
                    if (sites == null) {
                        stringConstToSites.put(constant, sites = new ArrayList<VirtualCallSite>());
                        stringConstants.add(constant);
                    }
                    sites.add(site);
                }
            }
        }

        @Override
        public void classNewInstance(MethodModel source, Stmt s) {
            if (options.safe_newinstance()) {
                for (MethodModel tgt : EntryPoints.v().inits()) {
                    addEdge(source, s, tgt, Kind.NEWINSTANCE);
                }
            } else {
                for (ClassModel cls : Scene.v().dynamicClasses()) {
                    MethodModel sm = cls.getMethodUnsafe(sigInit);
                    if (sm != null) {
                        addEdge(source, s, sm, Kind.NEWINSTANCE);
                    }
                }

                if (options.verbose()) {
                    logger.warn("Method " + source + " is reachable, and calls Class.newInstance; graph will be incomplete!"
                            + " Use safe-newinstance option for a conservative result.");
                }
            }
        }

        @Override
        public void contructorNewInstance(MethodModel source, Stmt s) {
            if (options.safe_newinstance()) {
                for (MethodModel tgt : EntryPoints.v().allInits()) {
                    addEdge(source, s, tgt, Kind.NEWINSTANCE);
                }
            } else {
                for (ClassModel cls : Scene.v().dynamicClasses()) {
                    for (MethodModel m : cls.getMethodModels()) {
                        if ("<init>".equals(m.getName())) {
                            addEdge(source, s, m, Kind.NEWINSTANCE);
                        }
                    }
                }
                if (options.verbose()) {
                    logger.warn("Method " + source + " is reachable, and calls Constructor.newInstance; graph will be incomplete!"
                            + " Use safe-newinstance option for a conservative result.");
                }
            }
        }

        @Override
        public void methodInvoke(MethodModel container, Stmt invokeStmt) {
            if (!warnedAlready(container)) {
                if (options.verbose()) {
                    logger.warn("Call to java.lang.reflect.Method: invoke() from " + container + "; graph will be incomplete!");
                }
                markWarned(container);
            }
        }

        private void markWarned(MethodModel m) {
            warnedAlready.add(m);
        }

        private boolean warnedAlready(MethodModel m) {
            return warnedAlready.contains(m);
        }
    }

    public class TypeBasedReflectionModel extends DefaultReflectionModel {
        @Override
        public void methodInvoke(MethodModel container, Stmt invokeStmt) {
            if (container.getDeclaringClass().isJavaLibraryClass()) {
                super.methodInvoke(container, invokeStmt);
                return;
            }
            InstanceInvokeExpr d = (InstanceInvokeExpr) invokeStmt.getInvokeExpr();
            Value base = d.getArg(0);
            // TODO no support for statics at the moment

            // SA: Better just fall back to degraded functionality than fail altogether
            if (!(base instanceof Local)) {
                super.methodInvoke(container, invokeStmt);
                return;
            }
            addInvokeCallSite(invokeStmt, container, d);
        }
    }

    public class TraceBasedReflectionModel implements ReflectionModel {

        protected final Set<Guard> guards;
        protected final ReflectionTraceInfo reflectionInfo;

        private TraceBasedReflectionModel() {
            String logFile = options.reflection_log();
            if (logFile == null) {
                throw new InternalError("Trace based refection model enabled but no trace file given!?");
            }

            this.reflectionInfo = new ReflectionTraceInfo(logFile);
            this.guards = new HashSet<Guard>();
        }

        /**
         * Adds an edge to all class initializers of all possible receivers of Class.forName() calls within source.
         */
        @Override
        public void classForName(MethodModel container, Stmt forNameInvokeStmt) {
            Set<String> classNames = reflectionInfo.classForNameClassNames(container);
            if (classNames == null || classNames.isEmpty()) {
                registerGuard(container, forNameInvokeStmt,
                        "Class.forName() call site; Soot did not expect this site to be reached");
            } else {
                for (String clsName : classNames) {
                    constantForName(clsName, container, forNameInvokeStmt);
                }
            }
        }

        /**
         * Adds an edge to the constructor of the target class from this call to {@link Class#newInstance()}.
         */
        @Override
        public void classNewInstance(MethodModel container, Stmt newInstanceInvokeStmt) {
            Set<String> classNames = reflectionInfo.classNewInstanceClassNames(container);
            if (classNames == null || classNames.isEmpty()) {
                registerGuard(container, newInstanceInvokeStmt,
                        "Class.newInstance() call site; Soot did not expect this site to be reached");
            } else {
                final Scene sc = Scene.v();
                for (String clsName : classNames) {
                    MethodModel constructor = sc.getSootClass(clsName).getMethodUnsafe(sigInit);
                    if (constructor != null) {
                        addEdge(container, newInstanceInvokeStmt, constructor, Kind.REFL_CLASS_NEWINSTANCE);
                    }
                }
            }
        }

        @Override
        public void contructorNewInstance(MethodModel container, Stmt newInstanceInvokeStmt) {
            Set<String> constructorSignatures = reflectionInfo.constructorNewInstanceSignatures(container);
            if (constructorSignatures == null || constructorSignatures.isEmpty()) {
                registerGuard(container, newInstanceInvokeStmt,
                        "Constructor.newInstance(..) call site; Soot did not expect this site to be reached");
            } else {
                final Scene sc = Scene.v();
                for (String constructorSignature : constructorSignatures) {
                    MethodModel constructor = sc.getMethod(constructorSignature);
                    addEdge(container, newInstanceInvokeStmt, constructor, Kind.REFL_CONSTR_NEWINSTANCE);
                }
            }
        }

        @Override
        public void methodInvoke(MethodModel container, Stmt invokeStmt) {
            Set<String> methodSignatures = reflectionInfo.methodInvokeSignatures(container);
            if (methodSignatures == null || methodSignatures.isEmpty()) {
                registerGuard(container, invokeStmt, "Method.invoke(..) call site; Soot did not expect this site to be reached");
            } else {
                final Scene sc = Scene.v();
                for (String methodSignature : methodSignatures) {
                    MethodModel method = sc.getMethod(methodSignature);
                    addEdge(container, invokeStmt, method, Kind.REFL_INVOKE);
                }
            }
        }

        private void registerGuard(MethodModel container, Stmt stmt, String string) {
            guards.add(new Guard(container, stmt, string));

            if (options.verbose()) {
                logger.debug("Incomplete trace file: Class.forName() is called in method '" + container
                        + "' but trace contains no information about the receiver class of this call.");
                switch (options.guards()) {
                    case "ignore":
                        logger.debug("Guarding strategy is set to 'ignore'. Will ignore this problem.");
                        break;
                    case "print":
                        logger.debug("Guarding strategy is set to 'print'. "
                                + "Program will print a stack trace if this location is reached during execution.");
                        break;
                    case "throw":
                        logger.debug("Guarding strategy is set to 'throw'. "
                                + "Program will throw an error if this location is reached during execution.");
                        break;
                    default:
                        throw new RuntimeException("Invalid value for phase option (guarding): " + options.guards());
                }
            }

            if (!registeredGuardsTransformation) {
                registeredGuardsTransformation = true;
                PackManager.v().getPack("wjap").add(new Transform("wjap.guards", new SceneTransformer() {

                    @Override
                    protected void internalTransform(String phaseName, Map<String, String> options) {
                        for (Guard g : guards) {
                            insertGuard(g);
                        }
                    }
                }));
                PhaseOptions.v().setPhaseOption("wjap.guards", "enabled");
            }
        }

        private void insertGuard(Guard guard) {
            if ("ignore".equals(options.guards())) {
                return;
            }

            MethodModel container = guard.container;
            if (!container.hasActiveBody()) {
                logger.warn("Tried to insert guard into " + container + " but couldn't because method has no body.");
            } else {
                final Jimple jimp = Jimple.v();
                final Body body = container.getActiveBody();
                final UnitPatchingChain units = body.getUnits();
                final LocalGenerator lg = Scene.v().createLocalGenerator(body);

                // exc = new Error
                RefType runtimeExceptionType = RefType.v("java.lang.Error");
                Local exceptionLocal = lg.generateLocal(runtimeExceptionType);
                AssignStmt assignStmt = jimp.newAssignStmt(exceptionLocal, jimp.newNewExpr(runtimeExceptionType));
                units.insertBefore(assignStmt, guard.stmt);

                // exc.<init>(message)
                SootMethodRef cref = runtimeExceptionType.getSootClass()
                        .getMethodModel("<init>", Collections.singletonList(RefType.v("java.lang.String"))).makeRef();
                InvokeStmt initStmt
                        = jimp.newInvokeStmt(jimp.newSpecialInvokeExpr(exceptionLocal, cref, StringConstant.v(guard.message)));
                units.insertAfter(initStmt, assignStmt);

                switch (options.guards()) {
                    case "print":
                        // logger.error(exc.getMessage(), exc);
                        VirtualInvokeExpr printStackTraceExpr = jimp.newVirtualInvokeExpr(exceptionLocal,
                                Scene.v().getSootClass(Scene.v().getBaseExceptionType().toString())
                                        .getMethodModel("printStackTrace", Collections.emptyList()).makeRef());
                        units.insertAfter(jimp.newInvokeStmt(printStackTraceExpr), initStmt);
                        break;
                    case "throw":
                        units.insertAfter(jimp.newThrowStmt(exceptionLocal), initStmt);
                        break;
                    default:
                        throw new RuntimeException("Invalid value for phase option (guarding): " + options.guards());
                }
            }
        }
    }

    static final class Guard {
        final MethodModel container;
        final Stmt stmt;
        final String message;

        public Guard(MethodModel container, Stmt stmt, String message) {
            this.container = container;
            this.stmt = stmt;
            this.message = message;
        }
    }

    private static abstract class AbstractMethodIterator implements Iterator<MethodModel> {
        private MethodModel next;
        private ClassModel currClass;
        private Iterator<MethodModel> methodIterator;

        AbstractMethodIterator(ClassModel baseClass) {
            this.currClass = baseClass;
            this.next = null;
            this.methodIterator = baseClass.methodIterator();
            this.findNextMethod();
        }

        protected void findNextMethod() {
            next = null;
            if (methodIterator != null) {
                while (true) {
                    while (methodIterator.hasNext()) {
                        MethodModel n = methodIterator.next();
                        if (!n.isPublic() || n.isStatic() || n.isConstructor() || n.isStaticInitializer() || !n.isConcrete()) {
                            continue;
                        }
                        if (!acceptMethod(n)) {
                            continue;
                        }
                        next = n;
                        return;
                    }
                    if (!currClass.hasSuperclass()) {
                        methodIterator = null;
                        return;
                    }
                    ClassModel superclass = currClass.getSuperType();
                    if (superclass.isPhantom() || Scene.v().getObjectType().toString().equals(superclass.getName())) {
                        methodIterator = null;
                        return;
                    } else {
                        methodIterator = superclass.methodIterator();
                        currClass = superclass;
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public MethodModel next() {
            MethodModel toRet = next;
            findNextMethod();
            return toRet;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        protected abstract boolean acceptMethod(MethodModel m);
    }

    public VirtualEdgesSummaries getVirtualEdgeSummaries() {
        return virtualEdgeSummaries;
    }

}
