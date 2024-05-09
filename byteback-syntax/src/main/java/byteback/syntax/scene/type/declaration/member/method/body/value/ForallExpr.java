package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Local;
import soot.Value;
import soot.jimple.Jimple;
import soot.util.Chain;

/**
 * Universal quantifier expression.
 *
 * @author paganma
 */
public class ForallExpr extends QuantifierExpr implements DefaultCaseValue {

	public ForallExpr(final Chain<Local> bindings, final Value value) {
		super(bindings, value);
	}

	public ForallExpr(final Chain<Local> bindings, final Chain<Value> triggers, final Value value) {
		super(bindings, triggers, value);
	}

	@Override
	public String getSymbol() {
		return "forall";
	}

	@Override
	public ForallExpr clone() {
		return new ForallExpr(cloneBindings(), Jimple.cloneIfNecessary(getValue()));
	}

}
