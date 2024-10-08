package byteback.syntax.scene.type.declaration.member.method.body.unit.printer;

import soot.*;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.IdentityRef;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;

/**
 * A Unit printer that does not require a Body as the outer context. Can be used
 * to implement `toString` in syntax
 * components.
 *
 * @author paganma
 */
public class InlineUnitPrinter extends AbstractUnitPrinter {

	public InlineUnitPrinter() {
		// Starts with an empty indent.
		this.indent = "";
	}

	@Override
	public void literal(final String string) {
		output.append(string);
	}

	@Override
	public void type(final Type type) {
		output.append(type == null ? "<null>" : type.toQuotedString());
	}

	@Override
	public void methodRef(final SootMethodRef methodRef) {
		output.append(methodRef.getSignature());
	}

	@Override
	public void fieldRef(final SootFieldRef fieldRef) {
		output.append(fieldRef.getSignature());
	}

	@Override
	public void unitRef(final Unit unit, final boolean branchTarget) {
		unit.toString(this);
	}

	@Override
	public void identityRef(final IdentityRef identityRef) {
		if (identityRef instanceof ThisRef) {
			literal("@this: ");
			type(identityRef.getType());
		} else if (identityRef instanceof final ParameterRef parameterRef) {
			literal("@parameter" + parameterRef.getIndex() + ": ");
			type(identityRef.getType());
		} else if (identityRef instanceof CaughtExceptionRef) {
			literal("@caughtexception");
		} else {
			throw new IllegalArgumentException("Unknown IdentityRef: " + identityRef);
		}
	}

}
