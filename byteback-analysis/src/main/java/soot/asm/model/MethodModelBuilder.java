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

import org.objectweb.asm.Attribute;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import soot.Type;
import soot.*;
import soot.asm.body.AsmMethodSource;
import soot.asm.AsmUtil;
import soot.tag.*;

import java.util.*;

/**
 * Soot method builder.
 *
 * @author Aaloan Miftah
 */
public class MethodModelBuilder extends JSRInlinerAdapter {

    private TagBuilder tb;
    private VisibilityAnnotationTag[] visibleParamAnnotations;
    private VisibilityAnnotationTag[] invisibleParamAnnotations;
    private List<VisibilityAnnotationTag> visibleLocalVarAnnotations;
    private List<VisibilityAnnotationTag> invisibleLocalVarAnnotations;
    private final SootMethod method;
    private final ClassModelBuilder classBuilder;
    private final String[] parameterNames;
    private final Map<Integer, Integer> slotToParameter;

    public MethodModelBuilder(SootMethod method, ClassModelBuilder classBuilder, String desc, String[] ex) {
        super(Opcodes.ASM6, null, method.getModifiers(), method.getName(), desc, null, ex);
        this.method = method;
        this.classBuilder = classBuilder;
        this.parameterNames = new String[method.getParameterCount()];
        this.slotToParameter = createSlotToParameterMap();
    }

    private Map<Integer, Integer> createSlotToParameterMap() {
        final int paramCount = method.getParameterCount();
        Map<Integer, Integer> slotMap = new HashMap<>(paramCount);
        int curSlot = method.isStatic() ? 0 : 1;
        for (int i = 0; i < paramCount; i++) {
            slotMap.put(curSlot, i);
            curSlot++;
            if (AsmUtil.isDWord(method.getParameterType(i))) {
                curSlot++;
            }
        }
        return slotMap;
    }

    private TagBuilder getTagBuilder() {
        TagBuilder t = tb;
        if (t == null) {
            t = tb = new TagBuilder(method, classBuilder);
        }
        return t;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return getTagBuilder().visitAnnotation(desc, visible);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return new AnnotationElementBuilder(1) {
            @Override
            public void visitEnd() {
                method.addTag(new AnnotationDefaultTag(annotationElements.get(0)));
            }
        };
    }

    @Override
    public void visitAttribute(Attribute attr) {
        getTagBuilder().visitAttribute(attr);
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, descriptor, signature, start, end, index);

