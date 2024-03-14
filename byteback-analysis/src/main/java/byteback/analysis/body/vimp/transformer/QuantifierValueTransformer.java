package byteback.analysis.body.vimp.transformer;

import byteback.analysis.body.common.syntax.Body;
import byteback.analysis.body.common.syntax.expr.Value;
import byteback.analysis.body.common.syntax.expr.ValueBox;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.transformer.ValueTransformer;
import byteback.analysis.body.jimple.syntax.expr.CastExpr;
import byteback.analysis.body.jimple.syntax.expr.InvokeExpr;
import byteback.analysis.body.jimple.syntax.expr.Local;
import byteback.analysis.body.jimple.syntax.expr.StaticInvokeExpr;
import byteback.analysis.body.jimple.syntax.stmt.AssignStmt;
import byteback.analysis.body.vimp.syntax.ExistsExpr;
import byteback.analysis.body.vimp.syntax.ForallExpr;
import byteback.analysis.body.vimp.syntax.QuantifierExpr;
import byteback.analysis.common.naming.BBLibNames;
import byteback.analysis.common.syntax.Chain;
import byteback.analysis.common.syntax.HashChain;
import byteback.analysis.model.syntax.signature.MethodSignature;
import byteback.analysis.model.syntax.type.ClassType;
import byteback.common.function.Lazy;

import java.util.Iterator;

public class QuantifierValueTransformer extends ValueTransformer {

    private static final Lazy<QuantifierValueTransformer> instance = Lazy.from(QuantifierValueTransformer::new);

    private QuantifierValueTransformer() {
    }

    public static QuantifierValueTransformer v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final Iterator<Unit> unitIterator = body.getUnits().snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof AssignStmt assignStmt) {
                if (assignStmt.getRightOp() instanceof InvokeExpr invokeExpr) {
                    final MethodSignature methodSignature = invokeExpr.getSignature();
                    final ClassType classType = methodSignature.getDeclaringClassType();

                    if (BBLibNames.v().isBindingClass(classType)) {
                        body.getUnits().remove(assignStmt);
                        continue;
                    }
                }
            }

            for (final ValueBox valueBox : unit.getUseAndDefBoxes()) {
                transformValue(valueBox);
            }
        }
    }

    @Override
    public void transformValue(final ValueBox valueBox) {
        final Value value = valueBox.getValue();

        if (value instanceof StaticInvokeExpr invokeExpr) {
            assert invokeExpr.getArgCount() == 2;

            final MethodSignature methodSignature = invokeExpr.getSignature();
            final ClassType classType = methodSignature.getDeclaringClassType();

            if (BBLibNames.v().isQuantifierClass(classType)) {
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

                final QuantifierExpr substitute = switch (methodSignature.getName()) {
                    case BBLibNames.UNIVERSAL_QUANTIFIER_NAME -> new ForallExpr(locals, expression);
                    case BBLibNames.EXISTENTIAL_QUANTIFIER_NAME -> new ExistsExpr(locals, expression);
                    default -> throw new IllegalStateException("Unknown quantifier method " + methodSignature.getName());
                };

                valueBox.setValue(substitute);
            }
        }
    }

}
