package byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.encoder.to_bpl.ClassToBplEncoder;
import byteback.syntax.scene.type.declaration.member.field.encoder.to_bpl.FieldToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.*;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.ValueEncoder;
import byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl.MethodToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.ExceptionalTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.OperatorTagMarker;
import byteback.syntax.scene.type.declaration.member.method.tag.PreludeTagAccessor;
import byteback.syntax.scene.type.declaration.member.method.tag.TwoStateTagMarker;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import soot.*;
import soot.jimple.*;
import soot.util.Chain;

import java.util.Arrays;
import java.util.List;

public class ValueToBplEncoder extends ValueEncoder {

    public enum HeapContext {
        TWO_STATE, POST_STATE, PRE_STATE
    }

    public static final String HEAP_SYMBOL = "heap";

    public static final String OLD_HEAP_SYMBOL = "heap'";

    public static final String THROWN_SYMBOL = "`#thrown`";

    public static final String RETURN_SYMBOL = "`#return`";

    private final HeapContext heapContext;

    public ValueToBplEncoder(final Printer printer, final HeapContext heapContext) {
        super(printer);
        this.heapContext = heapContext;
    }

    public ValueToBplEncoder(final Printer printer) {
        this(printer, HeapContext.PRE_STATE);
    }

    public void encodeTypeConstant(final TypeConstant typeConstant) {
        printer.print("`");
        printer.print(typeConstant.value.getClassName());
        printer.print("`");
    }

    public void encodeFunctionCall(final String functionName, final Value... arguments) {
        encodeFunctionCall(functionName, Arrays.stream(arguments).toList());
    }

    public void encodeFunctionCall(final String functionName, final Iterable<Value> arguments) {
        printer.print(functionName);
        printer.print("(");
        printer.startItems(", ");

        for (final Value value : arguments) {
            printer.separate();
            encodeValue(value);
        }

        printer.endItems();
        printer.print(")");
    }

    public void encodeBinding(final Local binding) {
        encodeLocal(binding);
        printer.print(": ");
        final Type bindingType = binding.getType();
        new TypeAccessToBplEncoder(printer).encodeTypeAccess(bindingType);
    }

    public void encodeBindings(final Iterable<Local> bindings) {
        for (final Local local : bindings) {
            printer.separate();
            encodeBinding(local);
        }
    }

    public void encodeQuantifierExpr(final QuantifierExpr quantifierExpr) {
        printer.print("(");
        printer.print(quantifierExpr.getSymbol());
        printer.print(" ");
        final Chain<Local> bindings = quantifierExpr.getBindings();
        printer.startItems(", ");
        encodeBindings(bindings);
        printer.endItems();
        printer.print(" :: ");
        final Value value = quantifierExpr.getValue();
        encodeValue(value);
        printer.print(")");
    }

    public void encodeBinaryExpr(final BinopExpr binopExpr, final String operator) {
        printer.print("(");
        final Value op1Value = binopExpr.getOp1();
        encodeValue(op1Value);
        printer.print(operator);
        final Value op2Value = binopExpr.getOp2();
        encodeValue(op2Value);
        printer.print(")");
    }

    public void encodeLocal(final Local local) {
        printer.print("`");
        printer.print(local.getName());
        printer.print("`");
    }

    public void encodeArguments(final List<Value> arguments) {
        for (final Value argument : arguments) {
            printer.separate();
            encodeValue(argument);
        }
    }

    public void encodeOldHeapReference() {
        switch (heapContext) {
            case TWO_STATE -> {
                printer.print(OLD_HEAP_SYMBOL);
            }
            case POST_STATE -> {
                printer.print("old(");
                printer.print(HEAP_SYMBOL);
                printer.print(")");
            }
            default -> {
                throw new IllegalStateException();
            }
        }
    }

    public void encodeHeapReference() {
        printer.print(HEAP_SYMBOL);
    }

    public void encodeHeapFunctionCall(final String functionName, final Iterable<Value> arguments) {
        printer.print(functionName);
        printer.print("(");

        printer.startItems(", ");
        printer.separate();
        encodeHeapReference();

        for (final Value argument : arguments) {
            printer.separate();
            encodeValue(argument);
        }

        printer.endItems();

        printer.print(")");
    }

    public void encodeHeapFunctionCall(final String functionName, final Value... arguments) {
        encodeHeapFunctionCall(functionName, Arrays.stream(arguments).toList());
    }

