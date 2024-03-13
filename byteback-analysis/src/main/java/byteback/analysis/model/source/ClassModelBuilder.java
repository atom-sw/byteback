package byteback.analysis.model.source;

import byteback.analysis.common.naming.ClassNames;
import byteback.analysis.common.naming.TypeNames;
import byteback.analysis.model.Modifier;
import byteback.analysis.model.syntax.ClassModel;
import byteback.analysis.model.syntax.FieldModel;
import byteback.analysis.model.syntax.MethodModel;
import byteback.analysis.model.syntax.signature.FieldSignature;
import byteback.analysis.model.syntax.type.ClassType;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.*;
import byteback.analysis.model.syntax.type.Type;

import java.util.*;

/**
 * Constructs a Soot class from a visited class.
 *
 * @author Aaloan Miftah
 */
public class ClassModelBuilder extends ClassVisitor {
    protected final ClassModel classModel;

    protected final Set<Type> typeDependencies;

    private TagBuilder tagBuilder;

    /**
     * Constructs a new builder for the given {@link ClassModel}.
     *
     * @param classModel Soot class to build.
     */
    protected ClassModelBuilder(ClassModel classModel) {
        super(Opcodes.ASM9);
        this.classModel = classModel;
        this.typeDependencies = new HashSet<>();
    }

    protected ClassModel getClassModel() {
        return classModel;
    }

    /**
     * Adds a dependency of the target class.
     *
     * @param s name, or type of class.
     */
    protected void addDep(Type s) {
        typeDependencies.add(s);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        classModel.setModifiers(filterASMFlags(access) & ~Opcodes.ACC_SUPER);

        if (superName != null) {
            superName = ClassNames.toQualifiedName(superName);
            final ClassType superType = new ClassType(superName);
            addDep(superType);
            classModel.setSuperType(superType);
        }

        for (String intrf : interfaces) {
            intrf = ClassNames.toQualifiedName(intrf);
            final ClassType interfaceType = new ClassType(intrf);
            addDep(interfaceType);
            classModel.getImplementedTypes().add(interfaceType);
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        final Type type = TypeNames.v().toBaseType(desc);
        addDep(type);
        FieldModel field = new FieldSignature(name, filterASMFlags(access), type, classModel.getType());
        return new FieldModelBuilder(classModel.getOrAddField(field), this);
    }

    public static int filterASMFlags(int access) {
        return access & ~Opcodes.ACC_DEPRECATED & ~Opcodes.ACC_RECORD;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        List<ClassModel> thrownExceptions;

        if (exceptions == null || exceptions.length == 0) {
            thrownExceptions = Collections.emptyList();
        } else {
            int len = exceptions.length;
            thrownExceptions = new ArrayList<>(len);
            for (int i = 0; i != len; i++) {
                String ex = ClassNames.toQualifiedName(exceptions[i]);
                addDep(makeRefType(ex));
                thrownExceptions.add(makeClassRef(ex));
            }
        }

        List<Type> sigTypes = AsmUtil.toJimpleDesc(desc);

        for (Type type : sigTypes) {
            addDep(type);
        }

        MethodModel method = Scene.v().makeSootMethod(name, sigTypes, sigTypes.remove(sigTypes.size() - 1),
                filterASMFlags(access), thrownExceptions);

        return createMethodBuilder(classModel.getOrAddMethod(method), desc, exceptions);
    }

    protected MethodVisitor createMethodBuilder(MethodModel methodModel, String desc, String[] exceptions) {
        return new MethodModelBuilder(methodModel, this, desc, exceptions);
    }
}
