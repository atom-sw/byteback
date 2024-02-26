package byteback.analysis.body.grimp.transformer;

import byteback.analysis.body.common.transformer.UnitTransformer;
import byteback.analysis.body.grimp.visitor.AbstractGrimpValueSwitchWithInvokeCase;
import byteback.analysis.body.vimp.*;
import byteback.analysis.body.vimp.visitor.AbstractVimpStmtSwitch;
import byteback.analysis.common.namespace.BBLibNamespace;
import byteback.analysis.scene.SootTypes;
import byteback.common.Lazy;
import soot.*;
import soot.jimple.*;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class VimpValueBodyTransformer extends BodyTransformer {

    private static final Lazy<VimpValueBodyTransformer> instance = Lazy.from(VimpValueBodyTransformer::new);

    private VimpValueBodyTransformer() {
    }

    public static VimpValueBodyTransformer v() {
        return instance.get();
    }

    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        new VimpValueTransformer(b.getMethod().getReturnType()).transform(b);
    }

    public static class VimpValueTransformer extends BodyTransformer implements UnitTransformer {
        public final Type returnType;

        public VimpValueTransformer(final Type returnType) {
            this.returnType = returnType;
        }

        @Override
        protected void internalTransform(final Body b, final String phaseName, final Map<String, String> options) {
            transformBody(b);
        }

        public void transformValue(final ValueBox valueBox) {
            new LogicValueTransformer(BooleanType.v(), valueBox).visit(valueBox.getValue());
        }

        public void transformUnit(final Unit unit) {
            unit.apply(new AbstractVimpStmtSwitch<>() {

                @Override
                public void caseAssignStmt(final AssignStmt unit) {
                    final Type type = unit.getLeftOp().getType();
                    final ValueBox rightBox = unit.getRightOpBox();
                    new LogicValueTransformer(type, unit.getRightOpBox()).visit(rightBox.getValue());
                }

                @Override
                public void caseIfStmt(final IfStmt unit) {
                    final ValueBox conditionBox = unit.getConditionBox();
                    new LogicValueTransformer(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
                }

                @Override
                public void caseReturnStmt(final ReturnStmt unit) {
                    final ValueBox returnBox = unit.getOpBox();
                    new LogicValueTransformer(returnType, returnBox).visit(returnBox.getValue());
                }

                @Override
                public void caseAssertionStmt(final AssertionStmt unit) {
                    final ValueBox conditionBox = unit.getConditionBox();
                    new LogicValueTransformer(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
                }

                @Override
                public void caseInvariantStmt(final InvariantStmt unit) {
                    final ValueBox conditionBox = unit.getConditionBox();
                    new LogicValueTransformer(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
                }

                @Override
                public void caseAssumptionStmt(final AssumptionStmt unit) {
                    final ValueBox conditionBox = unit.getConditionBox();
                    new LogicValueTransformer(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
                }

            });
        }

        @Override
        public void transformUnit(final UnitBox unitBox) {
            transformUnit(unitBox.getUnit());
        }

        public static class LogicValueTransformer extends AbstractGrimpValueSwitchWithInvokeCase<Value> {

            public final Type expectedType;
            public final ValueBox resultBox;

            public LogicValueTransformer(final Type expectedType, final ValueBox resultBox) {
                this.expectedType = expectedType;
                this.resultBox = resultBox;
            }

            public void setValue(final Value value) {
                if (expectedType != value.getType()) {
                    resultBox.setValue(Jimple.v().newCastExpr(value, expectedType));
                } else {
                    resultBox.setValue(value);
                }
            }

            public void setUnaryValue(final UnaryConstructor constructor, final Type expectedType, final UnopExpr value) {
                final ValueBox operandBox = value.getOpBox();
                setValue(constructor.apply(operandBox));
                new LogicValueTransformer(expectedType, operandBox).visit(operandBox.getValue());
            }

            public void setBinaryValue(final BinaryConstructor constructor, final Type expectedType,
                                       final BinopExpr value) {
                final ValueBox leftBox = value.getOp1Box();
                final ValueBox rightBox = value.getOp2Box();
                setValue(constructor.apply(leftBox, rightBox));
                new LogicValueTransformer(expectedType, leftBox).visit(leftBox.getValue());
                new LogicValueTransformer(expectedType, rightBox).visit(rightBox.getValue());
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
            public void caseInvokeExpr(final InvokeExpr invokeExpr) {
                for (int i = 0; i < invokeExpr.getArgCount(); ++i) {
                    final ValueBox argumentBox = invokeExpr.getArgBox(i);
                    new LogicValueTransformer(invokeExpr.getMethodRef().getParameterType(i), argumentBox)
                            .visit(argumentBox.getValue());
                }

                final SootMethod invokedMethod = invokeExpr.getMethod();

                if (BBLibNamespace.isSpecialClass(invokedMethod.getDeclaringClass())) {
                    setSpecial(invokeExpr);
                }
            }

            @Override
            public void caseCmpExpr(final CmpExpr $) {}

            @Override
            public void caseCmplExpr(final CmplExpr $) {}

            @Override
            public void caseCmpgExpr(final CmpgExpr $) {}

            @Override
            public void defaultCase(final Value defaultValue) {
                setValue(defaultValue);

                if (defaultValue instanceof BinopExpr value) {
                    final ValueBox leftBox = value.getOp1Box();
                    final ValueBox rightBox = value.getOp2Box();
                    new LogicValueTransformer(expectedType, leftBox).visit(leftBox.getValue());
                    new LogicValueTransformer(expectedType, rightBox).visit(rightBox.getValue());
                }

                if (defaultValue instanceof UnopExpr value) {
                    final ValueBox operandBox = value.getOpBox();
                    new LogicValueTransformer(expectedType, operandBox).visit(operandBox.getValue());
                }
            }

            @Override
            public Value getResult() {
                return resultBox.getValue();
            }

            private interface BinaryConstructor extends BiFunction<ValueBox, ValueBox, Value> {
            }

            private interface UnaryConstructor extends Function<ValueBox, Value> {
            }

        }

    }

}
