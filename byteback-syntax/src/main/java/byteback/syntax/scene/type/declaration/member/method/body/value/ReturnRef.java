package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.ConcreteRef;

import java.util.Collections;
import java.util.List;

/**
 * Reference corresponding to the value returned by a method.
 *
 * @author paganma
 */
public class ReturnRef implements ConcreteRef, DefaultCaseValue {

    private final Type type;

    public ReturnRef(final Type type) {
        this.type = type;
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof final ReturnRef returnRef
                && returnRef.getType().equals(getType());
    }

    @Override
    public int equivHashCode() {
        return 31 * getType().hashCode();
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Object clone() {
        return new ReturnRef(type);
    }

    @Override
    public void toString(final UnitPrinter printer) {
        printer.literal("@return");
    }

}
