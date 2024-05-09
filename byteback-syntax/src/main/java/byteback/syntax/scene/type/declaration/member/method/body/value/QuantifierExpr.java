package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.*;
import soot.grimp.Grimp;
import soot.jimple.Expr;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Base implementation for a quantifier expression.
 *
 * @author paganma
 */
public abstract class QuantifierExpr implements Expr {

	private Chain<Local> bindings;

	private Chain<Value> triggers;

	private final ValueBox conditionBox;

	public QuantifierExpr(final Chain<Local> bindings, final Chain<Value> triggers, final ValueBox conditionBox) {
		this.conditionBox = conditionBox;
		setBindings(bindings);
		setTriggers(triggers);
	}

	public QuantifierExpr(final Chain<Local> bindings, final ValueBox conditionBox) {
		this(bindings, new HashChain<>(), conditionBox);
	}

	public QuantifierExpr(final Chain<Local> bindings, final Chain<Value> triggers, final Value condition) {
		this(bindings, triggers, Grimp.v().newExprBox(condition));
	}

	public QuantifierExpr(final Chain<Local> bindings, final Value condition) {
		this(bindings, Grimp.v().newExprBox(condition));
	}

	public Value getValue() {
		return conditionBox.getValue();
	}

	public void setValue(final Value value) {
		this.conditionBox.setValue(value);
	}

	public Chain<Local> getBindings() {
		return bindings;
	}

	public final void setBindings(final Chain<Local> bindings) {
		if (bindings.isEmpty()) {
			throw new IllegalArgumentException("A Quantifier must have at least one binding local.");
		}

		this.bindings = bindings;
	}

	protected Chain<Local> cloneBindings() {
		final Chain<Local> locals = new HashChain<>();

		for (Local local : getBindings()) {
			locals.add((Local) local.clone());
		}

		return locals;
	}

	public Chain<Value> getTriggers() {
		return triggers;
	}

	public final void setTriggers(final Chain<Value> triggers) {
		this.triggers = triggers;
	}

	public abstract String getSymbol();

	@Override
	public void toString(final UnitPrinter printer) {
		final Iterator<Local> bindingsIterator = bindings.iterator();
		printer.literal("(");
		printer.literal(getSymbol());
		printer.literal(" ");

		while (bindingsIterator.hasNext()) {
			final Local local = bindingsIterator.next();
			printer.type(local.getType());
			printer.literal(" ");
			local.toString(printer);

			if (bindingsIterator.hasNext()) {
				printer.literal(", ");
			}
		}

		printer.literal(" :: ");

		if (!triggers.isEmpty()) {
			printer.literal("{ ");

			final Iterator<Value> triggersIterator = triggers.iterator();
			while (triggersIterator.hasNext()) {
				final Value trigger = triggersIterator.next();
				printer.type(trigger.getType());
				printer.literal(" ");
				trigger.toString(printer);

				if (bindingsIterator.hasNext()) {
					printer.literal(", ");
				}
			}

			printer.literal(" } ");
		}
		
		getValue().toString(printer);
		printer.literal(")");
	}

	@Override
	public List<ValueBox> getUseBoxes() {
		final var useBoxes = new ArrayList<ValueBox>();
		useBoxes.add(conditionBox);
		useBoxes.addAll(conditionBox.getValue().getUseBoxes());

		return useBoxes;
	}

	@Override
	public boolean equivTo(final Object object) {
		return object instanceof final QuantifierExpr quantifierExpr
				&& getValue().equivTo(quantifierExpr.getValue());
	}

	@Override
	public int equivHashCode() {
		int hashCode = 17 ^ getSymbol().hashCode();

		for (Local local : bindings) {
			hashCode += local.equivHashCode();
		}

		return hashCode + (getValue().equivHashCode() * 101);
	}

	@Override
	public abstract Object clone();

	@Override
	public Type getType() {
		return BooleanType.v();
	}

}
