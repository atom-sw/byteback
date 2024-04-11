package byteback.analysis.global.vimp.syntax.value;

import byteback.analysis.global.vimp.syntax.type.TypeType;
import byteback.analysis.local.common.syntax.value.DefaultCaseValue;
import soot.Type;
import soot.jimple.Constant;

public class TypeConstant extends Constant implements DefaultCaseValue {

    public final Type value;

    public TypeConstant(final Type type) {
        this.value = type;
    }

    @Override
    public Type getType() {
        return TypeType.v();
    }

    @Override
    public Object clone() {
        return null;
    }

    @Override
    public boolean equivTo(Object o) {
        return o instanceof final TypeConstant typeConstant
                && typeConstant.value == value;
    }

    @Override
    public int equivHashCode() {
        return value.getNumber() * 31;
    }

}
