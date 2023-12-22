package byteback.analysis.transformer;

import byteback.analysis.JimpleStmtSwitch;
import byteback.analysis.JimpleValueSwitch;
import byteback.analysis.Namespace;
import byteback.analysis.Vimp;
import byteback.analysis.util.SootTypes;
import byteback.analysis.vimp.AssertionStmt;
import byteback.analysis.vimp.AssumptionStmt;
import byteback.analysis.vimp.InvariantStmt;
import byteback.analysis.vimp.LogicConstant;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import soot.Body;
import soot.BodyTransformer;
import soot.BooleanType;
import soot.SootMethod;
import soot.Type;
import soot.TypeSwitch;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Grimp;
import soot.grimp.GrimpBody;
import soot.jimple.AndExpr;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.EqExpr;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.IfStmt;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LtExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.OrExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.UnopExpr;
import soot.jimple.XorExpr;

public class LogicValueTransformer extends BodyTransformer implements UnitTransformer {

	public static class LogicValueSwitch extends JimpleValueSwitch<Value> {

		private interface BinaryConstructor extends BiFunction<ValueBox, ValueBox, Value> {
		}

		private interface UnaryConstructor extends Function<ValueBox, Value> {
		}

		public final Type expectedType;

		public final ValueBox resultBox;

		public LogicValueSwitch(final Type expectedType, final ValueBox resultBox) {
			this.expectedType = expectedType;
			this.resultBox = resultBox;
		}

		public void setValue(final Value value) {
			if (expectedType != value.getType()) {
				resultBox.setValue(Grimp.v().newCastExpr(value, expectedType));
			} else {
				resultBox.setValue(value);
			}
		}

		public void setUnaryValue(final UnaryConstructor constructor, final Type expectedType, final UnopExpr value) {
			final ValueBox operandBox = value.getOpBox();
			new LogicValueSwitch(expectedType, operandBox).visit(operandBox.getValue());
		}

		public void setBinaryValue(final BinaryConstructor constructor, final Type expectedType,
				final BinopExpr value) {
			final ValueBox leftBox = value.getOp1Box();
			final ValueBox rightBox = value.getOp2Box();
			setValue(constructor.apply(leftBox, rightBox));

			new LogicValueSwitch(expectedType, leftBox).visit(leftBox.getValue());
			new LogicValueSwitch(expectedType, rightBox).visit(rightBox.getValue());
		}

		public void setBinaryValue(final BinaryConstructor constructor, final BinopExpr value) {
			setBinaryValue(constructor, SootTypes.join(value.getOp1().getType(), value.getOp2().getType()), value);
		}

