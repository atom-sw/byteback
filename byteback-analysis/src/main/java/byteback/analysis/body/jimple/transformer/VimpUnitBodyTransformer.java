package byteback.analysis.body.grimp.transformer;

import byteback.analysis.body.common.transformer.UnitTransformer;
import byteback.analysis.body.jimple.visitor.AbstractStmtSwitch;
import byteback.analysis.body.vimp.LogicStmt;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.common.namespace.BBLibNamespace;
import byteback.common.Lazy;
import soot.*;
import soot.grimp.GrimpBody;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleBody;

import java.util.Map;

public class VimpUnitBodyTransformer extends BodyTransformer implements UnitTransformer {

    private static final Lazy<VimpUnitBodyTransformer> instance = Lazy.from(VimpUnitBodyTransformer::new);

    private VimpUnitBodyTransformer() {
    }

    public static VimpUnitBodyTransformer v() {
        return instance.get();
    }

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        transformBody(body);
    }

    @Override
    public void transformUnit(final UnitBox unitBox) {
        final Unit unit = unitBox.getUnit();

        unit.apply(new AbstractStmtSwitch<>() {

            @Override
            public void caseInvokeStmt(final InvokeStmt invokeUnit) {
                final InvokeExpr value = invokeUnit.getInvokeExpr();
                final SootMethod method = value.getMethod();
                final SootClass declaringClass = method.getDeclaringClass();

                if (BBLibNamespace.isContractClass(declaringClass)) {
                    assert value.getArgCount() == 1;
                    final Value argument = value.getArg(0);

                    final LogicStmt newUnit = switch (method.getName()) {
                        case BBLibNamespace.ASSERTION_NAME -> Vimp.v().newAssertionStmt(argument);
                        case BBLibNamespace.ASSUMPTION_NAME -> Vimp.v().newAssumptionStmt(argument);
                        case BBLibNamespace.INVARIANT_NAME -> Vimp.v().newInvariantStmt(argument);
                        default -> throw new IllegalStateException("Unknown logic statement " + method.getName());
                    };

                    newUnit.addAllTagsOf(invokeUnit);
                    unitBox.setUnit(newUnit);
                }
            }

        });
    }

}
