package byteback.converter.soottoboogie.method.procedure;

import byteback.analysis.Namespace;
import byteback.analysis.TypeSwitch;
import byteback.converter.soottoboogie.Convention;
import byteback.converter.soottoboogie.Prelude;
import byteback.converter.soottoboogie.expression.BaseExpressionExtractor;
import byteback.converter.soottoboogie.expression.PureExpressionExtractor;
import byteback.converter.soottoboogie.method.MethodConverter;
import byteback.converter.soottoboogie.method.function.FunctionManager;
import byteback.converter.soottoboogie.type.ReferenceTypeConverter;
import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.List;
import byteback.frontend.boogie.ast.Procedure;
import byteback.frontend.boogie.ast.TargetedCallStatement;
import byteback.frontend.boogie.ast.ValueReference;
import byteback.frontend.boogie.builder.TargetedCallStatementBuilder;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.UnknownType;
import soot.Value;
import soot.VoidType;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.SpecialInvokeExpr;

public class ProcedureExpressionExtractor extends PureExpressionExtractor {

	final ProcedureBodyExtractor bodyExtractor;
	final ReferenceProvider referenceProvider;

	public ProcedureExpressionExtractor(final ProcedureBodyExtractor bodyExtractor) {
		this(bodyExtractor, bodyExtractor.getReferenceProvider());
	}

	public ProcedureExpressionExtractor(final ProcedureBodyExtractor bodyExtractor,
			final ReferenceProvider referenceProvider) {
		this.bodyExtractor = bodyExtractor;
		this.referenceProvider = referenceProvider;
	}

	@Override
	public BaseExpressionExtractor makeExpressionExtractor() {
		return new ProcedureExpressionExtractor(bodyExtractor);
	}

	public TargetedCallStatement makeCall(final SootMethod method, final Iterable<Value> arguments) {
		final var callBuilder = new TargetedCallStatementBuilder();
		callBuilder.name(MethodConverter.methodName(method));
		callBuilder.arguments(new List<Expression>().addAll(convertArguments(method, arguments)));

		return callBuilder.build();
	}

	public void addCall(final TargetedCallStatement callStatement, final Type type) {
		final List<ValueReference> targets = new List<ValueReference>();

		if (type != VoidType.v() && type != UnknownType.v()) {
			final ValueReference reference = referenceProvider.get(type);
			targets.add(reference);
			setExpression(reference);
		}

		targets.add(Convention.makeExceptionReference());
		callStatement.setTargetList(targets);
		bodyExtractor.addStatement(callStatement);
	}

	@Override
	public void setFunctionReference(final SootMethod method, final Iterable<Value> arguments) {
		if (Namespace.isPureMethod(method)) {
			super.setFunctionReference(method, arguments);
		} else if (Namespace.isPredicateMethod(method)) {
			final List<Expression> expressions = new List<>();
			expressions.add(Prelude.v().getHeapVariable().makeValueReference());
			expressions.addAll(convertArguments(method, arguments));
			super.setExpression(FunctionManager.v().convert(method).getFunction().inline(expressions));
		} else {
			final TargetedCallStatement callStatement = makeCall(method, arguments);
			addCall(callStatement, method.getReturnType());
		}
	}

	@Override
	public void caseSpecialInvokeExpr(final SpecialInvokeExpr invoke) {
		caseInstanceInvokeExpr(invoke);
	}

	@Override
	public void caseNewExpr(final NewExpr newExpression) {
		final Procedure newProcedure = Prelude.v().getNewProcedure();
		final TargetedCallStatement callStatement = newProcedure.makeTargetedCall();
		final SootClass baseClass = newExpression.getBaseType().getSootClass();
		final String typeName = ReferenceTypeConverter.typeName(baseClass);
		callStatement.addArgument(ValueReference.of(typeName));
		addCall(callStatement, newExpression.getType());
	}

	@Override
	public void caseNewArrayExpr(final NewArrayExpr arrayExpression) {
		final Procedure arrayProcedure = Prelude.v().getArrayProcedure();
		final TargetedCallStatement callStatement = arrayProcedure.makeTargetedCall();

		arrayExpression.getBaseType().apply(new TypeSwitch<>() {

			@Override
			public void caseRefType(final RefType referenceType) {
				final SootClass baseType = referenceType.getSootClass();
				final String typeName = ReferenceTypeConverter.typeName(baseType);
				callStatement.addArgument(ValueReference.of(typeName));
			}

			@Override
			public void caseDefault(final soot.Type type) {
				callStatement.addArgument(Prelude.v().getPrimitiveTypeConstant().makeValueReference());
			}

		});

		final Expression size = this.makeExpressionExtractor().visit(arrayExpression.getSize());
		callStatement.addArgument(size);
		addCall(callStatement, arrayExpression.getType());
	}

	@Override
	public void caseCaughtExceptionRef(final CaughtExceptionRef caughtExceptionRef) {
		setExpression(Convention.makeExceptionReference());
	}

}
