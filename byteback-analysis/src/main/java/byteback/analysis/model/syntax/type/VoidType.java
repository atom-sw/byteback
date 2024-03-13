package byteback.analysis.model.syntax.type;

import byteback.analysis.model.syntax.type.visitor.TypeSwitch;

public class VoidType extends Type {

    public static final int HASHCODE = 0x3A8C1035;

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public void apply(final TypeSwitch<?> typeSwitch) {
        typeSwitch.caseVoidType(this);
    }
}
