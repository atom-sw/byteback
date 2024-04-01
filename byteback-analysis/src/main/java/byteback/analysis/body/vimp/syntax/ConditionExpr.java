package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.VimpTypeInterpreter;
import byteback.analysis.scene.Types;
import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.jimple.Jimple;

/**
 * A ternary conditional expression.
 *
 * @author paganma
 */
public class ConditionExpr extends TernaryExpr {

    public ConditionExpr(final Value conditionValue, final Value thenValue, final Value elseValue) {
        super(Vimp.v().newArgBox(conditionValue), Vimp.v().newImmediateBox(thenValue), Vimp.v().newImmediateBox(elseValue));
    }

    @Override
    public Type getType() {
        return VimpTypeInterpreter.v().join(getOp2().getType(), getOp3().getType());
    }

    @Override
    public Object clone() {
        return new ConditionExpr(
                Jimple.cloneIfNecessary(getOp1()),
                Jimple.cloneIfNecessary(getOp2()),
                Jimple.cloneIfNecessary(getOp3())
        );
    }

    @Override
    public void toString(final UnitPrinter up) {
        up.literal("if ");
        getOp1().toString(up);
        up.literal(" then ");
        getOp2().toString(up);
        up.literal( " else ");
        getOp3().toString(up);
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof ConditionExpr conditionExpr
                && conditionExpr.getOp1().equivTo(getOp1())
                && conditionExpr.getOp2().equivTo(getOp2())
                && conditionExpr.getOp3().equivTo(getOp3());
    }

    @Override
    public int equivHashCode() {
        return 31 * getOp1().equivHashCode() + getOp2().equivHashCode() + getOp3().equivHashCode();
    }

}
