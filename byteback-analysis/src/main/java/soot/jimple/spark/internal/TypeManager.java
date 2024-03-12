package soot.jimple.spark.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.toolkits.typing.fast.WeakObjectType;
import soot.util.ArrayNumberer;
import soot.util.BitVector;
import soot.util.queue.QueueReader;

import java.util.*;
import java.util.function.Supplier;

/**
 * A map of bit-vectors representing subtype relationships.
 *
 * @author Ondrej Lhotak
 * @author Hamid A. Toussi (hamid2c@gmail.com): Making TypeManager faster by making type masks during a depth-first-traversal
 * on the class hierarchy. First, type-masks of the leaves of Class Hierarchy are created and then the type mask of
 * each type T is obtained by ORing type maks of Types sub-types and setting the bit-numbers associated with
 * Allocation Nodes of type T. The type-mask of each interface is achieved by ORing the type-masks of its top-level
 * concrete implementers. In fact, Reference types are visited in reversed-topological-order.
 */
public final class TypeManager {

    private final Map<ClassModel, List<AllocNode>> class2allocs = new HashMap<ClassModel, List<AllocNode>>(1024);
    private final List<AllocNode> anySubtypeAllocs = new LinkedList<AllocNode>();

    private static final Logger logger = LoggerFactory.getLogger(TypeManager.class);

    protected final RefType rtObject;
    protected final RefType rtSerializable;
    protected final RefType rtCloneable;

    public TypeManager(PAG pag) {
        this.pag = pag;

        this.rtObject = Scene.v().getObjectType();
        this.rtSerializable = RefType.v("java.io.Serializable");
        this.rtCloneable = RefType.v("java.lang.Cloneable");
    }

    public static boolean isUnresolved(Type type) {
        if (type instanceof ArrayType at) {
          type = at.getArrayElementType();
        }
        if (!(type instanceof RefType rt)) {
            return false;
        }
      if (!rt.hasSootClass()) {
            if (rt instanceof WeakObjectType) {
                // try to resolve sootClass one more time.
                ClassModel c = Scene.v().forceResolve(rt.getClassName(), ClassModel.HIERARCHY);
                if (c == null) {
                    return true;
                } else {
                    rt.setSootClass(c);
                }
            } else {
                return true;
            }
        }
        ClassModel cl = rt.getSootClass();
        return cl.resolvingLevel() < ClassModel.HIERARCHY;
    }

    public BitVector get(Type type) {
        if (type == null) {
            return null;
        }
        final Scene sc = Scene.v();
        while (allocNodeListener.hasNext()) {
            AllocNode n = allocNodeListener.next();
            if (n == null) {
                continue;
            }
            Type nt = n.getType();
            Iterable<Type> types;
            if (nt instanceof NullType || nt instanceof AnySubType) {
                types = sc.getTypeNumberer();
            } else {
                types = sc.getOrMakeFastHierarchy().canStoreTypeList(nt);
            }
            for (final Type t : types) {
                if (!(t instanceof RefLikeType) || (t instanceof AnySubType) || isUnresolved(t)) {
                    continue;
                }

                BitVector mask = typeMask.get(t);
                if (mask == null) {
                    typeMask.put(t, mask = new BitVector());
                    for (final AllocNode an : pag.getAllocNodeNumberer()) {
                        if (castNeverFails(an.getType(), t)) {
                            mask.set(an.getNumber());
                        }
                    }
                    continue;
                }
                mask.set(n.getNumber());

            }
        }
        BitVector ret = typeMask.get(type);
        if (ret == null && fh != null && type instanceof RefType) {
            // If we have a phantom class and have no type mask, we assume that
            // it is not cast-compatible to anything
            ClassModel curClass = ((RefType) type).getSootClass();
            if (curClass.isPhantom()) {
                return new BitVector();
            } else {
                // Scan through the hierarchy. We might have a phantom class higher up
                while (curClass.hasSuperclass()) {
                    curClass = curClass.getSuperclass();
                    if (type instanceof RefType && curClass.isPhantom()) {
                        return new BitVector();
                    }
                }
                logger.warn("Type mask not found for type " + type
                        + ". This is casued by a cast operation to a type which is a phantom class "
                        + "and no type mask was found. This may affect the precision of the point-to set.");
                BitVector soundOverApproxRet = new BitVector();
                for (int i = 0; i <= 63; i++) {
                    soundOverApproxRet.set(i);
                }
                return soundOverApproxRet;
            }
        }
        return ret;
    }

    public void clearTypeMask() {
        typeMask = null;
    }

