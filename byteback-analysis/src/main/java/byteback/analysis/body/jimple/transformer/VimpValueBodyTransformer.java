package byteback.analysis.body.jimple.transformer;

import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.common.transformer.ValueTransformer;
import byteback.analysis.body.jimple.visitor.AbstractJimpleValueSwitch;
import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.vimp.syntax.*;
import byteback.analysis.body.vimp.visitor.AbstractVimpStmtSwitch;
import byteback.analysis.scene.Types;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.*;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Converts bytecode boolean expressions into logic boolean expressions. Unlike bytecode boolean expressions, logic
 * boolean expressions cannot evaluate to an integer. If the result of a boolean expression needs to be used in an int
 * context, this transformation will cast it explicitly.
 * @author paganma
 */
public class VimpValueBodyTransformer extends BodyTransformer {

    private static final Lazy<VimpValueBodyTransformer> instance = Lazy.from(VimpValueBodyTransformer::new);

    private VimpValueBodyTransformer() {
    }

    public static VimpValueBodyTransformer v() {
        return instance.get();
    }

    @Override
    public void transformBody(final Body body) {
        final Type returnType = body.getMethod().getReturnType();
        new VimpValueTransformer(returnType).transform(body);
    }

    public static class VimpValueTransformer extends ValueTransformer {
        public final Type returnType;

        public VimpValueTransformer(final Type returnType) {
            this.returnType = returnType;
        }

        @Override
        public void transformValue(final ValueBox valueBox) {
            new LogicValueTransformer(BooleanType.v(), valueBox).visit(valueBox.getValue());
        }

        @Override
        public void transformBody(final Body body) {
            for (final Unit unit : body.getUnits()) {
                transformUnit(unit);
            }
        }

        @Override
        public void transformUnit(final UnitBox unitBox) {
            transformUnit(unitBox.getUnit());
        }

        public void transformUnit(final Unit unit) {
            unit.apply(new AbstractVimpStmtSwitch<>() {

                @Override
                public void caseAssignStmt(final AssignStmt assignStmt) {
                    final Type type = assignStmt.getLeftOp().getType();
                    final ValueBox rightBox = assignStmt.getRightOpBox();
                    new LogicValueTransformer(type, assignStmt.getRightOpBox()).visit(rightBox.getValue());
                }

                @Override
                public void caseIfStmt(final IfStmt ifStmt) {
                    final ValueBox conditionBox = ifStmt.getConditionBox();
                    new LogicValueTransformer(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
                }

                @Override
                public void caseReturnStmt(final ReturnStmt returnStmt) {
                    final ValueBox returnBox = returnStmt.getOpBox();
                    new LogicValueTransformer(returnType, returnBox).visit(returnBox.getValue());
                }

                @Override
                public void caseAssertionStmt(final AssertStmt assertStmt) {
                    final ValueBox conditionBox = assertStmt.getConditionBox();
                    new LogicValueTransformer(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
                }

                @Override
                public void caseInvariantStmt(final InvariantStmt invariantStmt) {
                    final ValueBox conditionBox = invariantStmt.getConditionBox();
                    new LogicValueTransformer(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
                }

                @Override
                public void caseAssumptionStmt(final AssumeStmt assumeStmt) {
                    final ValueBox conditionBox = assumeStmt.getConditionBox();
                    new LogicValueTransformer(BooleanType.v(), conditionBox).visit(conditionBox.getValue());
                }

            });
        }

        public static class LogicValueTransformer extends AbstractJimpleValueSwitch<Value> {

            public final Type expectedType;
            public final ValueBox resultBox;

            protected interface BinaryConstructor extends BiFunction<ValueBox, ValueBox, Value> {
            }

            protected interface UnaryConstructor extends Function<ValueBox, Value> {
            }

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

            protected void setUnaryValue(final UnaryConstructor constructor, final Type expectedType,
                                         final UnopExpr unaryExpr) {
                final ValueBox operandBox = unaryExpr.getOpBox();
                setValue(constructor.apply(operandBox));
                new LogicValueTransformer(expectedType, operandBox).visit(operandBox.getValue());
            }

            protected void setBinaryValue(final BinaryConstructor constructor, final Type expectedType,
                                          final BinopExpr binaryExpr) {
                final ValueBox leftBox = binaryExpr.getOp1Box();
                final ValueBox rightBox = binaryExpr.getOp2Box();
                setValue(constructor.apply(leftBox, rightBox));
                new LogicValueTransformer(expectedType, leftBox).visit(leftBox.getValue());
                new LogicValueTransformer(expectedType, rightBox).visit(rightBox.getValue());
            }

            protected void setBinaryValue(final BinaryConstructor constructor, final BinopExpr value) {
                setBinaryValue(constructor, Types.v().join(value.getOp1().getType(), value.getOp2().getType()), value);
            }

            @Override
            public void caseIntConstant(final IntConstant intConstant) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType $) {
                        setValue(LogicConstant.v(intConstant.value > 0));
                    }

                });
            }

