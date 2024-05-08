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

    public ValueBox newImmediateBox(final Value value) {
        return new ImmediateBox(value);
    }

    public ValueBox newArgBox(final Value value) {
        return newImmediateBox(value);
    }

    public AssertStmt newAssertStmt(final Value value) {
        return new AssertStmt(value);
    }

    public AssumeStmt newAssumeStmt(final Value value) {
        return new AssumeStmt(value);
    }

    public InvariantStmt newInvariantStmt(final Value value) {
        return new InvariantStmt(value);
    }

    public IffExpr newLogicIffExpr(final Value op1, final Value op2) {
        return new IffExpr(op1, op2);
    }

    public ImpliesExpr newImpliesExpr(final Value op1, final Value op2) {
        return new ImpliesExpr(op1, op2);
    }

    public ForallExpr newForallExpr(final Chain<Local> ls, final Value v) {
        return new ForallExpr(ls, v);
    }

    public ForallExpr newForallExpr(final Local[] ls, final Value v) {
        final var bindings = new HashChain<Local>();
        bindings.addAll(Arrays.asList(ls));

        return new ForallExpr(bindings, v);
    }

    public ExistsExpr newLogicExistsExpr(final Chain<Local> locals, final Value value) {
        return new ExistsExpr(locals, value);
    }

    public OldExpr newOldExpr(final Value value) {
        return new OldExpr(value);
    }

    public CallExpr newCallExpr(final SootMethodRef methodRef, final List<Value> arguments) {
        return new CallExpr(methodRef, arguments);
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

    public TypeConstant newTypeConstant(final RefType refType) {
        return new TypeConstant(refType);
    }

    public ExtendsExpr newExtendsExpr(final Value op1, final Value op2) {
        return new ExtendsExpr(op1, op2);
    }

    public Immediate nest(final Value value) {
        if (value instanceof Immediate immediate) {
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
