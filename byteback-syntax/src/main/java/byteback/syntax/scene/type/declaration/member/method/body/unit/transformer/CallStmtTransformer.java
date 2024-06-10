package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer;

import java.util.ArrayList;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.unit.CallStmt;
import byteback.syntax.scene.type.declaration.member.method.body.value.CallExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.PreludeNewRef;
import soot.Body;
import soot.RefType;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewExpr;

public class CallStmtTransformer extends UnitTransformer {

	private static final Lazy<CallStmtTransformer> INSTANCE = Lazy.from(CallStmtTransformer::new);

	public static CallStmtTransformer v() {
		return INSTANCE.get();
	}

	public void transformUnit(final SootMethod sootMethod, final Body body, final UnitBox unitBox) {
		final Unit unit = unitBox.getUnit();
		final SootMethodRef methodRef;
		final ArrayList<Value> targets;
		final ArrayList<Value> arguments;

		if (unit instanceof final AssignStmt assignStmt) {
			final Value assignedValue = assignStmt.getRightOp();

			if (assignedValue instanceof InvokeExpr || assignedValue instanceof NewExpr) {
				targets = new ArrayList<>();
				arguments = new ArrayList<>();
				targets.add(assignStmt.getLeftOp());
			} else {
				return;
			}

			if (assignedValue instanceof final InvokeExpr invokeExpr
					&& !(invokeExpr instanceof CallExpr)) {
				targets.add(Vimp.v().newThrownRef());

				methodRef = invokeExpr.getMethodRef();
				arguments.addAll(invokeExpr.getArgs());

				if (invokeExpr instanceof final InstanceInvokeExpr instanceInvokeExpr) {
					arguments.add(0, instanceInvokeExpr.getBase());
				}
			} else if (assignedValue instanceof final NewExpr newExpr) {
				targets.add(Vimp.v().newThrownRef());

				methodRef = PreludeNewRef.v();
				arguments.add(Vimp.v().newTypeConstant((RefType) newExpr.getType()));
			} else {
				return;
			}
		} else if (unit instanceof final InvokeStmt invokeStmt) {
			final InvokeExpr invokeExpr = invokeStmt.getInvokeExpr();
			methodRef = invokeExpr.getMethodRef();
			arguments = new ArrayList<>(invokeExpr.getArgs());
			targets = new ArrayList<>();
			targets.add(Vimp.v().newThrownRef());

			if (invokeExpr instanceof InstanceInvokeExpr instanceInvokeExpr) {
				arguments.add(0, instanceInvokeExpr.getBase());
			}
		} else {
			return;
		}

		final CallExpr callExpr = Vimp.v().newCallExpr(methodRef, arguments);
		final CallStmt callStmt = Vimp.v().newCallStmt(targets, callExpr);

		unitBox.setUnit(callStmt);
	}

}
