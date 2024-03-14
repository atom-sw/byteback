package byteback.analysis.body.jimple.transformer;

import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.syntax.stmt.UnitBox;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.transformer.UnitTransformer;
import byteback.analysis.body.jimple.syntax.stmt.InvokeStmt;
import byteback.analysis.body.vimp.syntax.AssertStmt;
import byteback.analysis.body.vimp.syntax.AssumeStmt;
import byteback.analysis.body.vimp.syntax.InvariantStmt;
import byteback.analysis.body.vimp.syntax.SpecificationStmt;
import byteback.analysis.common.naming.BBLibNames;
import byteback.analysis.model.syntax.signature.MethodSignature;
import byteback.analysis.model.syntax.type.ClassType;
import byteback.common.function.Lazy;
import byteback.analysis.body.jimple.syntax.expr.InvokeExpr;

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

        if (unit instanceof InvokeStmt invokeStmt) {
            final InvokeExpr value = invokeStmt.getInvokeExpr();
            final MethodSignature methodSignature = value.getSignature();
            final ClassType declaringClassType = methodSignature.getDeclaringClassType();

            if (BBLibNames.v().isContractClass(declaringClassType)) {
                assert value.getArgCount() == 1;
                final Value argument = value.getArg(0);

                final SpecificationStmt newUnit = switch (methodSignature.getName()) {
                    case BBLibNames.ASSERTION_NAME -> new AssertStmt(argument);
                    case BBLibNames.ASSUMPTION_NAME -> new AssumeStmt(argument);
                    case BBLibNames.INVARIANT_NAME -> new InvariantStmt(argument);
                    default -> throw new IllegalStateException("Unknown specification statement " + methodSignature.getName());
                };

                unitBox.setUnit(newUnit);
            }
        }
    }
}
