package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Immediate;
import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.ConcreteRef;

import java.util.Collections;
import java.util.List;

public abstract class ParameterRef implements ConcreteRef, Immediate {

    private final String name;

    private final Type type;

    public ParameterRef(final String name, final Type type) {
        this.name = name;
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
    public void toString(final UnitPrinter printer) {
        printer.literal("@" + name);
    }

    @Override
    public Object clone() {
        throw new UnsupportedOperationException();
    }

}
