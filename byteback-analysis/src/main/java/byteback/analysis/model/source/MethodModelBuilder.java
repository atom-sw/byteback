package byteback.analysis.model.source;

import byteback.analysis.common.naming.ClassNames;
import byteback.analysis.model.syntax.MethodModel;
import byteback.analysis.model.syntax.signature.MethodSignature;
import byteback.analysis.model.syntax.type.ArrayType;
import byteback.analysis.model.syntax.type.ClassType;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import byteback.analysis.model.syntax.type.Type;
import byteback.analysis.body.jimple.source.AsmMethodSource;

import java.util.*;

/**
 * Soot method builder.
 *
 * @author Aaloan Miftah
 */
public class MethodModelBuilder extends JSRInlinerAdapter {

    private final MethodModel methodModel;

    private final ClassModelBuilder classBuilder;

    private final String[] parameterNames;

    private final Map<Integer, Integer> slotToParameter;

    public MethodModelBuilder(MethodModel methodModel, ClassModelBuilder classBuilder, String desc, String[] ex) {
        super(Opcodes.ASM6, null, methodModel.getModifiers(), methodModel.getSignature().getName(), desc, null, ex);
        this.methodModel = methodModel;
        this.classBuilder = classBuilder;
        this.parameterNames = new String[methodModel.getSignature().getArgumentTypes().size()];
        this.slotToParameter = createSlotToParameterMap();
    }

    private Map<Integer, Integer> createSlotToParameterMap() {
        final MethodSignature methodSignature = methodModel.getSignature();
        final int paramCount = methodModel.getSignature().getArgumentTypes().size();
        Map<Integer, Integer> slotMap = new HashMap<>(paramCount);
        int curSlot = methodModel.isStatic() ? 0 : 1;

        for (int i = 0; i < paramCount; i++) {
            slotMap.put(curSlot, i);
            curSlot++;
            if (AsmUtil.isDWord(methodSignature.getArgumentTypes().get(i))) {
                curSlot++;
            }
        }

        return slotMap;
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
            if (t instanceof ClassType) {
                classBuilder.addDep(t);
            }
        }

        classBuilder.addDep(ClassNames.toQualifiedName(owner));
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
        if (t instanceof ClassType) {
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

    protected MethodSource createAsmMethodSource(int maxLocals, InsnList instructions, List<LocalVariableNode> localVariables,
                                                 List<TryCatchBlockNode> tryCatchBlocks) {
        return new AsmMethodSource(maxLocals, instructions, localVariables, tryCatchBlocks);
    }

    @Override
    public String toString() {
        return methodModel.toString();
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
