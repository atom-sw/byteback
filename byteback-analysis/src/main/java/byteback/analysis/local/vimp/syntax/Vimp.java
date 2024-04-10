package byteback.analysis.local.vimp.syntax;

import byteback.analysis.local.vimp.analyzer.value.VimpTypeInterpreter;
import byteback.analysis.local.vimp.syntax.unit.*;
import byteback.analysis.local.vimp.syntax.value.*;
import byteback.analysis.local.vimp.syntax.value.box.ConditionExprBox;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.ImmediateBox;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.List;

/**
 * Provides factory methods for Vimp's syntactic constructs.
 *
 * @author paganma
 */
public class Vimp {

    private static final Lazy<Vimp> instance = Lazy.from(Vimp::new);

    public static Vimp v() {
        return instance.get();
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

    public ImpliesExpr newLogicImpliesExpr(final Value op1, final Value op2) {
        return new ImpliesExpr(op1, op2);
    }

    public ForallExpr newLogicForallExpr(final Chain<Local> ls, final Value v) {
        return new ForallExpr(ls, v);
    }

    public ForallExpr newLogicForallExpr(final Local local, final Value value) {
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

    public NestedExpr newNestedExpr(final AssignStmt def) {
        return new NestedExpr(def);
    }

    public IfStmt newIfStmt(final Value condition, final Unit target) {
        return new JumpStmt(condition, target);
    }

    public YieldStmt newYieldStmt() {
        return new YieldStmt();
    }

}
