package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.declaration.member.method.body.transformer.GuardTransformer;
import soot.RefType;
import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.ConcreteRef;
import soot.jimple.IdentityRef;

import java.util.Collections;
import java.util.List;

/**
 * A concrete version of JCaughtExceptionRef, which can be assigned to. We use
 * this to model exceptional behavior using branches/guards.
 *
 * @author paganma
 * @see GuardTransformer
 */
public class ThrownRef implements IdentityRef, ConcreteRef, DefaultCaseValue {

	public ThrownRef() {
	}

	@Override
	public boolean equivTo(final Object object) {
		return object instanceof ThrownRef;
	}

	@Override
	public int equivHashCode() {
		return 34949;
	}

	@Override
	public List<ValueBox> getUseBoxes() {
		return Collections.emptyList();
	}

	@Override
	public Type getType() {
		return RefType.v("java.lang.Throwable");
	}

	@Override
	public Object clone() {
		return new ThrownRef();
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("@thrown");
	}

}
