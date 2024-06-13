package byteback.syntax.scene.type.declaration.member.method.body.value.encoder.to_bpl;

import byteback.syntax.printer.Printer;
import byteback.syntax.scene.type.declaration.encoder.to_bpl.ClassToBplEncoder;
import byteback.syntax.scene.type.declaration.member.field.encoder.to_bpl.FieldToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.value.*;
import byteback.syntax.scene.type.declaration.member.method.body.value.analyzer.VimpTypeInterpreter;
import byteback.syntax.scene.type.declaration.member.method.body.value.encoder.ValueEncoder;
import byteback.syntax.scene.type.declaration.member.method.encoder.to_bpl.MethodToBplEncoder;
import byteback.syntax.scene.type.declaration.member.method.tag.PreludeTagAccessor;
import byteback.syntax.scene.type.encoder.to_bpl.TypeAccessToBplEncoder;
import soot.*;
import soot.asm.AsmUtil;
import soot.jimple.*;
import soot.util.Chain;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Optional;

public class ValueToBplEncoder extends ValueEncoder {

	public static final String HEAP_SYMBOL = "heap";

	public static final String OLD_HEAP_SYMBOL = "heap'";

	public static final String THROWN_SYMBOL = "`#e`";

	public static final String RETURN_SYMBOL = "`#r`";

	public ValueToBplEncoder(final Printer printer) {
		super(printer);
	}

	public void encodeTypeConstant(final Type type) {
		if (type instanceof final ArrayType arrayType) {
			final Type baseType = arrayType.getArrayElementType();
			
			if (baseType instanceof PrimType) {
				new TypeAccessToBplEncoder(printer).encodeTypeAccess(baseType);
				printer.print(".array");
			} else {
				printer.print("array.type(");
				encodeTypeConstant(baseType);
				printer.print(")");
			}
		} else if (type instanceof final RefType refType) {
			new ClassToBplEncoder(printer)
				.encodeClassConstant(refType.getSootClass());
		} else {
			throw new IllegalStateException("Unable to extract type constant from " + type);
		}
	}

