package byteback.converter.soottoboogie.method.procedure;

import byteback.analysis.JimpleStmtSwitch;
import byteback.analysis.JimpleValueSwitch;
import byteback.analysis.Vimp;
import byteback.analysis.vimp.AssertionStmt;
import byteback.analysis.vimp.AssumptionStmt;
import byteback.converter.soottoboogie.Convention;
import byteback.converter.soottoboogie.ConversionException;
import byteback.converter.soottoboogie.Prelude;
import byteback.converter.soottoboogie.expression.PureExpressionExtractor;
import byteback.converter.soottoboogie.field.FieldConverter;
import byteback.converter.soottoboogie.method.StatementConversionException;
import byteback.converter.soottoboogie.type.ReferenceTypeConverter;
import byteback.converter.soottoboogie.type.TypeAccessExtractor;
import byteback.frontend.boogie.ast.AssertStatement;
import byteback.frontend.boogie.ast.Assignee;
import byteback.frontend.boogie.ast.AssignmentStatement;
import byteback.frontend.boogie.ast.AssumeStatement;
import byteback.frontend.boogie.ast.Attribute;
import byteback.frontend.boogie.ast.Body;
import byteback.frontend.boogie.ast.BoundedBinding;
import byteback.frontend.boogie.ast.EqualsOperation;
import byteback.frontend.boogie.ast.Expression;
import byteback.frontend.boogie.ast.GotoStatement;
import byteback.frontend.boogie.ast.Label;
import byteback.frontend.boogie.ast.List;
import byteback.frontend.boogie.ast.NumberLiteral;
import byteback.frontend.boogie.ast.ReturnStatement;
import byteback.frontend.boogie.ast.Statement;
import byteback.frontend.boogie.ast.ValueReference;
import byteback.frontend.boogie.builder.IfStatementBuilder;
import byteback.frontend.boogie.builder.VariableDeclarationBuilder;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import soot.Local;
import soot.SootField;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.grimp.Grimp;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;

public class ProcedureStatementExtractor extends JimpleStmtSwitch<Body> {

	private final ProcedureBodyExtractor bodyExtractor;

	public ProcedureStatementExtractor(final ProcedureBodyExtractor bodyExtractor) {
		this.bodyExtractor = bodyExtractor;
	}

	public ProcedureExpressionExtractor makeExpressionExtractor() {
		return new ProcedureExpressionExtractor(bodyExtractor);
	}

	public ProcedureExpressionExtractor makeExpressionExtractor(final ReferenceProvider referenceProvider) {
		return new ProcedureExpressionExtractor(bodyExtractor, referenceProvider);
	}

	public void addStatement(final Statement statement) {
		assert statement != null;

		bodyExtractor.addStatement(statement);
	}

	public void addSingleAssignment(final Assignee assignee, final Expression expression) {
		final var assignStatement = new AssignmentStatement(assignee, expression);
		addStatement(assignStatement);
	}

	public Body visit(final Unit unit) {
		try {
			unit.apply(this);

			return result();
		} catch (final ConversionException exception) {
			throw new StatementConversionException(unit, exception);
		}
	}

	@Override
	public void caseIdentityStmt(final IdentityStmt identity) {
		final Value assignee = identity.getLeftOp();
		final Value assigned = identity.getRightOp();

		if (assignee instanceof Local local) {
			if (assigned instanceof CaughtExceptionRef) {
				visit(Grimp.v().newAssignStmt(local, Vimp.v().newCaughtExceptionRef()));
			}

			if (assigned instanceof ParameterRef || assigned instanceof ThisRef) {
				final var variableBuilder = new VariableDeclarationBuilder();
				final BoundedBinding variableBinding = ProcedureConverter.makeBinding(local);
				bodyExtractor.addLocalDeclaration(variableBuilder.addBinding(variableBinding).build());

				final var assignment = new AssignmentStatement(
						Assignee.of(ValueReference.of(PureExpressionExtractor.localName(local))),
						ValueReference.of(ProcedureConverter.parameterName(local)));
				addStatement(assignment);
			}
		}
	}

