package byteback.analysis.model.syntax;

import byteback.analysis.common.syntax.Chain;
import byteback.analysis.common.syntax.HashChain;

import java.util.Optional;

public class ClassModel extends Model<byteback.analysis.model.syntax.type.ClassType> {

    protected byteback.analysis.model.syntax.type.ClassType type;

    protected final Chain<FieldModel> fieldModels;

    protected final Chain<MethodModel> methodModels;

    protected final Chain<byteback.analysis.model.syntax.type.ClassType> implementedTypes;

    protected byteback.analysis.model.syntax.type.ClassType superType;

    public ClassModel(final byteback.analysis.model.syntax.type.ClassType classType) {
        this(classType, 0);
    }

    public ClassModel(final byteback.analysis.model.syntax.type.ClassType classType, int modifiers) {
        super(modifiers, classType);
        this.type = classType;
        this.fieldModels = new HashChain<>();
        this.methodModels = new HashChain<>();
        this.implementedTypes = new HashChain<>();
    }

    public byteback.analysis.model.syntax.type.ClassType getType() {
        return type;
    }

    public String getName() {
        return type.getName();
    }

    public void setType(final byteback.analysis.model.syntax.type.ClassType type) {
        this.type = type;
    }

    public Optional<byteback.analysis.model.syntax.type.ClassType> getSuperType() {
        return Optional.ofNullable(superType);
    }

    public void setSuperType(final byteback.analysis.model.syntax.type.ClassType superType) {
        this.superType = superType;
    }

    public Chain<FieldModel> getFieldModels() {
        return fieldModels;
    }

    public Chain<MethodModel> getMethodModels() {
        return methodModels;
    }

    public Chain<byteback.analysis.model.syntax.type.ClassType> getImplementedTypes() {
        return implementedTypes;
    }
}