    public void encodeCallExpr(final InvokeExpr invokeExpr) {
        final SootMethod calledMethod = invokeExpr.getMethod();
        PreludeTagAccessor.v().get(calledMethod)
                .ifPresentOrElse(
                        (preludeDefinitionTag) -> printer.print(preludeDefinitionTag.getDefinitionSymbol()),
                        () -> new MethodToBplEncoder(printer).encodeMethodName(invokeExpr.getMethodRef())
                );
        printer.print("(");
        printer.startItems(", ");

        if (!OperatorTagMarker.v().hasTag(calledMethod)) {
            printer.separate();
            encodeHeapReference();

            if (TwoStateTagMarker.v().hasTag(calledMethod)) {
                printer.separate();
                encodeOldHeapReference();
            }

            if (ExceptionalTagMarker.v().hasTag(calledMethod)) {
                printer.separate();
                printer.print(THROWN_SYMBOL);
            }
        }

        encodeArguments(invokeExpr.getArgs());
        printer.endItems();
        printer.print(")");
    }

    public void encodeIntConstant(final IntConstant intConstant) {
        printer.print(Integer.toString(intConstant.value));
    }

    public void encodeDoubleConstant(final DoubleConstant doubleConstant) {
        if (Double.isFinite(doubleConstant.value)) {
            printer.print(Double.toString(doubleConstant.value));
        } else {
            throw new IllegalArgumentException("Unable to encode NaN to Boogie");
        }
    }

    public void encodeFloatConstant(final FloatConstant floatConstant) {
        if (Float.isFinite(floatConstant.value)) {
            printer.print(Float.toString(floatConstant.value));
        } else {
            throw new IllegalArgumentException("Unable to encode NaN to Boogie");
        }
    }

    public void encodeLogicConstant(final LogicConstant logicConstant) {
        if (logicConstant.value) {
            printer.print("true");
        } else {
            printer.print("false");
        }
    }

    public void encodeStringConstant(final StringConstant stringConstant) {
        printer.print("string.const(");
        printer.print(Integer.toString(stringConstant.hashCode()));
        printer.print(")");
    }

    public void encodeInstanceFieldRef(final InstanceFieldRef fieldReadExpr, final boolean isOld) {
        printer.print("store.read(");
        printer.startItems(", ");
        printer.separate();

        if (isOld) {
            encodeOldHeapReference();
        } else {
            encodeHeapReference();
        }

        printer.separate();
        encodeValue(fieldReadExpr.getBase());
        printer.separate();
        new FieldToBplEncoder(printer).encodeFieldConstant(fieldReadExpr.getField());
        printer.endItems();
        printer.print(")");
    }

    public void encodeStaticFieldRef(final StaticFieldRef staticFieldRef, final boolean isOld) {
        final SootField sootField = staticFieldRef.getField();
        printer.print("store.read(");
        printer.startItems(", ");
        printer.separate();

        if (isOld) {
            encodeOldHeapReference();
        } else {
            encodeHeapReference();
        }

        printer.separate();
        printer.print("type.reference(");
        new ClassToBplEncoder(printer).encodeClassConstant(sootField.getDeclaringClass());
        printer.print(")");
        printer.separate();
        new FieldToBplEncoder(printer).encodeFieldConstant(sootField);
        printer.endItems();
        printer.print(")");
    }

    public void encodeArrayRef(final ArrayRef arrayReadExpr, final boolean isOld) {
        printer.print("array.read(");
        printer.startItems(", ");
        printer.separate();

        if (isOld) {
            encodeOldHeapReference();
        } else {
            encodeHeapReference();
        }

        printer.separate();
        encodeValue(arrayReadExpr.getBase());
        printer.separate();
        encodeValue(arrayReadExpr.getIndex());
        printer.endItems();
        printer.print(")");
        printer.print(" : ");
        new TypeAccessToBplEncoder(printer).encodeTypeAccess(arrayReadExpr.getType());
    }


    public void encodeCaughtExceptionRef() {
        printer.print(THROWN_SYMBOL);
    }

    public void encodeReturnRef() {
        printer.print(RETURN_SYMBOL);
    }

