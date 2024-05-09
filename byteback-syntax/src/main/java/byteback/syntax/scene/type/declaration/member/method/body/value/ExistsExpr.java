package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Local;
import soot.Value;
import soot.jimple.Jimple;
import soot.util.Chain;

/**
 * Existential quantification expression.
 *
 * @author paganma
 */
public class ExistsExpr extends QuantifierExpr implements DefaultCaseValue {

	public ExistsExpr(final Chain<Local> bindings, final Value value) {
		super(bindings, value);
	}

	public ExistsExpr(final Chain<Local> bindings, final Chain<Value> triggers, final Value value) {
		super(bindings, triggers, value);
	}

	@Override
	public String getSymbol() {
		return "exists";
	}

	@Override
	public ExistsExpr clone() {
		return new ExistsExpr(cloneBindings(), Jimple.cloneIfNecessary(getValue()));
	}

}
