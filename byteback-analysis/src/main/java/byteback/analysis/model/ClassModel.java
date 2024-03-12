package byteback.analysis.model;

import soot.*;
import soot.tag.AbstractHost;
import soot.util.*;

import java.util.*;

public class ClassModel extends AbstractHost {
    protected String name;

    protected int modifiers;

    protected Chain<FieldModel> fieldModels;

    protected Chain<MethodModel> methodModels;

    protected Chain<RefType> interfaceTypes;

    protected RefType superType;

    private RefType type;

    public ClassModel(final String className) {
        this(className, 0);
    }

    public ClassModel(final String name, int modifiers) {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Class name must not be empty!");
        }

        if (name.charAt(0) == '[') {
            throw new IllegalArgumentException("Attempt to make a class whose name starts with [");
        }

        setModifiers(modifiers);
        setName(name);
        setType(RefType.v(name));
        this.fieldModels = new HashChain<>();
        this.methodModels = new HashChain<>();
        this.interfaceTypes = new HashChain<>();
    }


    public String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name.intern();
    }

    public RefType getType() {
        return type;
    }

    protected final void setType(final RefType type) {
        this.type = type;
    }

    public int getModifiers() {
        return modifiers;
    }

    public final void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }


    public Chain<FieldModel> getFieldModels() {
        return fieldModels == null ? EmptyChain.v() : fieldModels;
    }

    public void addFieldModel(final FieldModel fieldModel) {
        final String fieldName = fieldModel.getName();

        if (fieldModel.isDeclared()) {
            throw new RuntimeException("already declared: " + fieldName);
        }

        final Type fieldType = fieldModel.getType();

        if (declaresFieldModel(fieldName, fieldType)) {
            throw new RuntimeException("Field already exists : " + fieldName + " of type " + fieldType);
        }

        fieldModel.setDeclared(true);
        fieldModel.setDeclaringClass(this);

        fieldModels.add(fieldModel);
    }

    public void removeFieldModel(final FieldModel fieldModel) {
        if (!fieldModel.isDeclared() || fieldModel.getDeclaringClass() != this) {
            throw new RuntimeException("did not declare: " + fieldModel.getName());
        }

        if (fieldModels != null) {
            fieldModels.remove(fieldModel);
        }

        fieldModel.setDeclared(false);
        fieldModel.setDeclaringClass(null);
    }

    public Optional<FieldModel> getFieldModel(final String name, final Type type) {
        for (final FieldModel fieldModel : fieldModels.getElementsUnsorted()) {
            if (name.equals(fieldModel.getName()) && type.equals(fieldModel.getType())) {
                return Optional.of(fieldModel);
            }
        }

        return Optional.empty();
    }

    public Optional<FieldModel> getFieldModel(final String name) {
        FieldModel foundField = null;

        for (final FieldModel field : fieldModels.getElementsUnsorted()) {
            if (name.equals(field.getName())) {
                if (foundField == null) {
                    foundField = field;
                } else {
                    throw new AmbiguousFieldException(name, this.name);
                }
            }
        }

        return Optional.ofNullable(foundField);
    }

    public boolean declaresFieldModel(final String name, final Type type) {
        for (final FieldModel field : fieldModels) {
            if (name.equals(field.getName()) && type.equals(field.getType())) {
                return true;
            }
        }

        return false;
    }

    public void addMethod(final MethodModel methodModel) {
        methodModel.setDeclared(true);
        methodModel.setDeclaringClass(this);
    }

    public void removeMethod(final MethodModel methodModel) {
        if (!methodModel.isDeclared() || methodModel.getDeclaringClass() != this) {
            throw new IllegalStateException("Incorrect declarer for remove: " + methodModel.getName());
        }

        methodModels.remove(methodModel);
        methodModel.setDeclared(false);
        methodModel.setDeclaringClass(null);
    }

    public Optional<MethodModel> getMethodModel(final String name, final List<Type> parameterTypes, final Type returnType) {
        if (methodModels != null) {
            for (final MethodModel method : methodModels) {
                if (name.equals(method.getName()) && returnType.equals(method.getReturnType())
                        && parameterTypes.equals(method.getParameterTypes())) {
                    return Optional.of(method);
                }
            }
        }

        return Optional.empty();
    }


    public Optional<MethodModel> getMethodModel(final String name, final List<Type> parameterTypes) {
        if (methodModels != null) {
            for (final MethodModel method : methodModels) {
                if (name.equals(method.getName()) && parameterTypes.equals(method.getParameterTypes())) {
                    return Optional.of(method);
                }
            }
        }

        return Optional.empty();
    }

    public Optional<MethodModel> getMethodModel(final String name) {
        MethodModel foundMethod = null;

        for (final MethodModel methodModel : methodModels) {
            if (name.equals(methodModel.getName())) {
                if (foundMethod == null) {
                    foundMethod = methodModel;
                } else {
                    throw new AmbiguousMethodException(name, this.name);
                }
            }
        }

        return Optional.of(foundMethod);
    }

    public boolean declaresMethod(final String name, final List<Type> parameterTypes) {
        for (final MethodModel methodModel : methodModels) {
            if (name.equals(methodModel.getName()) && parameterTypes.equals(methodModel.getParameterTypes())) {
                return true;
            }
        }

        return false;
    }

    public boolean declaresMethod(final String name, final List<Type> parameterTypes, final Type returnType) {
        for (final MethodModel methodModel : methodModels) {
            if (name.equals(methodModel.getName()) && returnType.equals(methodModel.getReturnType())
                    && parameterTypes.equals(methodModel.getParameterTypes())) {
                return true;
            }
        }

        return false;
    }

    public Chain<RefType> getInterfaceTypes() {
        return interfaceTypes;
    }

    public void addInterfaceType(final RefType interfaceType) {
        interfaceTypes.add(interfaceType);
    }

    public void removeInterfaceType(final RefType interfaceType) {
        if (!implementsInterface(interfaceType)) {
            throw new RuntimeException("no such interface on class " + this.getName() + ": " + interfaceType);
        }

        interfaceTypes.remove(interfaceType);
    }

    public boolean implementsInterface(final RefType interfaceType) {
        return interfaceTypes.contains(interfaceType);
    }

    public boolean hasSuperclass() {
        return getSuperType().isPresent();
    }

    public Optional<RefType> getSuperType() {
        return Optional.ofNullable(superType);
    }

    public void setSuperType(final RefType superType) {
        this.superType = superType;
    }

}