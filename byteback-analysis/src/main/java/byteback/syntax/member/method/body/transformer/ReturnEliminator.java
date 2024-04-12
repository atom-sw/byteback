package byteback.syntax.member.method.body.transformer;

import byteback.syntax.Vimp;
import byteback.syntax.value.ReturnRef;
import byteback.common.function.Lazy;
import soot.Body;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.util.Chain;

import java.util.Iterator;

/**
 * Removes return statements and replaces them to an assignment to the @return reference followed by a `yield`
 * statement.
 *
 * @author paganma
 */
public class ReturnEliminator extends BodyTransformer {

    private static final Lazy<ReturnEliminator> instance = Lazy.from(ReturnEliminator::new);

    private ReturnEliminator() {
    }

    public static ReturnEliminator v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final Type returnType = body.getMethod().getReturnType();
        final Chain<Unit> units = body.getUnits();
        final ReturnRef returnRef = new ReturnRef(returnType);
        final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof ReturnStmt returnStmt) {
                final Value returnValue = returnStmt.getOp();
                final Unit returnAssignUnit = Jimple.v().newAssignStmt(returnRef, returnValue);
                units.insertBefore(returnAssignUnit, returnStmt);
                returnStmt.redirectJumpsToThisTo(returnAssignUnit);
                returnAssignUnit.addAllTagsOf(returnStmt);
                units.swapWith(returnStmt, Vimp.v().newYieldStmt());
            } else if (unit instanceof ReturnVoidStmt returnVoidStmt) {
                units.swapWith(returnVoidStmt, Vimp.v().newYieldStmt());
            }
        }
    }

}
