package byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl;

import byteback.syntax.Vimp;
import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.member.field.transformer.encoder.to_bpl.FieldToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.tag.TwoStateFlagger;
import byteback.syntax.scene.type.declaration.member.method.body.value.*;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.ValueEncoder;
import byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl.MethodToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.OperatorFlagger;
import byteback.syntax.scene.type.declaration.member.method.tag.PreludeDefinitionReader;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import soot.*;
import soot.jimple.*;
import soot.util.Chain;

import java.util.Arrays;
import java.util.List;

public class ValueToBplEncoder extends ValueEncoder {

	public static final String HEAP_SYMBOL = "h";

	public ValueToBplEncoder(final Printer printer) {
		super(printer);
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
		printer.print("(forall ");
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

	public String getHeapSymbol() {
		return "h";
	}

	public void encodeHeapFunctionCall(final String functionName, final Iterable<Value> arguments) {
		printer.print(functionName);
		printer.print("(");
		printer.print(getHeapSymbol());

		for (final Value argument : arguments) {
			printer.print(", ");
			encodeValue(argument);
		}

		printer.print(")");
	}

	public void encodeHeapFunctionCall(final String functionName, final Value... arguments) {
		encodeHeapFunctionCall(functionName, Arrays.stream(arguments).toList());
	}

	public void encodeCallExpr(final CallExpr callExpr) {
		final SootMethod calledMethod = callExpr.getMethod();
		PreludeDefinitionReader.v().get(calledMethod)
				.ifPresentOrElse(
						(preludeDefinitionTag) -> printer.print(preludeDefinitionTag.getDefinitionSymbol()),
						() -> new MethodToBplEncoder(printer).encodeMethodName(callExpr.getMethod()));
		printer.print("(");
		printer.startItems(", ");

		if (!OperatorFlagger.v().isTagged(calledMethod)) {
			printer.separate();
			printer.print(getHeapSymbol());

			if (TwoStateFlagger.v().isTagged(calledMethod)) {
				printer.separate();
				printer.print("heap'");
			}
		}

		encodeArguments(callExpr.getArgs());
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

	public void encodeOldExpr(final OldExpr oldExpr) {
		new OldValueToBplEncoder(printer).encodeValue(oldExpr.getOp());
	}

	public void encodeFieldConstant(final SootFieldRef sootFieldRef) {
		printer.print("`");
		printer.print(sootFieldRef.name());
		printer.print("`");
	}

	public void encodeInstanceFieldRef(final InstanceFieldRef instanceFieldRef) {
		printer.print("store.read(");
		printer.startItems(", ");
		printer.separate();
		printer.print(getHeapSymbol());
		printer.separate();
		encodeValue(instanceFieldRef.getBase());
		printer.separate();
		new FieldToBplEncoder(printer).encodeFieldConstantName(instanceFieldRef.getField());
		printer.print(")");
	}

	@Override
	public void encodeValue(final Value value) {
		if (value instanceof final Immediate immediate) {
			if (immediate instanceof NestedExpr nestedExpr) {
				encodeValue(nestedExpr.getValue());
				return;
			}

			if (value instanceof final Local local) {
				encodeLocal(local);
				return;
			}
		}

		if (value instanceof final ConcreteRef concreteRef) {
			if (concreteRef instanceof final ArrayRef arrayRef) {
				encodeHeapFunctionCall("array.read", arrayRef.getBase(), arrayRef.getIndex());
				printer.print(" : ");
				new TypeAccessToBplEncoder(printer).encodeTypeAccess(arrayRef.getType());
				return;
			}

			if (concreteRef instanceof final InstanceFieldRef instanceFieldRef) {
				encodeInstanceFieldRef(instanceFieldRef);
				return;
			}
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
		}

		if (value instanceof final CastExpr castExpr) {
			final Type fromType = castExpr.getCastType();
			final Type toType = castExpr.getType();

			if (fromType == BooleanType.v()) {
				if (toType == IntType.v()) {
					encodeFunctionCall("bool.to.int");
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

		throw new IllegalStateException("Unable to convert value " + value + " of type " + value.getClass());
	}

}
