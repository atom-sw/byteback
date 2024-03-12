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

import byteback.analysis.model.MethodModel;
import soot.*;
import soot.jimple.spark.pag.*;

/**
 * This {@link TypeSwitch} can be used to add library behavior to the PAG. It adds allocation nodes with {@link AnySubType}
 * of the declared type to the target node.
 *
 * @author Florian Kuebler
 */
public class SparkLibraryHelper extends TypeSwitch {

    private final PAG pag;
    private final Node node;
    private final MethodModel method;

    /**
     * The constructor for this {@link TypeSwitch}.
     *
     * @param pag    the pointer assignment graph in that the new edges and nodes should be added into.
     * @param node   the node of the value for which allocations should be made.
     * @param method the method in which the allocations should take place. This parameter can be null.
     */
    public SparkLibraryHelper(PAG pag, Node node, MethodModel method) {
        this.pag = pag;
        this.node = node;
        this.method = method;
    }

    /**
     * A new local will be created and connected to {@link SparkLibraryHelper#node} of type {@link RefType}. For this new local
     * an allocation edge to {@link AnySubType} of its declared type will be added.
     */
    @Override
    public void caseRefType(RefType t) {
        // var tmp;
        VarNode local = pag.makeLocalVarNode(new Object(), t, method);

        // new T();
        AllocNode alloc = pag.makeAllocNode(new Object(), AnySubType.v(t), method);

        // tmp = new T();
        pag.addAllocEdge(alloc, local);

        // x = tmp;
        pag.addEdge(local, node);
    }

    /**
     * A new local array will be created and connected to {@link SparkLibraryHelper#node} of type {@link ArrayType}. For this
     * new local an allocation edge to a new array of its declared type will be added. If the
     * {@link ArrayType#getElementType()} is still an array an allocation to a new array of this element type will be made and
     * stored until the element type is a {@link RefType}. If this is the case an allocation to {@link AnySubType} of
     * {@link ArrayType#baseType} will be made.
     */
    @Override
    public void caseArrayType(ArrayType type) {
        Node array = node;
        for (Type t = type; t instanceof ArrayType at; t = ((ArrayType) t).getElementType()) {
            if (at.baseType instanceof RefType) {

                // var tmpArray;
                LocalVarNode localArray = pag.makeLocalVarNode(new Object(), t, method);

                // x = tmpArray;
                pag.addEdge(localArray, array);

                // new T[]
                AllocNode newArray = pag.makeAllocNode(new Object(), at, method);

                // tmpArray = new T[]
                pag.addEdge(newArray, localArray);

                // tmpArray[i]
                FieldRefNode arrayRef = pag.makeFieldRefNode(localArray, ArrayElement.v());

                // var tmp
                LocalVarNode local = pag.makeLocalVarNode(new Object(), at.getElementType(), method);

                // tmpArray[i] = tmp
                pag.addEdge(local, arrayRef);

                // x = tmp
                array = local;

                if (at.numDimensions == 1) {
                    // new T()
                    AllocNode alloc = pag.makeAllocNode(new Object(), AnySubType.v((RefType) at.baseType), method);

                    // tmp = new T()
                    pag.addEdge(alloc, local);
                }
            }
        }
    }

}
