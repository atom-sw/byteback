package byteback.analysis.body.vimp;

import byteback.analysis.body.common.Body;
import byteback.analysis.body.common.syntax.Immediate;
import byteback.analysis.body.common.syntax.Local;
import byteback.analysis.body.common.syntax.LocalGenerator;
import byteback.analysis.body.common.syntax.Value;
import soot.*;
import byteback.analysis.body.jimple.syntax.AssignStmt;
import byteback.analysis.body.jimple.syntax.Jimple;

import java.util.function.BiFunction;
import java.util.function.Function;

public class VimpExprFactory {

    private final LocalGenerator localGenerator;

    public VimpExprFactory(final LocalGenerator localGenerator) {
        this.localGenerator = localGenerator;
    }

    public VimpExprFactory(final Body body) {
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

    public Immediate binary(final BiFunction<Value, Value, Value> constructor, final Value op1, final Value op2) {
        final Value nestedOp1 = nestIfNeeded(op1);
        final Value nestedOp2 = nestIfNeeded(op2);

        return nestIfNeeded(constructor.apply(nestedOp1, nestedOp2));
    }

    public Immediate unary(final Function<Value, Value> constructor, final Value op) {
        final Value nestedOp = nestIfNeeded(op);

        return nestIfNeeded(constructor.apply(nestedOp));
    }

}
