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

import byteback.analysis.model.ClassModel;
import byteback.analysis.model.FieldModel;
import byteback.analysis.model.MethodModel;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.*;
import soot.Type;
import soot.*;
import soot.asm.AsmUtil;
import soot.options.Options;
import soot.tag.*;

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

    private TagBuilder getTagBuilder() {
        TagBuilder t = tagBuilder;
        if (t == null) {
            t = tagBuilder = new TagBuilder(classModel, this);
        }
        return t;
    }

    protected ClassModel getClassModel() {
        return classModel;
    }

    protected void addDep(final String typeName) {
        addDep(makeRefType(AsmUtil.stripArrayTypeName(typeName)));
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
        setJavaVersion(version);
        name = AsmUtil.toQualifiedName(name);

        if (!name.equals(classModel.getName()) && Options.v().verbose()) {
            System.err.println("Class names not equal! " + name + " != " + classModel.getName());
        }

        classModel.setModifiers(filterASMFlags(access) & ~Opcodes.ACC_SUPER);

        if (superName != null) {
            superName = AsmUtil.toQualifiedName(superName);
            addDep(makeRefType(superName));
            ClassModel superClass = makeClassRef(superName);
            classModel.setSuperclass(superClass);
        }

        for (String intrf : interfaces) {
            intrf = AsmUtil.toQualifiedName(intrf);
            addDep(makeRefType(intrf));
            ClassModel interfaceClass = makeClassRef(intrf);
            interfaceClass.setModifiers(interfaceClass.getModifiers() | Modifier.INTERFACE);
            classModel.addInterfaceType(interfaceClass);
        }

        if (signature != null) {
            classModel.addTag(new SignatureTag(signature));
        }
    }

    private void setJavaVersion(int version) {
        final Options opts = Options.v();
        if (opts.derive_java_version()) {
            opts.set_java_version(Math.max(opts.java_version(), AsmUtil.byteCodeToJavaVersion(version)));
        }
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        Type type = AsmUtil.toJimpleTypeName(desc);
        addDep(type);
        FieldModel field = Scene.v().makeSootField(name, type, filterASMFlags(access));
        Tag tag;
        if (value instanceof Integer) {
            tag = new IntegerConstantValueTag((Integer) value);
        } else if (value instanceof Float) {
            tag = new FloatConstantValueTag((Float) value);
        } else if (value instanceof Long) {
            tag = new LongConstantValueTag((Long) value);
        } else if (value instanceof Double) {
            tag = new DoubleConstantValueTag((Double) value);
        } else if (value instanceof String) {
            tag = new StringConstantValueTag(value.toString());
        } else {
            tag = null;
        }
        if (tag != null) {
            field.addTag(tag);
        }
        if (signature != null) {
            field.addTag(new SignatureTag(signature));
        }
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
                String ex = AsmUtil.toQualifiedName(exceptions[i]);
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

        if (signature != null) {
            method.addTag(new SignatureTag(signature));
        }

        return createMethodBuilder(classModel.getOrAddMethod(method), desc, exceptions);
    }

    protected MethodVisitor createMethodBuilder(MethodModel methodModel, String desc, String[] exceptions) {
        return new MethodModelBuilder(methodModel, this, desc, exceptions);
    }

    @Override
    public void visitSource(String source, String debug) {
        if (source != null) {
            classModel.addTag(new SourceFileTag(source));
        }
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        classModel.addTag(new InnerClassTag(name, outerName, innerName, access));
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        if (name != null) {
            classModel.addTag(new EnclosingMethodTag(owner, name, desc));
        }

        owner = AsmUtil.toQualifiedName(owner);
        typeDependencies.add(makeRefType(owner));
        classModel.setOuterClass(makeClassRef(owner));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return getTagBuilder().visitAnnotation(desc, visible);
    }

    @Override
    public void visitAttribute(Attribute attr) {
        getTagBuilder().visitAttribute(attr);
    }

    private ClassModel makeClassRef(String className) {
        return SootResolver.v().makeClassRef(className);
    }

    private RefType makeRefType(String className) {
        return RefType.v(className);
    }
}
