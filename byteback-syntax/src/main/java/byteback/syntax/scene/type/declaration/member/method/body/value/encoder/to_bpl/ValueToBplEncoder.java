package byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl;

import byteback.common.function.Lazy;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.method.body.value.*;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.ValueEncoder;
import byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl.MethodToBplEncoder;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import byteback.syntax.tag.AnnotationReader;
import soot.*;
import soot.jimple.*;
import soot.util.Chain;

import java.util.Arrays;
import java.util.List;

public class ValueToBplEncoder implements ValueEncoder {

    private static final Lazy<ValueToBplEncoder> INSTANCE = Lazy.from(ValueToBplEncoder::new);

    public static ValueToBplEncoder v() {
        return INSTANCE.get();
    }

    private ValueToBplEncoder() {
    }

    public void encodeTypeConstant(final Printer printer, final TypeConstant typeConstant) {
        printer.print("`");
        printer.print(typeConstant.value.getClassName());
        printer.print("`");
    }

    public void encodeFunction(final Printer printer, final String functionName, final Value... arguments) {
        encodeFunction(printer, functionName, Arrays.stream(arguments).toList());
    }

    public void encodeFunction(final Printer printer, final String functionName, final Iterable<Value> arguments) {
        printer.print(functionName);
        printer.print("(");

        printer.startItems(", ");
        for (final Value value : arguments) {
            printer.separate();
            encodeValue(printer, value);
        }
        printer.endItems();
        printer.print(")");
    }

    public void encodeBinding(final Printer printer, final Local binding) {
        printer.print(binding.getName());
        printer.print(": ");
        final Type bindingType = binding.getType();
        TypeAccessToBplEncoder.v().encodeTypeAccess(printer, bindingType);
    }

    public void encodeBindings(final Printer printer, final Iterable<Local> bindings) {
        printer.startItems(", ");
        for (final Local local : bindings) {
            printer.separate();
            encodeBinding(printer, local);
        }
        printer.endItems();
    }

    public void encodeQuantifierExpr(final Printer printer, final QuantifierExpr quantifierExpr) {
        printer.print("(forall ");
        final Chain<Local> bindings = quantifierExpr.getBindings();
        encodeBindings(printer, bindings);
        printer.print(" :: ");
        final Value value = quantifierExpr.getValue();
        encodeValue(printer, value);
        printer.print(")");
    }

    public void encodeBinaryExpr(final Printer printer, final BinopExpr binopExpr, final String operator) {
        printer.print("(");
        final Value op1Value = binopExpr.getOp1();
        encodeValue(printer, op1Value);
        printer.print(operator);
        final Value op2Value = binopExpr.getOp2();
        encodeValue(printer, op2Value);
        printer.print(")");
    }

    public void encodeLocal(final Printer printer, final Local local) {
        printer.print(local.getName());
    }

    public void encodeArguments(final Printer printer, final List<Value> arguments) {
        printer.startItems(", ");

        for (final Value argument : arguments) {
            printer.separate();
            encodeValue(printer, argument);
        }

        printer.endItems();
    }

    public void encodeCallExpr(final Printer printer, final CallExpr callExpr) {
        MethodToBplEncoder.v().encodeMethodName(printer, callExpr.getMethod());
        printer.print("(");
        encodeArguments(printer, callExpr.getArgs());
        printer.print(")");
    }

    public void encodeDoubleConstant(final Printer printer, final DoubleConstant doubleConstant) {
        if (Double.isFinite(doubleConstant.value)) {
            printer.print(Double.toString(doubleConstant.value));
        } else {
            throw new IllegalArgumentException("Unable to encode NaN to Boogie");
        }
    }

    public void encodeFloatConstant(final Printer printer, final FloatConstant floatConstant) {
        if (Float.isFinite(floatConstant.value)) {
            printer.print(Float.toString(floatConstant.value));
        } else {
            throw new IllegalArgumentException("Unable to encode NaN to Boogie");
        }
    }

    @Override
    public void encodeValue(final Printer printer, final Value value) {
        System.out.println(value);
        if (value instanceof final Immediate immediate) {
            if (value instanceof Local local) {
                encodeLocal(printer, local);
                return;
            }

            if (immediate instanceof NestedExpr nestedExpr) {
                encodeValue(printer, nestedExpr.getValue());
                return;
            }
        }

        if (value instanceof final ConcreteRef concreteRef) {
            if (concreteRef instanceof ArrayRef arrayRef) {
                encodeFunction(printer,"array.element", arrayRef.getBase(), arrayRef.getIndex());
            }
        }

        if (value instanceof final Constant constant) {
            if (constant instanceof final TypeConstant typeConstant) {
                encodeTypeConstant(printer, typeConstant);
                return;
            }

            if (constant instanceof final DoubleConstant doubleConstant) {
                encodeDoubleConstant(printer, doubleConstant);
                return;
            }

            if (constant instanceof final FloatConstant floatConstant) {
                encodeFloatConstant(printer, floatConstant);
                return;
            }
        }

        if (value instanceof final BinopExpr binopExpr) {
            if (binopExpr instanceof final ExtendsExpr extendsExpr) {
                encodeFunction(printer, "type.extends", extendsExpr.getOp1(), extendsExpr.getOp2());
                return;
            }

            if (binopExpr instanceof final ImpliesExpr impliesExpr) {
                encodeBinaryExpr(printer, impliesExpr, " ==> ");
                return;
            }

            if (binopExpr instanceof final AndExpr andExpr) {
                final Type type = VimpTypeInterpreter.v().typeOf(andExpr);

                if (type == BooleanType.v()) {
                    encodeBinaryExpr(printer, andExpr, " && ");
                    return;
                }
            }
        }

        if (value instanceof final UnopExpr unopExpr) {
            if (unopExpr instanceof NegExpr negExpr) {
                final Type type = VimpTypeInterpreter.v().typeOf(negExpr);

                if (type == BooleanType.v()) {
                    printer.print("!");
                    encodeValue(printer, negExpr.getOp());
                    return;
                }
            }

            if (unopExpr instanceof LengthExpr lengthExpr) {
                encodeFunction(printer, "array.lengthof", lengthExpr.getOp());
                return;
            }
        }

        if (value instanceof final InvokeExpr invokeExpr) {
            if (invokeExpr instanceof final CallExpr callExpr) {
                encodeCallExpr(printer, callExpr);
                return;
            }
        }

        if (value instanceof final QuantifierExpr quantifierExpr) {
            encodeQuantifierExpr(printer, quantifierExpr);
            return;
        }

        throw new IllegalStateException("Unable to convert value " + value + " of type " + value.getClass());
    }

}
