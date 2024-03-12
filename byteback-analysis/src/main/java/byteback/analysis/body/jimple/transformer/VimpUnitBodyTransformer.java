package byteback.analysis.body.jimple.transformer;

import byteback.analysis.body.common.transformer.UnitTransformer;
import byteback.analysis.body.common.visitor.AbstractStmtSwitch;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.syntax.SpecificationStmt;
import byteback.analysis.common.namespace.BBLibNames;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;

/**
 * Converts BBLib's invoke statements into specification statements.
 *
 * @author paganma
 */
public class VimpUnitBodyTransformer extends UnitTransformer {

    private static final Lazy<VimpUnitBodyTransformer> instance = Lazy.from(VimpUnitBodyTransformer::new);

    private VimpUnitBodyTransformer() {
    }

    public static VimpUnitBodyTransformer v() {
        return instance.get();
    }

    @Override
    public void transformUnit(final UnitBox unitBox) {
        final Unit unit = unitBox.getUnit();

        unit.apply(new AbstractStmtSwitch<>() {

            @Override
            public void caseInvokeStmt(final InvokeStmt invokeStmt) {
                final InvokeExpr value = invokeStmt.getInvokeExpr();
                final SootMethod method = value.getMethod();
                final ClassModel declaringClass = method.getDeclaringClass();

                if (BBLibNames.v().isContractClass(declaringClass)) {
                    assert value.getArgCount() == 1;
                    final Value argument = value.getArg(0);

                    final SpecificationStmt newUnit = switch (method.getName()) {
                        case BBLibNames.ASSERTION_NAME -> Vimp.v().newAssertStmt(argument);
                        case BBLibNames.ASSUMPTION_NAME -> Vimp.v().newAssumeStmt(argument);
                        case BBLibNames.INVARIANT_NAME -> Vimp.v().newInvariantStmt(argument);
                        default ->
                                throw new IllegalStateException("Unknown specification statement " + method.getName());
                    };

                    newUnit.addAllTagsOf(invokeStmt);
                    unitBox.setUnit(newUnit);
                }
            }

        });
    }

}
