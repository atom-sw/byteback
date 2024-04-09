package byteback.analysis.local.vimp.transformer.value;

import byteback.analysis.common.name.BBLibNames;
import byteback.analysis.local.common.transformer.body.BodyTransformer;
import byteback.analysis.local.vimp.syntax.value.QuantifierExpr;
import byteback.analysis.local.vimp.syntax.Vimp;
import byteback.common.function.Lazy;

import java.util.Iterator;

import soot.*;
import soot.jimple.*;
import soot.util.Chain;
import soot.util.HashChain;

/**
 * Transforms BBLib's quantifier expressions.
 *
 * @author paganma
 */
public class QuantifierValueTransformer extends BodyTransformer {

    private static final Lazy<QuantifierValueTransformer> instance = Lazy.from(QuantifierValueTransformer::new);

    private QuantifierValueTransformer() {
    }

    public static QuantifierValueTransformer v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof AssignStmt assignStmt) {
                if (assignStmt.getRightOp() instanceof InvokeExpr invokeExpr) {
                    final SootMethod invokedMethod = invokeExpr.getMethod();
                    final SootClass declaringClass = invokedMethod.getDeclaringClass();

                    if (BBLibNames.v().isBindingClass(declaringClass)) {
                        body.getUnits().remove(assignStmt);
                        continue;
                    }
                }
            }

            for (final ValueBox valueBox : unit.getUseBoxes()) {
                if (valueBox.getValue() instanceof final StaticInvokeExpr invokeExpr) {
                    final SootMethod method = invokeExpr.getMethod();
                    final SootClass clazz = method.getDeclaringClass();

                    if (BBLibNames.v().isQuantifierClass(clazz)) {
                        assert invokeExpr.getArgCount() == 2;
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
                            case BBLibNames.UNIVERSAL_QUANTIFIER_NAME ->
                                    Vimp.v().newLogicForallExpr(locals, expression);
                            case BBLibNames.EXISTENTIAL_QUANTIFIER_NAME ->
                                    Vimp.v().newLogicExistsExpr(locals, expression);
                            default ->
                                    throw new IllegalStateException("Unknown quantifier method " + method.getName());
                        };

                        valueBox.setValue(substitute);
                    }
                }
            }
        }
    }

}
