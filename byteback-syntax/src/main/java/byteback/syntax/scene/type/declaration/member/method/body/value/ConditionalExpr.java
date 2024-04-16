package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.Vimp;
import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.jimple.Jimple;

/**
 * A ternary conditional expression.
 *
 * @author paganma
 */
public class ConditionalExpr extends TernaryExpr {

    public ConditionalExpr(final Value conditionValue, final Value thenValue, final Value elseValue) {
        super(Vimp.v().newArgBox(conditionValue), Vimp.v().newImmediateBox(thenValue), Vimp.v().newImmediateBox(elseValue));
    }

    @Override
    public Type getType() {
        return VimpTypeInterpreter.v().join(getOp2().getType(), getOp3().getType());
    }

    @Override
    public Object clone() {
        return new ConditionalExpr(
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
        return object instanceof ConditionalExpr conditionalExpr
                && conditionalExpr.getOp1().equivTo(getOp1())
                && conditionalExpr.getOp2().equivTo(getOp2())
                && conditionalExpr.getOp3().equivTo(getOp3());
    }

    @Override
    public int equivHashCode() {
        return 31 * getOp1().equivHashCode() + getOp2().equivHashCode() + getOp3().equivHashCode();
    }

}
