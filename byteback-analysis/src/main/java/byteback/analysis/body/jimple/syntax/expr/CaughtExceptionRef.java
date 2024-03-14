package byteback.analysis.body.jimple.syntax.expr;

import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.model.syntax.type.ClassType;
import byteback.analysis.model.syntax.type.Type;
import byteback.common.function.Lazy;

import java.util.Collections;
import java.util.List;

public class CaughtExceptionRef implements ConcreteRef{

    private static final Lazy<CaughtExceptionRef> instance = Lazy.from(CaughtExceptionRef::new);

    public static CaughtExceptionRef v() {
        return instance.get();
    }

    private final ClassType type;

    private CaughtExceptionRef() {
        this.type = new ClassType("java.lang.Throwable");
    }

    public boolean equivTo(Object c) {
        return c instanceof CaughtExceptionRef;
    }

    @Override
    public String toString() {
        return "@caughtexception";
    }

    @Override
    public final List<ValueBox> getUseBoxes() {
        return Collections.emptyList();
    }

    @Override
    public Type getType() {
        return type;
    }
}