    public void makeTypeMask() {
        RefType.v("java.lang.Class");
        typeMask = new HashMap<Type, BitVector>();
        if (fh == null) {
            return;
        }

        // **
        initClass2allocs();
        makeClassTypeMask(Scene.v().getSootClass(Scene.v().getObjectType().getClassName()));
        BitVector visitedTypes = new BitVector();
        {
            Iterator<Type> it = typeMask.keySet().iterator();
            while (it.hasNext()) {
                Type t = it.next();
                visitedTypes.set(t.getNumber());
            }
        }
        // **
        ArrayNumberer<AllocNode> allocNodes = pag.getAllocNodeNumberer();
        for (Type t : Scene.v().getTypeNumberer()) {
            if (!(t instanceof RefLikeType) || (t instanceof AnySubType) || isUnresolved(t)) {
                continue;
            }
            // **
            if (t instanceof RefType rt && t != rtObject && t != rtSerializable && t != rtCloneable) {
              ClassModel sc = rt.getSootClass();
                if (sc.isInterface()) {
                    makeMaskOfInterface(sc);
                }
                if (!visitedTypes.get(t.getNumber()) && !rt.getSootClass().isPhantom()) {
                    makeClassTypeMask(rt.getSootClass());
                }
                continue;
            }
            // **
            BitVector mask = new BitVector(allocNodes.size());
            for (Node n : allocNodes) {
                if (castNeverFails(n.getType(), t)) {
                    mask.set(n.getNumber());
                }
            }
            typeMask.put(t, mask);
        }

        allocNodeListener = pag.allocNodeListener();
    }

    private Map<Type, BitVector> typeMask = null;

    public boolean castNeverFails(Type src, Type dst) {
        if (dst == null) {
            return true;
        } else if (dst == src) {
            return true;
        } else if (src == null) {
            return false;
        } else if (src instanceof NullType) {
            return true;
        } else if (src instanceof AnySubType) {
            return true;
        } else if (dst instanceof NullType) {
            return false;
        } else if (dst instanceof AnySubType) {
            throw new RuntimeException("oops src=" + src + " dst=" + dst);
        } else {
            FastHierarchy fh = getFastHierarchy();
            if (fh == null) {
                return true;
            }
            return fh.canStoreType(src, dst);
        }
    }

    public void setFastHierarchy(Supplier<FastHierarchy> fh) {
        this.fh = fh;
    }

    public FastHierarchy getFastHierarchy() {
        return fh == null ? null : fh.get();
    }

    protected Supplier<FastHierarchy> fh = null;
    protected PAG pag;
    protected QueueReader<AllocNode> allocNodeListener = null;

    // ** new methods
    private void initClass2allocs() {
        for (AllocNode an : pag.getAllocNodeNumberer()) {
            addAllocNode(an);
        }
    }

    private void addAllocNode(final AllocNode alloc) {
        alloc.getType().apply(new TypeSwitch() {
            public void caseRefType(RefType t) {
                ClassModel cl = t.getSootClass();
                List<AllocNode> list;
                if ((list = class2allocs.get(cl)) == null) {
                    list = new LinkedList<AllocNode>();
                    class2allocs.put(cl, list);
                }
                list.add(alloc);
            }

            public void caseAnySubType(AnySubType t) {
                anySubtypeAllocs.add(alloc);
            }
        });
    }

    private BitVector makeClassTypeMask(ClassModel clazz) {
        {
            BitVector cachedMask = typeMask.get(clazz.getType());
            if (cachedMask != null) {
                return cachedMask;
            }
        }

        int nBits = pag.getAllocNodeNumberer().size();
        final BitVector mask = new BitVector(nBits);

        List<AllocNode> allocs = null;
        if (clazz.isConcrete()) {
            allocs = class2allocs.get(clazz);
        }
        if (allocs != null) {
            for (AllocNode an : allocs) {
                mask.set(an.getNumber());
            }
        }

        Collection<ClassModel> subclasses = fh.get().getSubclassesOf(clazz);
        if (subclasses == Collections.EMPTY_LIST) {
            for (AllocNode an : anySubtypeAllocs) {
                mask.set(an.getNumber());
            }
            typeMask.put(clazz.getType(), mask);
            return mask;
        }

        for (ClassModel subcl : subclasses) {
            mask.or(makeClassTypeMask(subcl));
        }

        typeMask.put(clazz.getType(), mask);
        return mask;
    }

    private BitVector makeMaskOfInterface(ClassModel interf) {
        if (!(interf.isInterface())) {
            throw new RuntimeException();
        }

        BitVector ret = new BitVector(pag.getAllocNodeNumberer().size());
        typeMask.put(interf.getType(), ret);
        Collection<ClassModel> implementers = getFastHierarchy().getAllImplementersOfInterface(interf);

        for (ClassModel impl : implementers) {
            BitVector other = typeMask.get(impl.getType());
            if (other == null) {
                other = makeClassTypeMask(impl);
            }
            ret.or(other);
        }
        // I think, the following can be eliminated. It is added to make
        // type-masks exactly the same as the original type-masks
        if (implementers.size() == 0) {
            for (AllocNode an : anySubtypeAllocs) {
                ret.set(an.getNumber());
            }
        }
        return ret;
    }

}
