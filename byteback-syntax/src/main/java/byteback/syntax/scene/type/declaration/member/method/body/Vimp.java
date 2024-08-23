package byteback.syntax.scene.type.declaration.member.method.body;

import byteback.common.function.Lazy;
import byteback.syntax.scene.type.declaration.member.method.body.unit.*;
import byteback.syntax.scene.type.declaration.member.method.body.value.*;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.IfStmt;
import soot.jimple.internal.ImmediateBox;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.Arrays;
import java.util.List;

/**
 * Provides factory methods for Vimp's syntactic constructs.
 *
 * @author paganma
 */
public class Vimp {

	private static final Lazy<Vimp> INSTANCE = Lazy.from(Vimp::new);

	public static Vimp v() {
		return INSTANCE.get();
	}

	public ImmediateBox newImmediateBox(final Value value) {
		return new ImmediateBox(value);
	}

	public ImmediateBox newArgBox(final Value value) {
		return newImmediateBox(value);
	}

	public AssertStmt newAssertStmt(final Value condition) {
		return new AssertStmt(condition);
	}

	public AssumeStmt newAssumeStmt(final Value condition) {
		return new AssumeStmt(condition);
	}

	public InvariantStmt newInvariantStmt(final Value condition) {
		return new InvariantStmt(condition);
	}

	public ImpliesExpr newImpliesExpr(final Value op1, final Value op2) {
		return new ImpliesExpr(op1, op2);
	}

	public ForallExpr newForallExpr(final Chain<Local> bindings, final Value condition) {
		return new ForallExpr(bindings, condition);
	}

	public ForallExpr newForallExpr(final Local[] bindings, final Value[] triggers, final Value condition) {
		final var bindingChain = new HashChain<Local>();
		bindingChain.addAll(Arrays.asList(bindings));

		final var triggerChain = new HashChain<Value>();
		triggerChain.addAll(Arrays.asList(triggers));

		return new ForallExpr(bindingChain, triggerChain, condition);
	}

	public ForallExpr newForallExpr(final Local[] bindings, final Value condition) {
		final var bindingChain = new HashChain<Local>();
		bindingChain.addAll(Arrays.asList(bindings));

		return new ForallExpr(bindingChain, condition);
	}

	public ExistsExpr newExistsExpr(final Chain<Local> bindings, final Value condition) {
		return new ExistsExpr(bindings, condition);
	}

	public ExistsExpr newExistsExpr(final Local[] bindings, final Value[] triggers, final Value condition) {
		final var bindingChain = new HashChain<Local>();
		bindingChain.addAll(Arrays.asList(bindings));

		final var triggerChain = new HashChain<Value>();
		triggerChain.addAll(Arrays.asList(triggers));

		return new ExistsExpr(bindingChain, triggerChain, condition);
	}

	public ExistsExpr newExistsExpr(final Local[] bindings, final Value condition) {
		final var bindingChain = new HashChain<Local>();
		bindingChain.addAll(Arrays.asList(bindings));

		return new ExistsExpr(bindingChain, condition);
	}

	public OldExpr newOldExpr(final Value value) {
		return new OldExpr(value);
	}

	public CallExpr newCallExpr(final SootMethodRef methodRef, final List<Value> arguments) {
		return new CallExpr(methodRef, arguments);
	}

	public CallExpr newCallExpr(final SootMethodRef methodRef, Value... arguments) {
		return new CallExpr(methodRef, List.of(arguments));
	}

	public CallStmt newCallStmt(final Value[] targets, final CallExpr callExpr) {
		return new CallStmt(targets, callExpr);
	}

	public CallStmt newCallStmt(final List<Value> targets, final CallExpr callExpr) {
		return new CallStmt(targets.toArray(new Value[0]), callExpr);
	}

	public ConditionalExpr newConditionExpr(final Value op1, final Value op2, final Value op3) {
		return new ConditionalExpr(op1, op2, op3);
	}

	public ReturnRef newReturnRef(final Type type) {
		return new ReturnRef(type);
	}

	public ThrownRef newThrownRef() {
		return new ThrownRef();
	}

	public NestedExpr newNestedExpr(final Value value) {
		return new NestedExpr(value);
	}

	public AggregateExpr newAggregateExpr(final AssignStmt assignStmt) {
		return new AggregateExpr(assignStmt);
	}

	public IfStmt newIfStmt(final Value condition, final Unit target) {
		return new JumpStmt(condition, target);
	}

	public YieldStmt newYieldStmt() {
		return new YieldStmt();
	}

	public TypeConstant newTypeConstant(final RefLikeType type) {
		return new TypeConstant(type);
	}

	public ExtendsExpr newExtendsExpr(final Value op1, final Value op2) {
		return new ExtendsExpr(op1, op2);
	}

	public HeapRef newHeapRef() {
		return new HeapRef();
	}

	public OldHeapRef newOldHeapRef() {
		return new OldHeapRef();
	}

	public FieldPointer newFieldPointer(final SootFieldRef fieldRef) {
		return new FieldPointer(fieldRef);
	}

	public ArrayPointer newArrayPointer(final Type type, final Value index) {
		return new ArrayPointer(index, type);
	}

	public Immediate nest(final Value value) {
		if (value instanceof final Immediate immediate) {
			return immediate;
		} else {
			return Vimp.v().newNestedExpr(value);
		}
	}

	public Value unnest(final Value value) {
		Value currentValue = value;

		while (currentValue instanceof final NestedExpr nestedExpr) {
			currentValue = nestedExpr.getValue();
		}

		return currentValue;
	}

}