	@Override
	public void caseAssignStmt(final AssignStmt assignment) {
		final Value left = assignment.getLeftOp();
		final Value right = assignment.getRightOp();

		left.apply(new JimpleValueSwitch<>() {

			@Override
			public void caseLocal(final Local local) {
				final ValueReference reference = ValueReference.of(PureExpressionExtractor.localName(local));
				final AtomicBoolean referenceWasAssigned = new AtomicBoolean();
				final Expression assigned = makeExpressionExtractor(new ReferenceProvider() {

					public ValueReference get(final Type expectedType) {
						referenceWasAssigned.set(true);
						return reference;
					}

				}).visit(right);

				if (!referenceWasAssigned.get()) {
					addSingleAssignment(Assignee.of(reference), assigned);
				}
			}

			@Override
			public void caseInstanceFieldRef(final InstanceFieldRef instanceFieldReference) {
				final SootField field = instanceFieldReference.getField();
				final Value base = instanceFieldReference.getBase();
				final Expression assigned = makeExpressionExtractor().visit(right);
				final Expression fieldReference = ValueReference.of(FieldConverter.fieldName(field));
				final Expression boogieBase = new PureExpressionExtractor().visit(base);
				addStatement(Prelude.v().makeHeapUpdateStatement(boogieBase, fieldReference, assigned));
			}

			@Override
			public void caseStaticFieldRef(final StaticFieldRef staticFieldReference) {
				final SootField field = staticFieldReference.getField();
				final Expression assigned = new ProcedureExpressionExtractor(bodyExtractor).visit(right);
				final Expression fieldReference = ValueReference.of(FieldConverter.fieldName(field));
				final Expression boogieBase = ValueReference
						.of(ReferenceTypeConverter.typeName(field.getDeclaringClass()));
				addStatement(Prelude.v().makeStaticUpdateStatement(boogieBase, fieldReference, assigned));
			}

			@Override
			public void caseArrayRef(final ArrayRef arrayReference) {
				final Value base = arrayReference.getBase();
				final Value index = arrayReference.getIndex();
				final Type type = arrayReference.getType();
				final Expression assigned = makeExpressionExtractor().visit(right);
				final Expression indexReference = makeExpressionExtractor().visit(index);
				final Expression boogieBase = new PureExpressionExtractor().visit(base);
				addStatement(Prelude.v().makeArrayUpdateStatement(new TypeAccessExtractor().visit(type), boogieBase,
						indexReference, assigned));
			}

			@Override
			public void caseCaughtExceptionRef(final CaughtExceptionRef exceptionReference) {
				final ValueReference reference = Convention.makeExceptionReference();
				final Expression assigned = makeExpressionExtractor().visit(right);
				addSingleAssignment(Assignee.of(reference), assigned);
			}

			@Override
			public void caseDefault(final Value value) {
				throw new StatementConversionException(assignment,
						"Unknown left hand side argument in assignment: " + assignment);
			}

		});
	}

	@Override
	public void caseReturnVoidStmt(final ReturnVoidStmt returnStatement) {
		addStatement(new ReturnStatement());
	}

	@Override
	public void caseReturnStmt(final ReturnStmt returnStatement) {
		final Value operand = returnStatement.getOp();
		final ValueReference valueReference = Convention.makeReturnReference();
		final var assignee = Assignee.of(valueReference);
		final Expression expression = makeExpressionExtractor().visit(operand);
		addSingleAssignment(assignee, expression);
		addStatement(new ReturnStatement());
	}