            @Override
            public void caseAndExpr(final AndExpr andExpr) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType type) {
                        setBinaryValue(Vimp.v()::newLogicAndExpr, type, andExpr);
                    }

                });
            }

            @Override
            public void caseOrExpr(final OrExpr orExpr) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType type) {
                        setBinaryValue(Vimp.v()::newLogicOrExpr, type, orExpr);
                    }

                });
            }

            @Override
            public void caseXorExpr(final XorExpr xorExpr) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType type) {
                        setBinaryValue(Vimp.v()::newLogicXorExpr, type, xorExpr);
                    }

                });
            }

            @Override
            public void caseNegExpr(final NegExpr negExpr) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType type) {
                        setUnaryValue(Vimp.v()::newLogicNotExpr, type, negExpr);
                    }

                });
            }

            @Override
            public void caseGtExpr(final GtExpr gtExpr) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType $) {
                        setBinaryValue(Vimp.v()::newGtExpr, gtExpr);
                    }

                });
            }

            @Override
            public void caseGeExpr(final GeExpr geExpr) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType $) {
                        setBinaryValue(Vimp.v()::newGeExpr, geExpr);
                    }

                });
            }

            @Override
            public void caseLtExpr(final LtExpr ltExpr) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType $) {
                        setBinaryValue(Vimp.v()::newLtExpr, ltExpr);
                    }

                });
            }

            @Override
            public void caseLeExpr(final LeExpr leExpr) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType $) {
                        setBinaryValue(Vimp.v()::newLeExpr, leExpr);
                    }

                });
            }

            @Override
            public void caseEqExpr(final EqExpr eqExpr) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType $) {
                        setBinaryValue(Vimp.v()::newEqExpr, eqExpr);
                    }

                });
            }

            @Override
            public void caseNeExpr(final NeExpr neExpr) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType $) {
                        setBinaryValue(Vimp.v()::newNeExpr, neExpr);
                    }

                });
            }

            @Override
            public void caseInstanceOfExpr(final InstanceOfExpr instanceOfExpr) {
                expectedType.apply(new TypeSwitch<>() {

                    @Override
                    public void caseBooleanType(final BooleanType $) {
                        setValue(Vimp.v().newInstanceOfExpr(instanceOfExpr.getOp(), instanceOfExpr.getCheckType()));
                    }

                });
            }

            private void caseInvokeExpr(final InvokeExpr invokeExpr) {
                for (int i = 0; i < invokeExpr.getArgCount(); ++i) {
                    final ValueBox argumentBox = invokeExpr.getArgBox(i);
                    new LogicValueTransformer(invokeExpr.getMethodRef().getParameterType(i), argumentBox)
                            .visit(argumentBox.getValue());
                }
            }

            @Override
            public void caseVirtualInvokeExpr(final VirtualInvokeExpr virtualInvokeExpr) {
                caseInvokeExpr(virtualInvokeExpr);
            }

            @Override
            public void caseInterfaceInvokeExpr(final InterfaceInvokeExpr interfaceInvokeExpr) {
                caseInvokeExpr(interfaceInvokeExpr);
            }

            @Override
            public void caseStaticInvokeExpr(final StaticInvokeExpr staticInvokeExpr) {
                caseInvokeExpr(staticInvokeExpr);
            }

            public void caseDynamicInvokeExpr(final DynamicInvokeExpr dynamicInvokeExpr) {
                caseInvokeExpr(dynamicInvokeExpr);
            }

            @Override
            public void caseCmpExpr(final CmpExpr $) {}

            @Override
            public void caseCmplExpr(final CmplExpr $) {}

            @Override
            public void caseCmpgExpr(final CmpgExpr $) {}

            @Override
            public void defaultCase(final Value value) {
                if (value instanceof BinopExpr binaryExpr) {
                    final ValueBox leftBox = binaryExpr.getOp1Box();
                    final ValueBox rightBox = binaryExpr.getOp2Box();
                    new LogicValueTransformer(expectedType, leftBox).visit(leftBox.getValue());
                    new LogicValueTransformer(expectedType, rightBox).visit(rightBox.getValue());
                }

                if (value instanceof UnopExpr binaryExpr) {
                    final ValueBox operandBox = binaryExpr.getOpBox();
                    new LogicValueTransformer(expectedType, operandBox).visit(operandBox.getValue());
                }
            }

            @Override
            public Value getResult() {
                return resultBox.getValue();
            }

        }

    }

}
