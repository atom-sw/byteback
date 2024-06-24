package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.unit.SpecificationStmt;
import byteback.syntax.scene.type.declaration.member.method.body.value.LogicConstant;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
import soot.*;
import soot.jimple.*;

/**
 * Introduces logic constants (true, false) wherever appropriate.
 *
 * @author paganma
 */
public class LogicConstantTransformer extends UnitTransformer {

	private static final Lazy<LogicConstantTransformer> INSTANCE = Lazy.from(LogicConstantTransformer::new);

	private LogicConstantTransformer() {
	}

	public static LogicConstantTransformer v() {
		return INSTANCE.get();
	}

	@Override
	public void transformUnit(final SootMethod sootMethod, final Body body, final UnitBox unitBox) {
		final Unit unit = unitBox.getUnit();

		if (unit instanceof final AssignStmt assignStmt) {
			final Type expectedType = assignStmt.getLeftOp().getType();
			final ValueBox rightValueBox = assignStmt.getRightOpBox();
			if (expectedType == BooleanType.v()) {
				transformValueOfType(rightValueBox);
			}
		} else if (unit instanceof final IfStmt ifStmt) {
			final ValueBox conditionValueBox = ifStmt.getConditionBox();
			transformValueOfType(conditionValueBox);
		} else if (unit instanceof final InvokeStmt invokeStmt) {
			transformValueOfType(invokeStmt.getInvokeExprBox());
		} else if (unit instanceof final SpecificationStmt specificationStmt) {
			final ValueBox conditionValueBox = specificationStmt.getConditionBox();
			transformValueOfType(conditionValueBox);
		} else if (unit instanceof final ReturnStmt returnStmt) {
			if (sootMethod.getReturnType() == BooleanType.v()) {
				transformValueOfType(returnStmt.getOpBox());
			}
		}
	}

	public void transformValueOfType(final ValueBox valueBox) {
		final Value value = valueBox.getValue();

		if (value instanceof final BinopExpr binopExpr) {
			if (binopExpr instanceof AndExpr || binopExpr instanceof OrExpr) {
				transformValueOfType(binopExpr.getOp1Box());
				transformValueOfType(binopExpr.getOp2Box());
			}
		} else if (value instanceof final InvokeExpr invokeExpr) {
			for (int i = 0; i < invokeExpr.getArgCount(); ++i) {
				final ValueBox argBox = invokeExpr.getArgBox(i);
				final Type argType = invokeExpr.getMethodRef().getParameterType(i);
				if (argType == BooleanType.v()) {
					transformValueOfType(argBox);
				}
			}
		} else if (value instanceof final IntConstant intConstant) {
			valueBox.setValue(LogicConstant.v(intConstant.value > 0));
		}
	}

}
