package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer;

import soot.*;
import soot.jimple.*;

/**
 * @author paganma
 */
public class InvokeAssigner extends UnitTransformer {

	public final LocalGenerator localGenerator;

	public InvokeAssigner(final LocalGenerator localGenerator) {
		this.localGenerator = localGenerator;
	}

	@Override
	public void transformUnit(final UnitBox unitBox) {
		final Unit unit = unitBox.getUnit();

		if (unit instanceof final InvokeStmt invokeStmt) {
			final InvokeExpr invokeExpr = invokeStmt.getInvokeExpr();
			final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();
			final Type returnType = invokedMethodRef.getReturnType();

			if (returnType != VoidType.v()) {
				final Local local = localGenerator.generateLocal(returnType);
				final AssignStmt assignStmt = Jimple.v().newAssignStmt(local, invokeExpr);
				unitBox.setUnit(assignStmt);
			}
		}
	}

}
