package byteback.analysis.body.vimp;

import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ImmediateConstructor {

    private final LocalGenerator localGenerator;

    public ImmediateConstructor(final LocalGenerator localGenerator) {
        this.localGenerator = localGenerator;
    }

    public ImmediateConstructor(final Body body) {
        this.localGenerator = Scene.v().createLocalGenerator(body);
    }

    public Immediate nestIfNeeded(final Value value) {
        if (value instanceof Immediate immediate) {
            return immediate;
        } else {
            final Local local = localGenerator.generateLocal(value.getType());
            final AssignStmt assignStmt = Jimple.v().newAssignStmt(local, value);

            return Vimp.v().newNestedExpr(assignStmt);
        }
    }

    public Immediate make(final BiFunction<Value, Value, Value> constructor, final Value op1, final Value op2, final Value... rest) {
        Value nestedOp1 = nestIfNeeded(op1);
        Immediate nestedOp2 = nestIfNeeded(op2);

        for (final Value op : rest) {
            nestedOp2 = nestIfNeeded(constructor.apply(nestedOp1, nestedOp2));
            nestedOp1 = op;
        }

        return nestedOp2;
    }

    public Immediate make(final BiFunction<Value, Value, Value> constructor, final Value op1, final Value op2) {
        final Value nestedOp1 = nestIfNeeded(op1);
        final Value nestedOp2 = nestIfNeeded(op2);

        return nestIfNeeded(constructor.apply(nestedOp1, nestedOp2));
    }

    public Immediate make(final Function<Value, Value> constructor, final Value op) {
        final Value nestedOp = nestIfNeeded(op);

        return nestIfNeeded(constructor.apply(nestedOp));
    }

}