		@Override
		public void caseIntConstant(final IntConstant constant) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType type) {
					setValue(LogicConstant.v(constant.value > 0));
				}

			});
		}

		@Override
		public void caseAndExpr(final AndExpr value) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType type) {
					setBinaryValue(Vimp.v()::newLogicAndExpr, type, value);
				}

			});
		}

		@Override
		public void caseOrExpr(final OrExpr value) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType type) {
					setBinaryValue(Vimp.v()::newLogicOrExpr, type, value);
				}

			});
		}

		@Override
		public void caseXorExpr(final XorExpr value) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType type) {
					setBinaryValue(Vimp.v()::newLogicXorExpr, type, value);
				}

			});
		}

		@Override
		public void caseNegExpr(final NegExpr value) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType type) {
					setUnaryValue(Vimp.v()::newLogicNotExpr, type, value);
				}

			});
		}

		@Override
		public void caseGtExpr(final GtExpr value) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType $) {
					setBinaryValue(Vimp.v()::newGtExpr, value);
				}

			});
		}

		@Override
		public void caseGeExpr(final GeExpr value) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType $) {
					setBinaryValue(Vimp.v()::newGeExpr, value);
				}

			});
		}

		@Override
		public void caseLtExpr(final LtExpr value) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType $) {
					setBinaryValue(Vimp.v()::newLtExpr, value);
				}

			});
		}

		@Override
		public void caseLeExpr(final LeExpr value) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType $) {
					setBinaryValue(Vimp.v()::newLeExpr, value);
				}

			});
		}

		@Override
		public void caseEqExpr(final EqExpr value) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType $) {
					setBinaryValue(Vimp.v()::newEqExpr, value);
				}

			});
		}

		@Override
		public void caseNeExpr(final NeExpr value) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType $) {
					setBinaryValue(Vimp.v()::newNeExpr, value);
				}

			});
		}

		@Override
		public void caseInstanceOfExpr(final InstanceOfExpr value) {
			expectedType.apply(new TypeSwitch<>() {

				@Override
				public void caseBooleanType(final BooleanType $) {
					setValue(Vimp.v().newInstanceOfExpr(value.getOp(), value.getCheckType()));
				}

			});
		}

		public void setSpecial(final InvokeExpr specialInvokeExpr) {
			final SootMethod method = specialInvokeExpr.getMethod();

			if (method.getName().equals("old")) {
				setValue(Vimp.v().newOldExpr(specialInvokeExpr.getArg(0)));
			}
		}

		@Override
		public void caseInvokeExpr(final InvokeExpr value) {
			for (int i = 0; i < value.getArgCount(); ++i) {
				final ValueBox argumentBox = value.getArgBox(i);
				new LogicValueSwitch(value.getMethodRef().getParameterType(i), argumentBox)
						.visit(argumentBox.getValue());
			}

			if (Namespace.isSpecialClass(value.getMethod().getDeclaringClass())) {
				setSpecial(value);
			}
		}

		@Override
		public void caseCmpExpr(final CmpExpr $) {
		}

		@Override
		public void caseCmplExpr(final CmplExpr $) {
		}

		@Override
		public void caseCmpgExpr(final CmpgExpr $) {
		}

		@Override
		public void caseLengthExpr(final LengthExpr $) {
		}

		@Override
		public void caseDefault(final Value defaultValue) {
			setValue(defaultValue);

			if (defaultValue instanceof BinopExpr value) {
				final ValueBox leftBox = value.getOp1Box();
				final ValueBox rightBox = value.getOp2Box();
				new LogicValueSwitch(expectedType, leftBox).visit(leftBox.getValue());
				new LogicValueSwitch(expectedType, rightBox).visit(rightBox.getValue());
			}

			if (defaultValue instanceof UnopExpr value) {
				final ValueBox operandBox = value.getOpBox();
				new LogicValueSwitch(expectedType, operandBox).visit(operandBox.getValue());
			}
		}

		@Override
		public Value result() {
			return resultBox.getValue();
		}

	}

	public final Type returnType;

	public LogicValueTransformer(final Type returnType) {
		this.returnType = returnType;
	}

	@Override
	protected void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		if (body instanceof GrimpBody grimpBody) {
			transformBody(grimpBody);
		} else {
			throw new IllegalArgumentException("Can only transform Jimple");
		}
	}

	public void transformValue(final ValueBox valueBox) {
		new LogicValueSwitch(BooleanType.v(), valueBox).visit(valueBox.getValue());
	}

	public void transformUnit(final Unit unit) {
		unit.apply(new JimpleStmtSwitch<>() {

			@Override
			public void caseAssignStmt(final AssignStmt unit) {
				final Type type = unit.getLeftOp().getType();
				final ValueBox rightBox = unit.getRightOpBox();
				new LogicValueSwitch(type, unit.getRightOpBox()).visit(rightBox.getValue());
			}

			@Override
			public void caseIfStmt(final IfStmt unit) {
				final ValueBox conditionBox = unit.getConditionBox();
				new LogicValueSwitch(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
			}

			@Override
			public void caseReturnStmt(final ReturnStmt unit) {
				final ValueBox returnBox = unit.getOpBox();
				new LogicValueSwitch(returnType, returnBox).visit(returnBox.getValue());
			}

			@Override
			public void caseAssertionStmt(final AssertionStmt unit) {
				final ValueBox conditionBox = unit.getConditionBox();
				new LogicValueSwitch(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
			}

			@Override
			public void caseInvariantStmt(final InvariantStmt unit) {
				final ValueBox conditionBox = unit.getConditionBox();
				new LogicValueSwitch(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
			}

			@Override
			public void caseAssumptionStmt(final AssumptionStmt unit) {
				final ValueBox conditionBox = unit.getConditionBox();
				new LogicValueSwitch(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
			}

		});
	}

	@Override
	public void transformUnit(final UnitBox unitBox) {
		transformUnit(unitBox.getUnit());
	}

}
