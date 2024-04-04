package byteback.analysis.local.vimp.transformer.body;

import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.local.vimp.syntax.value.ReturnRef;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;
import soot.util.Chain;

import java.util.Iterator;

/**
 * Converts BBLib's invoke statements into specification statements, corresponding to assertions, assumptions,
 * and invariants.
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

        if (returnType != VoidType.v()) {
            final Chain<Unit> units = body.getUnits();
            final ReturnRef returnRef = new ReturnRef(returnType);
            final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();

            while (unitIterator.hasNext()) {
                if (unitIterator.next() instanceof ReturnStmt returnUnit) {
                    final Value returnValue = returnUnit.getOp();
                    final Unit returnAssignUnit = Jimple.v().newAssignStmt(returnRef, returnValue);
                    units.insertBefore(returnAssignUnit, returnUnit);
                    returnUnit.redirectJumpsToThisTo(returnAssignUnit);
                    returnAssignUnit.addAllTagsOf(returnUnit);
                    units.swapWith(returnUnit, Jimple.v().newReturnVoidStmt());
                }
            }
        }
    }

}