    public void encodeConcreteRef(final ConcreteRef concreteRef, final boolean isOld) {
        if (concreteRef instanceof final ArrayRef arrayRef) {
            encodeArrayRef(arrayRef, isOld);
        } else if (concreteRef instanceof final InstanceFieldRef instanceFieldRef) {
            encodeInstanceFieldRef(instanceFieldRef, isOld);
        } else if (concreteRef instanceof final StaticFieldRef staticFieldRef) {
            encodeStaticFieldRef(staticFieldRef, isOld);
        } else if (concreteRef instanceof ThrownLocal) {
            encodeCaughtExceptionRef();
        } else if (concreteRef instanceof ReturnLocal) {
            encodeReturnRef();
        } else {
            throw new RuntimeException("Unable to convert reference " + concreteRef + ".");
        }
    }

    public void encodeOldExpr(final OldExpr oldExpr) {
        if (oldExpr.getOp() instanceof final ConcreteRef concreteRef) {
            encodeConcreteRef(concreteRef, true);
        } else {
            throw new IllegalStateException("Unsupported argument for `old` expression: " + oldExpr);
        }
    }

    @Override
    public void encodeValue(final Value value) {
        if (value instanceof final Immediate immediate) {
            if (immediate instanceof final NestedExpr nestedExpr) {
                encodeValue(nestedExpr.getValue());
                return;
            }

            if (value instanceof final Local local) {
                encodeLocal(local);
                return;
            }
        }

        if (value instanceof final ConcreteRef concreteRef) {
            encodeConcreteRef(concreteRef, false);
            return;
        }

        if (value instanceof final OldExpr oldExpr) {
            encodeOldExpr(oldExpr);
            return;
        }

        if (value instanceof final Constant constant) {
            if (constant instanceof final TypeConstant typeConstant) {
                encodeTypeConstant(typeConstant);
                return;
            }

            if (constant instanceof final LogicConstant logicConstant) {
                encodeLogicConstant(logicConstant);
                return;
            }

            if (constant instanceof final IntConstant intConstant) {
                encodeIntConstant(intConstant);
                return;
            }

            if (constant instanceof final DoubleConstant doubleConstant) {
                encodeDoubleConstant(doubleConstant);
                return;
            }

            if (constant instanceof final FloatConstant floatConstant) {
                encodeFloatConstant(floatConstant);
                return;
            }

            if (constant instanceof NullConstant) {
                printer.print("`null`");
                return;
            }

            if (constant instanceof UnitConstant) {
                printer.print("`void`");
                return;
            }

            if (constant instanceof StringConstant stringConstant) {
                encodeStringConstant(stringConstant);
                return;
            }
        }

        if (value instanceof final CastExpr castExpr) {
            final Type fromType = VimpTypeInterpreter.v().typeOf(castExpr.getOp());
            final Type toType = VimpTypeInterpreter.v().typeOf(castExpr);

            if (fromType == BooleanType.v()) {
                if (toType == IntType.v()) {
                    encodeFunctionCall("boolean.to.int", castExpr.getOp());
                    return;
                }
            } else if (fromType == IntType.v()) {
                if (toType == DoubleType.v()) {
                    encodeFunctionCall("int.to.double", castExpr.getOp());
                    return;
                } else if (toType == FloatType.v()) {
                    encodeFunctionCall("int.to.float", castExpr.getOp());
                    return;
                }

            } else if (fromType == DoubleType.v()) {
                if (toType == IntType.v()) {
                    encodeFunctionCall("double.to.int", castExpr.getOp());
                    return;
                }
            } else if (fromType == FloatType.v()) {
                if (toType == IntType.v()) {
                    encodeFunctionCall("float.to.int", castExpr.getOp());
                    return;
                }
            }


            encodeValue(castExpr.getOp());
            return;
        }

        if (value instanceof final BinopExpr binopExpr) {
            if (binopExpr instanceof final ExtendsExpr extendsExpr) {
                encodeFunctionCall("type.extends", extendsExpr.getOp1(), extendsExpr.getOp2());
                return;
            }

            if (binopExpr instanceof final ImpliesExpr impliesExpr) {
                encodeBinaryExpr(impliesExpr, " ==> ");
                return;
            }

            if (binopExpr instanceof final AndExpr andExpr) {
                final Type type = VimpTypeInterpreter.v().typeOf(andExpr);

                if (type == BooleanType.v()) {
                    encodeBinaryExpr(andExpr, " && ");
                    return;
                }
            }

            if (binopExpr instanceof final OrExpr orExpr) {
                final Type type = VimpTypeInterpreter.v().typeOf(orExpr);

                if (type == BooleanType.v()) {
                    encodeBinaryExpr(orExpr, " || ");
                    return;
                }
            }

            if (binopExpr instanceof final XorExpr xorExpr) {
                final Type type = VimpTypeInterpreter.v().typeOf(xorExpr);

                if (type == BooleanType.v()) {
                    encodeBinaryExpr(xorExpr, " != ");
                    return;
                }
            }

            if (binopExpr instanceof final SubExpr subExpr) {
                encodeBinaryExpr(subExpr, " - ");
                return;
            }

            if (binopExpr instanceof final AddExpr addExpr) {
                encodeBinaryExpr(addExpr, " + ");
                return;
            }

            if (binopExpr instanceof final MulExpr mulExpr) {
                encodeBinaryExpr(mulExpr, " * ");
                return;
            }

            if (binopExpr instanceof final RemExpr remExpr) {
                encodeBinaryExpr(remExpr, " mod ");
                return;
            }

            if (binopExpr instanceof final DivExpr divExpr) {
                if (Type.toMachineType(binopExpr.getType()) == IntType.v()) {
                    encodeBinaryExpr(divExpr, " div ");
                } else {
                    encodeBinaryExpr(divExpr, " / ");
                }
                return;
            }

            if (binopExpr instanceof final EqExpr eqExpr) {
                encodeBinaryExpr(eqExpr, " == ");
                return;
            }

            if (binopExpr instanceof final NeExpr neExpr) {
                encodeBinaryExpr(neExpr, " != ");
                return;
            }

            if (binopExpr instanceof final GeExpr geExpr) {
                encodeBinaryExpr(geExpr, " >= ");
                return;
            }

            if (binopExpr instanceof final GtExpr gtExpr) {
                encodeBinaryExpr(gtExpr, " > ");
                return;
            }

            if (binopExpr instanceof final LeExpr leExpr) {
                encodeBinaryExpr(leExpr, " <= ");
                return;
            }

            if (binopExpr instanceof final LtExpr ltExpr) {
                encodeBinaryExpr(ltExpr, " < ");
                return;
            }

            if (binopExpr instanceof final CmplExpr cmplExpr) {
                encodeFunctionCall("real.cmp", cmplExpr.getOp1(), cmplExpr.getOp2());
                return;
            }

            if (binopExpr instanceof final CmpgExpr cmpgExpr) {
                encodeFunctionCall("real.cmp", cmpgExpr.getOp1(), cmpgExpr.getOp2());
                return;
            }

            if (binopExpr instanceof final CmpExpr cmpExpr) {
                encodeFunctionCall("int.cmp", cmpExpr.getOp1(), cmpExpr.getOp2());
                return;
            }
        }

        if (value instanceof final UnopExpr unopExpr) {
            if (unopExpr instanceof NegExpr negExpr) {
                final Type type = VimpTypeInterpreter.v().typeOf(negExpr);

                if (type == BooleanType.v()) {
                    printer.print("!");
                    encodeValue(negExpr.getOp());
                    return;
                }

                if (Type.toMachineType(type) == IntType.v()) {
                    printer.print("-");
                    encodeValue(negExpr.getOp());
                    return;
                }
            }

            if (unopExpr instanceof final LengthExpr lengthExpr) {
                encodeFunctionCall("array.lengthof", lengthExpr.getOp());
                return;
            }
        }

        if (value instanceof final InstanceOfExpr instanceOfExpr) {
            final TypeConstant checkTypeConstant = Vimp.v().newTypeConstant((RefType) instanceOfExpr.getCheckType());
            encodeHeapFunctionCall("reference.instanceof", instanceOfExpr.getOp(), checkTypeConstant);
            return;
        }

        if (value instanceof final InvokeExpr invokeExpr) {
            if (invokeExpr instanceof final CallExpr callExpr) {
                encodeCallExpr(callExpr);
                return;
            }
        }

        if (value instanceof final QuantifierExpr quantifierExpr) {
            encodeQuantifierExpr(quantifierExpr);
            return;
        }

        if (value instanceof final ConditionalExpr conditionalExpr) {
            printer.print("if (");
            encodeValue(conditionalExpr.getOp1());
            printer.print(") then ");
            encodeValue(conditionalExpr.getOp2());
            printer.print(" else ");
            encodeValue(conditionalExpr.getOp3());
            return;
        }

        throw new IllegalStateException("Unable to convert value " + value + " of type " + value.getClass() + ".");
    }

}