	@Override
	public void caseLookupSwitchStmt(final LookupSwitchStmt switchStatement) {
		final Iterator<Unit> targets = switchStatement.getTargets().iterator();
		final Iterator<IntConstant> values = switchStatement.getLookupValues().iterator();
		final Expression key = new ProcedureExpressionExtractor(bodyExtractor).visit(switchStatement.getKey());

		while (targets.hasNext() && values.hasNext()) {
			final Unit target = targets.next();
			final Value value = values.next();
			final var ifBuilder = new IfStatementBuilder();
			final Expression index = new ProcedureExpressionExtractor(bodyExtractor).visit(value);
			final Label label = bodyExtractor.getLabelCollector().fetchLabel(target);

			ifBuilder.condition(new EqualsOperation(index, key)).thenStatement(new GotoStatement(label));
			addStatement(ifBuilder.build());
		}
	}

	@Override
	public void caseTableSwitchStmt(final TableSwitchStmt switchStatement) {
		final Expression key = new ProcedureExpressionExtractor(bodyExtractor).visit(switchStatement.getKey());

		for (int i = 0; i <= switchStatement.getHighIndex() - switchStatement.getLowIndex(); ++i) {
			final Unit target = switchStatement.getTarget(i);
			final var ifBuilder = new IfStatementBuilder();
			final int offsetIndex = switchStatement.getLowIndex() + i;
			final Expression index = new NumberLiteral(Integer.toString(offsetIndex));
			final Label label = bodyExtractor.getLabelCollector().fetchLabel(target);
			ifBuilder.condition(new EqualsOperation(index, key)).thenStatement(new GotoStatement(label));
			addStatement(ifBuilder.build());
		}

		final Label defaultLabel = bodyExtractor.getLabelCollector().fetchLabel(switchStatement.getDefaultTarget());
		addStatement(new GotoStatement(defaultLabel));
	}

	@Override
	public void caseGotoStmt(final GotoStmt gotoStatement) {
		final Unit targetUnit = gotoStatement.getTarget();
		final Label label = bodyExtractor.getLabelCollector().fetchLabel(targetUnit);
		addStatement(new GotoStatement(label));
	}

	@Override
	public void caseIfStmt(final IfStmt ifStatement) {
		final var ifBuilder = new IfStatementBuilder();
		final Value condition = ifStatement.getCondition();
		final Label label = bodyExtractor.getLabelCollector().fetchLabel(ifStatement.getTarget());
		ifBuilder.condition(new ProcedureExpressionExtractor(bodyExtractor).visit(condition))
				.thenStatement(new GotoStatement(label));
		addStatement(ifBuilder.build());
	}

	@Override
	public void caseInvokeStmt(final InvokeStmt invokeStatement) {
		final var invoke = invokeStatement.getInvokeExpr();
		makeExpressionExtractor().visit(invoke);
	}

	@Override
	public void caseAssertionStmt(final AssertionStmt assertionStmt) {
		final Expression condition = makeExpressionExtractor().visit(assertionStmt.getCondition());
		final List<Attribute> attributes = new List<>();

		addStatement(new AssertStatement(attributes, condition));
	}

	@Override
	public void caseAssumptionStmt(final AssumptionStmt assumptionStmt) {
		final Expression condition = makeExpressionExtractor().visit(assumptionStmt.getCondition());
		addStatement(new AssumeStatement(condition));
	}

	@Override
	public void caseDefault(final Unit unit) {
		throw new StatementConversionException(unit, "Cannot extract statements of type " + unit.getClass().getName());
	}

	@Override
	public void caseThrowStmt(final ThrowStmt throwStatement) {
		final Value operand = throwStatement.getOp();

		if (!(operand instanceof CaughtExceptionRef)) {
			final ValueReference valueReference = Convention.makeExceptionReference();
			final var assignee = Assignee.of(valueReference);
			final Expression expression = makeExpressionExtractor().visit(operand);
			addSingleAssignment(assignee, expression);
		}

		addStatement(new ReturnStatement());
	}

	@Override
	public Body result() {
		return bodyExtractor.result();
	}

}
