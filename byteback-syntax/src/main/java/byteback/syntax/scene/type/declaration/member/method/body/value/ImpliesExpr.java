package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractIntBinopExpr;
import soot.jimple.internal.ImmediateBox;

/**
 * Boolean implication expression.
 *
 * @author paganma
 */
public class ImpliesExpr extends AbstractIntBinopExpr implements DefaultCaseValue, ConditionExpr {

    public ImpliesExpr(final Value op1, final Value op2) {
        super(Jimple.v().newImmediateBox(op1), Jimple.v().newImmediateBox(op2));
    }

    @Override
    public String getSymbol() {
        return " -> ";
    }

    @Override
    public ImpliesExpr clone() {
        return new ImpliesExpr(Jimple.cloneIfNecessary(getOp1()), Jimple.cloneIfNecessary(getOp2()));
    }

}
