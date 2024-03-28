package byteback.analysis.body.vimp.syntax;

import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.ConcreteRef;
import soot.jimple.IdentityRef;

import java.util.Collections;
import java.util.List;

/**
 * Reference corresponding to the value returned by a method.
 *
 * @author paganma
 */
public class ReturnRef implements ConcreteRef, IdentityRef, Unswitchable {

    final Type type;

    public ReturnRef(final Type type) {
        this.type = type;
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

    @Override
    public boolean equivTo(Object o) {
        return false;
    }

    @Override
    public int equivHashCode() {
        return 155809;
    }

}