	public void encodeTypeConstant(final TypeConstant typeConstant) {
		encodeTypeConstant(typeConstant.type);
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

	public void encodeQuantifierBinding(final Local binding) {
		encodeLocal(binding);
		printer.print(": ");
		final Type bindingType = binding.getType();
		new TypeAccessToBplEncoder(printer).encodeTypeAccess(bindingType);
	}

	public void encodeQuantifierBindings(final Iterable<Local> bindings) {
		for (final Local local : bindings) {
			printer.separate();
			encodeQuantifierBinding(local);
		}
	}

	public void encodeQuantifierExpr(final QuantifierExpr quantifierExpr) {
		printer.print("(");
		printer.print(quantifierExpr.getSymbol());
		printer.print(" ");
		final Chain<Local> bindings = quantifierExpr.getBindings();

		printer.startItems(", ");
		encodeQuantifierBindings(bindings);
		printer.endItems();
		printer.print(" :: ");
		final Chain<Value> triggers = quantifierExpr.getTriggers();

		if (!triggers.isEmpty()) {
			printer.print("{ ");
			printer.startItems(", ");

			for (final Value trigger : triggers) {
				printer.separate();
				encodeValue(trigger);
			}

			printer.endItems();
			printer.print(" } ");
		}

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
		printer.print(OLD_HEAP_SYMBOL);
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
		final SootMethodRef sootMethodRef = invokeExpr.getMethodRef();

		if (sootMethodRef instanceof PreludeRef) {
			printer.print(sootMethodRef.getName());
		} else {
			final SootMethod calledMethod = invokeExpr.getMethod();
			PreludeTagAccessor.v().get(calledMethod)
					.ifPresentOrElse(
							(preludeDefinitionTag) -> printer.print(preludeDefinitionTag.getDefinitionSymbol()),
							() -> new MethodToBplEncoder(printer).encodeMethodName(invokeExpr.getMethod()));
		}

		printer.print("(");
		printer.startItems(", ");
		encodeArguments(invokeExpr.getArgs());
		printer.endItems();
		printer.print(")");
	}

	public void encodeIntConstant(final IntConstant intConstant) {
		printer.print(Integer.toString(intConstant.value));
	}

	public void encodeLongConstant(final LongConstant longConstant) {
		printer.print(Long.toString(longConstant.value));
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

	public void encodeClassConstant(final ClassConstant classConstant) {
		final String classDescriptor = classConstant.getValue();
		final RefLikeType constantType = (RefLikeType) AsmUtil.toJimpleRefType(classDescriptor, Optional.absent());
		printer.print("type.const(");
		printer.print(Integer.toString(constantType.getNumber()));
		printer.print(")");
	}

	public void encodeInstanceFieldRef(final InstanceFieldRef fieldReadExpr) {
		printer.print("store.read(");
		printer.startItems(", ");
		printer.separate();
		encodeHeapReference();
		printer.separate();
		encodeValue(fieldReadExpr.getBase());
		printer.separate();
		new FieldToBplEncoder(printer).encodeFieldConstant(fieldReadExpr.getField());
		printer.endItems();
		printer.print(")");
	}

	public void encodeFieldPointer(final FieldPointer fieldPointer) {
		new FieldToBplEncoder(printer).encodeFieldConstant(fieldPointer.getField());
	}

	public void encodeArrayPointer(final ArrayPointer arrayPointer) {
		printer.print("array.element(");
		encodeValue(arrayPointer.getIndex());
		printer.print(")");
	}

	public void encodeStaticFieldRef(final StaticFieldRef staticFieldRef) {
		final SootField sootField = staticFieldRef.getField();
		printer.print("store.read(");
		printer.startItems(", ");
		printer.separate();
		encodeHeapReference();
		printer.separate();
		printer.print("type.reference(");
		new ClassToBplEncoder(printer).encodeClassConstant(sootField.getDeclaringClass());
		printer.print(")");
		printer.separate();
		new FieldToBplEncoder(printer).encodeFieldConstant(sootField);
		printer.endItems();
		printer.print(")");
	}

	public void encodeArrayRef(final ArrayRef arrayReadExpr) {
		printer.print("unbox(");
		printer.print("store.read(");
		printer.startItems(", ");
		printer.separate();
		encodeHeapReference();
		printer.separate();
		encodeValue(arrayReadExpr.getBase());
		printer.separate();
		printer.print("array.element(");
		encodeValue(arrayReadExpr.getIndex());
		printer.print(")");
		printer.endItems();
		printer.print("))");
		printer.print(" : ");
		new TypeAccessToBplEncoder(printer).encodeTypeAccess(arrayReadExpr.getType());
	}

	public void encodeCaughtExceptionRef() {
		printer.print(THROWN_SYMBOL);
	}

	public void encodeReturnRef() {
		printer.print(RETURN_SYMBOL);
	}

	public void encodeConcreteRef(final ConcreteRef concreteRef) {
		if (concreteRef instanceof HeapRef) {
			printer.print("heap");
		} else if (concreteRef instanceof final FieldPointer fieldPointer) {
			encodeFieldPointer(fieldPointer);
			return;
		} else if (concreteRef instanceof ThrownRef) {
			encodeCaughtExceptionRef();
		} else if (concreteRef instanceof ReturnRef) {
			encodeReturnRef();
		} else {
			throw new RuntimeException("Unable to convert reference " + concreteRef + ".");
		}
	}

	public void encodeOldExpr(final OldExpr oldExpr) {
		printer.print("old(");
		encodeValue(oldExpr.getOp());
		printer.print(")");
	}

	public void encodeParameterRef(final ParameterRef parameterRef) {
		printer.print("`#p");
		printer.print(Integer.toString(parameterRef.getIndex()));
		printer.print("`");
	}

	public void encodeThisRef(final ThisRef thisRef) {
		printer.print("`#t`");
	}

	public void encodeInputRef(final IdentityRef identityRef) {
		if (identityRef instanceof final ParameterRef parameterRef) {
			encodeParameterRef(parameterRef);
		} else if (identityRef instanceof final ThisRef thisRef) {
			encodeThisRef(thisRef);
		} else {
			throw new IllegalStateException("Unable to encode IdentityRef of type: " + identityRef.getClass());
		}
	}

	@Override
	public void encodeValue(final Value value) {
		if (value instanceof final NestedExpr nestedExpr) {
			encodeValue(nestedExpr.getValue());
			return;
		}

		if (value instanceof final Local local) {
			encodeLocal(local);
			return;
		}

		if (value instanceof final ConcreteRef concreteRef) {
			encodeConcreteRef(concreteRef);
			return;
		}

		if (value instanceof final ArrayPointer arrayPointer) {
			encodeArrayPointer(arrayPointer);
			return;
		}

		if (value instanceof final OldExpr oldExpr) {
			encodeOldExpr(oldExpr);
			return;
		}

		if (value instanceof final TypeConstant typeConstant) {
			encodeTypeConstant(typeConstant);
			return;
		}

		if (value instanceof final LogicConstant logicConstant) {
			encodeLogicConstant(logicConstant);
			return;
		}

		if (value instanceof final IntConstant intConstant) {
			encodeIntConstant(intConstant);
			return;
		}

		if (value instanceof final LongConstant longConstant) {
			encodeLongConstant(longConstant);
			return;
		}

		if (value instanceof final DoubleConstant doubleConstant) {
			encodeDoubleConstant(doubleConstant);
			return;
		}

		if (value instanceof final FloatConstant floatConstant) {
			encodeFloatConstant(floatConstant);
			return;
		}

		if (value instanceof NullConstant) {
			printer.print("`null`");
			return;
		}

		if (value instanceof UnitConstant) {
			printer.print("`void`");
			return;
		}

		if (value instanceof StringConstant stringConstant) {
			encodeStringConstant(stringConstant);
			return;
		}

		if (value instanceof ClassConstant classConstant) {
			encodeClassConstant(classConstant);
			return;
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

		if (value instanceof final ExtendsExpr extendsExpr) {
			encodeFunctionCall("type.extends", extendsExpr.getOp1(), extendsExpr.getOp2());
			return;
		}

		if (value instanceof final ImpliesExpr impliesExpr) {
			encodeBinaryExpr(impliesExpr, " ==> ");
			return;
		}

		if (value instanceof final AndExpr andExpr) {
			final Type type = VimpTypeInterpreter.v().typeOf(andExpr);

			if (type == BooleanType.v()) {
				encodeBinaryExpr(andExpr, " && ");
				return;
			}
		}

		if (value instanceof final OrExpr orExpr) {
			final Type type = VimpTypeInterpreter.v().typeOf(orExpr);

			if (type == BooleanType.v()) {
				encodeBinaryExpr(orExpr, " || ");
				return;
			}
		}

		if (value instanceof final XorExpr xorExpr) {
			final Type type = VimpTypeInterpreter.v().typeOf(xorExpr);

			if (type == BooleanType.v()) {
				encodeBinaryExpr(xorExpr, " != ");
				return;
			}
		}

		if (value instanceof final SubExpr subExpr) {
			encodeBinaryExpr(subExpr, " - ");
			return;
		}

		if (value instanceof final AddExpr addExpr) {
			encodeBinaryExpr(addExpr, " + ");
			return;
		}

		if (value instanceof final MulExpr mulExpr) {
			encodeBinaryExpr(mulExpr, " * ");
			return;
		}

		if (value instanceof final RemExpr remExpr) {
			encodeBinaryExpr(remExpr, " mod ");
			return;
		}

		if (value instanceof final DivExpr divExpr) {
			if (Type.toMachineType(divExpr.getType()) == IntType.v()) {
				encodeBinaryExpr(divExpr, " div ");
			} else {
				encodeBinaryExpr(divExpr, " / ");
			}
			return;
		}

		if (value instanceof final EqExpr eqExpr) {
			encodeBinaryExpr(eqExpr, " == ");
			return;
		}

		if (value instanceof final NeExpr neExpr) {
			encodeBinaryExpr(neExpr, " != ");
			return;
		}

		if (value instanceof final GeExpr geExpr) {
			encodeBinaryExpr(geExpr, " >= ");
			return;
		}

		if (value instanceof final GtExpr gtExpr) {
			encodeBinaryExpr(gtExpr, " > ");
			return;
		}

		if (value instanceof final LeExpr leExpr) {
			encodeBinaryExpr(leExpr, " <= ");
			return;
		}

		if (value instanceof final LtExpr ltExpr) {
			encodeBinaryExpr(ltExpr, " < ");
			return;
		}

		if (value instanceof final CmplExpr cmplExpr) {
			encodeFunctionCall("real.cmp", cmplExpr.getOp1(), cmplExpr.getOp2());
			return;
		}

		if (value instanceof final CmpgExpr cmpgExpr) {
			encodeFunctionCall("real.cmp", cmpgExpr.getOp1(), cmpgExpr.getOp2());
			return;
		}

		if (value instanceof final CmpExpr cmpExpr) {
			encodeFunctionCall("int.cmp", cmpExpr.getOp1(), cmpExpr.getOp2());
			return;
		}

		if (value instanceof final ShlExpr shlExpr) {
			encodeFunctionCall("shl", shlExpr.getOp1(), shlExpr.getOp2());
			return;
		}

		if (value instanceof final ShrExpr shrExpr) {
			encodeFunctionCall("shr", shrExpr.getOp1(), shrExpr.getOp2());
			return;
		}

		if (value instanceof NegExpr negExpr) {
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

		if (value instanceof final LengthExpr lengthExpr) {
			encodeFunctionCall("array.lengthof", lengthExpr.getOp());
			return;
		}

		if (value instanceof final InstanceOfExpr instanceOfExpr) {
			final TypeConstant checkTypeConstant = Vimp.v().newTypeConstant((RefLikeType) instanceOfExpr.getCheckType());
			encodeHeapFunctionCall("reference.instanceof", instanceOfExpr.getOp(), checkTypeConstant);
			return;
		}

		if (value instanceof final CallExpr callExpr) {
			encodeCallExpr(callExpr);
			return;
		}

		if (value instanceof final QuantifierExpr quantifierExpr) {
			encodeQuantifierExpr(quantifierExpr);
			return;
		}

		if (value instanceof final ParameterRef parameterRef) {
			encodeParameterRef(parameterRef);
			return;
		}

		if (value instanceof final ThisRef thisRef) {
			encodeThisRef(thisRef);
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
