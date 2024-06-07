package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer;

import byteback.common.function.Lazy;
import soot.*;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.*;

/**
 * @author paganma
 */
public class InvokeAssigner extends UnitTransformer {

	private static final Lazy<InvokeAssigner> INSTANCE = Lazy.from(InvokeAssigner::new);

	private InvokeAssigner() {
	}

	public static InvokeAssigner v() {
		return INSTANCE.get();
	}

	@Override
	public void transformUnit(final SootMethod sootMethod, final Body body, final UnitBox unitBox) {
		final Unit unit = unitBox.getUnit();

		if (unit instanceof final InvokeStmt invokeStmt) {
			final InvokeExpr invokeExpr = invokeStmt.getInvokeExpr();
			final SootMethodRef invokedMethodRef = invokeExpr.getMethodRef();
			final Type returnType = invokedMethodRef.getReturnType();

			if (returnType != VoidType.v()) {
				final LocalGenerator localGenerator = new DefaultLocalGenerator(body);
				final Local local = localGenerator.generateLocal(returnType);
				final AssignStmt assignStmt = Jimple.v().newAssignStmt(local, invokeExpr);
				unitBox.setUnit(assignStmt);
			}
		}
	}

}
