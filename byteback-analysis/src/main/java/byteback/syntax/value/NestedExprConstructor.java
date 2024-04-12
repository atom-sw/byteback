package byteback.syntax.value;

import byteback.syntax.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.Vimp;
import soot.*;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;

import java.util.function.Function;

/**
 * Factory for creating nested expressions.
 *
 * @author paganma
 */
public class NestedExprConstructor implements Function<Value, Immediate> {

    private final LocalGenerator localGenerator;

    public NestedExprConstructor(final LocalGenerator localGenerator) {
        this.localGenerator = localGenerator;
    }

    public NestedExprConstructor(final Body body) {
        this.localGenerator = new DefaultLocalGenerator(body);
    }

    public Immediate apply(final Value value) {
        if (value instanceof Immediate immediate) {
            return immediate;
        } else {
            // Assign a new fresh local, which has exactly one definition and one use (hence obeying the contract
            // established by NestedExpr's constructor).
            final Local local = localGenerator.generateLocal(VimpTypeInterpreter.v().typeOf(value));
            final AssignStmt assignStmt = Jimple.v().newAssignStmt(local, value);

            return Vimp.v().newNestedExpr(assignStmt);
        }
    }

}
