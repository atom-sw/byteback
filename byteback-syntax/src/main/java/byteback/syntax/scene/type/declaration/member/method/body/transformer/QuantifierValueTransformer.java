package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.name.BBLibNames;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformationContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.QuantifierExpr;
import byteback.syntax.Vimp;
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

    private static final Lazy<QuantifierValueTransformer> INSTANCE = Lazy.from(QuantifierValueTransformer::new);

    private QuantifierValueTransformer() {
    }

    public static QuantifierValueTransformer v() {
        return INSTANCE.get();
    }

    @Override
    public void transformBody(final BodyTransformationContext bodyContext) {
        final Body body = bodyContext.getBody();
        final PatchingChain<Unit> units = body.getUnits();
        final Iterator<Unit> unitIterator = units.snapshotIterator();

        while (unitIterator.hasNext()) {
            final Unit unit = unitIterator.next();

            if (unit instanceof final AssignStmt assignStmt) {
                final Value rightOp = assignStmt.getRightOp();
                if (rightOp instanceof final InvokeExpr invokeExpr) {
                    final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();
                    final SootClass declaringClass = invokedMethodRef.getDeclaringClass();

                    if (BBLibNames.v().isBindingClass(declaringClass)) {
                        body.getUnits().remove(assignStmt);
                        continue;
                    }
                }
            }

            for (final ValueBox valueBox : unit.getUseBoxes()) {
                if (valueBox.getValue() instanceof final StaticInvokeExpr invokeExpr) {
                    final SootMethod invokedMethod = invokeExpr.getMethod();
                    final SootClass declaringClass = invokedMethod.getDeclaringClass();

                    if (BBLibNames.v().isQuantifierClass(declaringClass)) {
                        assert invokeExpr.getArgCount() == 2;
                        final Chain<Local> locals = new HashChain<>();
                        final Value bindingValue = invokeExpr.getArg(0);
                        final Value expressionValue = invokeExpr.getArg(1);

                        if (bindingValue instanceof final Local local) {
                            locals.add(local);
                        } else {
                            throw new RuntimeException("First argument of quantifier method must be a local variable");
                        }

                        final QuantifierExpr substitute = switch (invokedMethod.getName()) {
                            case BBLibNames.UNIVERSAL_QUANTIFIER_NAME ->
                                    Vimp.v().newForallExpr(locals, expressionValue);
                            case BBLibNames.EXISTENTIAL_QUANTIFIER_NAME ->
                                    Vimp.v().newLogicExistsExpr(locals, expressionValue);
                            default ->
                                    throw new IllegalStateException("Unknown quantifier method " + invokedMethod.getName());
                        };

                        valueBox.setValue(substitute);
                    }
                }
            }
        }
    }

}
