package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.transformer.UnitTransformer;
import byteback.analysis.body.vimp.syntax.SpecificationStmt;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.common.namespace.BBLibNames;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;

/**
 * Converts BBLib's invoke statements into specification statements, corresponding to assertions, assumptions,
 * and invariants.
 *
 * @author paganma
 */
public class SpecificationStmtTransformer extends UnitTransformer {

    private static final Lazy<SpecificationStmtTransformer> instance = Lazy.from(SpecificationStmtTransformer::new);

    private SpecificationStmtTransformer() {
    }

    public static SpecificationStmtTransformer v() {
        return instance.get();
    }

    @Override
    public void transformUnit(final UnitBox unitBox) {
        final Unit unit = unitBox.getUnit();

        if (unit instanceof InvokeStmt invokeStmt) {
            final InvokeExpr invokeExpr = invokeStmt.getInvokeExpr();
            final SootMethodRef methodRef = invokeExpr.getMethodRef();
            final SootClass declaringClass = methodRef.getDeclaringClass();

            if (BBLibNames.v().isContractClass(declaringClass)) {
                assert invokeExpr.getArgCount() == 1;
                final Value argument = invokeExpr.getArg(0);

                final SpecificationStmt newUnit = switch (methodRef.getName()) {
                    case BBLibNames.ASSERTION_NAME -> Vimp.v().newAssertStmt(argument);
                    case BBLibNames.ASSUMPTION_NAME -> Vimp.v().newAssumeStmt(argument);
                    case BBLibNames.INVARIANT_NAME -> Vimp.v().newInvariantStmt(argument);
                    default -> throw new IllegalStateException("Unknown specification statement "
                            + methodRef.getName()
                            + ".");
                };

                unit.redirectJumpsToThisTo(newUnit);
                newUnit.addAllTagsOf(invokeStmt);
                unitBox.setUnit(newUnit);
            }
        }
    }

}
