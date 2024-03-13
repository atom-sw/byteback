package byteback.analysis.model.syntax;

import byteback.analysis.common.syntax.Chain;
import byteback.analysis.common.syntax.HashChain;
import byteback.analysis.model.syntax.type.ClassType;

import java.util.Optional;

public class ClassModel extends Model<ClassType> {

    protected ClassType type;

    protected final Chain<FieldModel> fieldModels;

    protected final Chain<MethodModel> methodModels;

    protected final Chain<ClassType> implementedTypes;

    protected ClassType superType;

    public ClassModel(final ClassType classType) {
        this(classType, 0);
    }

    public ClassModel(final ClassType classType, int modifiers) {
        super(modifiers, classType);
        this.type = classType;
        this.fieldModels = new HashChain<>();
        this.methodModels = new HashChain<>();
        this.implementedTypes = new HashChain<>();
    }

    public ClassType getType() {
        return type;
    }

    public void setType(final ClassType type) {
        this.type = type;
    }

    public Optional<ClassType> getSuperType() {
        return Optional.ofNullable(superType);
    }

    public void setSuperType(final ClassType superType) {
        this.superType = superType;
    }

    public Chain<FieldModel> getFieldModels() {
        return fieldModels;
    }

    public Chain<MethodModel> getMethodModels() {
        return methodModels;
    }

    public Chain<ClassType> getImplementedTypes() {
        return implementedTypes;
    }
}