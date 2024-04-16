package byteback.syntax;

import byteback.common.function.Lazy;
import byteback.syntax.type.declaration.method.body.unit.*;
import byteback.syntax.type.declaration.method.body.value.*;
import byteback.syntax.type.declaration.method.body.value.box.ConditionExprBox;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.CaughtExceptionRef;
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

    public ConditionExprBox newConditionExprBox(final Value value) {
        return new ConditionExprBox(value);
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
        final var lsChain = new HashChain<Local>();
        lsChain.addAll(Arrays.asList(ls));

        return new ForallExpr(lsChain, v);
    }

    public ForallExpr newForallExpr(final Local local, final Value value) {
        final HashChain<Local> locals = new HashChain<>();
        locals.add(local);

        return new ForallExpr(locals, value);
    }

    public ExistsExpr newLogicExistsExpr(final Chain<Local> locals, final Value value) {
        return new ExistsExpr(locals, value);
    }

    public ExistsExpr newLogicExistsExpr(final Local local, final Value value) {
        final HashChain<Local> ls = new HashChain<>();
        ls.add(local);

        return new ExistsExpr(ls, value);
    }

    public CaughtExceptionRef newCaughtExceptionRef() {
        return new ThrownRef();
    }

    public OldExpr newOldExpr(final Value value) {
        return new OldExpr(value);
    }

    public CallExpr newCallExpr(final SootMethodRef methodRef, final List<Value> args) {
        return new CallExpr(methodRef, args);
    }

    public OldExpr newOldExpr(final ValueBox opBox) {
        return new OldExpr(opBox);
    }

    public ConditionalExpr newConditionExpr(final Value op1, final Value op2, final Value op3) {
        return new ConditionalExpr(op1, op2, op3);
    }

    public ReturnRef newReturnRef(final Type type) {
        return new ReturnRef(type);
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

    public ExtendsExpr newExtendsExpr(final Value op1, final Value op2) {
        return new ExtendsExpr(op1, op2);
    }

    public TypeConstant newTypeConstant(final RefType type) {
        return new TypeConstant(type);
    }

    public Immediate nest(final Value value) {
        if (value instanceof Immediate immediate) {
            return immediate;
        } else {
            return Vimp.v().newNestedExpr(value);
        }
    }

}