        if (name != null && !name.isEmpty() && index > 0) {
            Integer paramIdx = slotToParameter.get(index);
            if (paramIdx != null) {
                parameterNames[paramIdx] = name;
            }
        }
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(final int typeRef, final TypePath typePath, final Label[] start,
                                                          final Label[] end, final int[] index, final String descriptor, final boolean visible) {
        final VisibilityAnnotationTag vat
                = new VisibilityAnnotationTag(visible ? AnnotationConstants.RUNTIME_VISIBLE : AnnotationConstants.RUNTIME_INVISIBLE);
        if (visible) {
            if (visibleLocalVarAnnotations == null) {
                visibleLocalVarAnnotations = new ArrayList<VisibilityAnnotationTag>(2);
            }
            visibleLocalVarAnnotations.add(vat);
        } else {
            if (invisibleLocalVarAnnotations == null) {
                invisibleLocalVarAnnotations = new ArrayList<VisibilityAnnotationTag>(2);
            }
            invisibleLocalVarAnnotations.add(vat);
        }
        return new AnnotationElementBuilder() {
            @Override
            public void visitEnd() {
                AnnotationTag annotTag = new AnnotationTag(desc, annotationElements);
                vat.addAnnotation(annotTag);
            }
        };
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, final String desc, boolean visible) {
        VisibilityAnnotationTag vat;
        VisibilityAnnotationTag[] vats;
        if (visible) {
            vats = visibleParamAnnotations;
            if (vats == null) {
                vats = new VisibilityAnnotationTag[method.getParameterCount()];
                visibleParamAnnotations = vats;
            }
            vat = vats[parameter];
            if (vat == null) {
                vat = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE);
                vats[parameter] = vat;
            }
        } else {
            vats = invisibleParamAnnotations;
            if (vats == null) {
                vats = new VisibilityAnnotationTag[method.getParameterCount()];
                invisibleParamAnnotations = vats;
            }
            vat = vats[parameter];
            if (vat == null) {
                vat = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_INVISIBLE);
                vats[parameter] = vat;
            }
        }
        final VisibilityAnnotationTag _vat = vat;
        return new AnnotationElementBuilder() {
            @Override
            public void visitEnd() {
                AnnotationTag annotTag = new AnnotationTag(desc, annotationElements);
                _vat.addAnnotation(annotTag);
            }
        };
    }

    @Override
    public void visitTypeInsn(int op, String t) {
        super.visitTypeInsn(op, t);
        Type refType = AsmUtil.toRefType(t);
        if (refType instanceof ArrayType) {
            classBuilder.addDep(((ArrayType) refType).baseType);
        } else {
            classBuilder.addDep(refType);
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, owner, name, desc);
        for (Type t : AsmUtil.toJimpleDesc(desc)) {
            if (t instanceof RefType) {
                classBuilder.addDep(t);
            }
        }

        classBuilder.addDep(AsmUtil.toQualifiedName(owner));
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean isInterf) {
        super.visitMethodInsn(opcode, owner, name, desc, isInterf);

        for (Type t : AsmUtil.toJimpleDesc(desc)) {
            addDeps(t);
        }

        classBuilder.addDep(AsmUtil.toRefType(owner));
    }

    @Override
    public void visitLdcInsn(Object cst) {
        super.visitLdcInsn(cst);

        if (cst instanceof final Handle methodHandle) {
            classBuilder.addDep(AsmUtil.toBaseType(methodHandle.getOwner()));
        }
    }

    private void addDeps(Type t) {
        if (t instanceof RefType) {
            classBuilder.addDep(t);
        } else if (t instanceof ArrayType at) {
            addDeps(at.getElementType());
        }
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        super.visitTryCatchBlock(start, end, handler, type);
        if (type != null) {
            classBuilder.addDep(AsmUtil.toQualifiedName(type));
        }
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        if (visibleParamAnnotations != null) {
            VisibilityParameterAnnotationTag tag
                    = new VisibilityParameterAnnotationTag(visibleParamAnnotations.length, AnnotationConstants.RUNTIME_VISIBLE);
            for (VisibilityAnnotationTag vat : visibleParamAnnotations) {
                tag.addVisibilityAnnotation(vat);
            }
            method.addTag(tag);
        }
        if (invisibleParamAnnotations != null) {
            VisibilityParameterAnnotationTag tag
                    = new VisibilityParameterAnnotationTag(invisibleParamAnnotations.length, AnnotationConstants.RUNTIME_INVISIBLE);
            for (VisibilityAnnotationTag vat : invisibleParamAnnotations) {
                tag.addVisibilityAnnotation(vat);
            }
            method.addTag(tag);
        }
        if (visibleLocalVarAnnotations != null) {
            VisibilityLocalVariableAnnotationTag tag
                    = new VisibilityLocalVariableAnnotationTag(visibleLocalVarAnnotations.size(), AnnotationConstants.RUNTIME_VISIBLE);
            for (VisibilityAnnotationTag vat : visibleLocalVarAnnotations) {
                tag.addVisibilityAnnotation(vat);
            }
            method.addTag(tag);
        }
        if (invisibleLocalVarAnnotations != null) {
            VisibilityLocalVariableAnnotationTag tag = new VisibilityLocalVariableAnnotationTag(
                    invisibleLocalVarAnnotations.size(), AnnotationConstants.RUNTIME_INVISIBLE);
            for (VisibilityAnnotationTag vat : invisibleLocalVarAnnotations) {
                tag.addVisibilityAnnotation(vat);
            }
            method.addTag(tag);
        }
        if (!isFullyEmpty(parameterNames)) {
            method.addTag(new ParamNamesTag(parameterNames));
        }
        if (method.isConcrete()) {
            method.setSource(
                    createAsmMethodSource(maxLocals, instructions, localVariables, tryCatchBlocks));
        }
    }

    protected MethodSource createAsmMethodSource(int maxLocals, InsnList instructions, List<LocalVariableNode> localVariables,
                                                 List<TryCatchBlockNode> tryCatchBlocks) {
        return new AsmMethodSource(maxLocals, instructions, localVariables, tryCatchBlocks);
    }

    /**
     * Gets whether the given array is fully empty, i.e., contains only <code>null</code> values
     *
     * @param array The array to check
     * @return True if the given arry contains only <code>null</code> values, false otherwise
     */
    private boolean isFullyEmpty(String[] array) {
        for (String string : array) {
            if (string != null && !string.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return method.toString();
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle,
                                       Object... bootstrapMethodArguments) {
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);

        // convert info on bootstrap method
        final String bsmClassName = AsmUtil.toQualifiedName(bootstrapMethodHandle.getOwner());
        classBuilder.addDep(RefType.v(bsmClassName));

        for (Object arg : bootstrapMethodArguments) {
            if (arg instanceof final Handle argHandle) {
                String handleClsName = AsmUtil.toQualifiedName(argHandle.getOwner());
                classBuilder.addDep(RefType.v(handleClsName));
            }
        }
    }

}
