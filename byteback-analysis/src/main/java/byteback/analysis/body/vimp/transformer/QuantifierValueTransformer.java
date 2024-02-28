package byteback.analysis.body.vimp.transformer;

import byteback.analysis.common.namespace.BBLibNamespace;
import byteback.analysis.body.vimp.QuantifierExpr;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.common.transformer.ValueTransformer;
import byteback.common.Lazy;

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.*;
import soot.util.Chain;
import soot.util.HashChain;

public class QuantifierValueTransformer extends ValueTransformer {

    private static final Lazy<QuantifierValueTransformer> instance = Lazy.from(QuantifierValueTransformer::new);

    private QuantifierValueTransformer() {
    }

    public static QuantifierValueTransformer v() {
        return instance.get();
    }

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof AssignStmt assignStmt) {
                if (assignStmt.getRightOp() instanceof InvokeExpr invokeExpr) {
                    final SootMethod method = invokeExpr.getMethod();
                    final SootClass clazz = method.getDeclaringClass();

                    if (BBLibNamespace.isBindingClass(clazz)) {
                        body.getUnits().remove(assignStmt);
                    }
                }
            } else {
                for (final ValueBox vbox : unit.getUseAndDefBoxes()) {
                    transformValue(vbox);
                }
            }
        }
    }

    @Override
    public void transformValue(final ValueBox valueBox) {
        final Value value = valueBox.getValue();

        value.apply(new AbstractJimpleValueSwitch<>() {

            @Override
            public void caseStaticInvokeExpr(final StaticInvokeExpr invokeExpr) {
                assert invokeExpr.getArgCount() == 2;

                final SootMethod method = invokeExpr.getMethod();
                final SootClass clazz = method.getDeclaringClass();

                if (BBLibNamespace.isQuantifierClass(clazz)) {
                    final Chain<Local> locals = new HashChain<>();
                    final Value expression;
                    Value variable = invokeExpr.getArg(0);

                    while (variable instanceof CastExpr castExpr) {
                        variable = castExpr.getOp();
                    }

                    if (variable instanceof Local local) {
                        locals.add(local);
                        expression = invokeExpr.getArg(1);
                    } else {
                        throw new RuntimeException("First argument of quantifier method must be a local variable");
                    }

                    final QuantifierExpr substitute = switch (method.getName()) {
                        case BBLibNamespace.UNIVERSAL_QUANTIFIER_NAME ->
                                Vimp.v().newLogicForallExpr(locals, expression);
                        case BBLibNamespace.EXISTENTIAL_QUANTIFIER_NAME ->
                                Vimp.v().newLogicExistsExpr(locals, expression);
                        default -> throw new IllegalStateException("Unknown quantifier method " + method.getName());
                    };

                    valueBox.setValue(substitute);
                }

            }

        });
    }

}
