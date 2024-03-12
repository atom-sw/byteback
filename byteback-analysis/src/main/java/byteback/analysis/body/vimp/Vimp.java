package byteback.analysis.body.vimp;

import byteback.analysis.body.vimp.syntax.*;
import byteback.common.function.Lazy;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.ImmediateBox;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.List;

public class Vimp {

    private static final Lazy<Vimp> instance = Lazy.from(Vimp::new);

    public static Vimp v() {
        return instance.get();
    }

    public static Value cloneIfNecessary(final Value v) {
        if (v instanceof Local || v instanceof Constant) {
            return v;
        } else {
            return (Value) v.clone();
        }
    }

    public ValueBox newArgBox(final Value value) {
        return new ImmediateBox(value);
    }

    public ValueBox newImmediateBox(final Value value) {
        return new ImmediateBox(value);
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

    public IfStmt newIfStmt(final Value value, final Unit target) {
        return new LogicIfStmt(value, target);
    }

    public LogicAndExpr newLogicAndExpr(final Value op1, final Value op2) {
        return new LogicAndExpr(op1, op2);
    }

    public LogicAndExpr newLogicAndExpr(final ValueBox op1box, final ValueBox op2box) {
        return new LogicAndExpr(op1box, op2box);
    }

    public LogicOrExpr newLogicOrExpr(final Value op1, final Value op2) {
        return new LogicOrExpr(op1, op2);
    }

    public LogicOrExpr newLogicOrExpr(final ValueBox op1box, final ValueBox op2box) {
        return new LogicOrExpr(op1box, op2box);
    }

    public LogicXorExpr newLogicXorExpr(final ValueBox op1box, final ValueBox op2box) {
        return new LogicXorExpr(op1box, op2box);
    }

    public LogicIffExpr newLogicIffExpr(final Value op1, final Value op2) {
        return new LogicIffExpr(op1, op2);
    }

    public LogicNotExpr newLogicNotExpr(final Value v) {
        return new LogicNotExpr(v);
    }

    public LogicNotExpr newLogicNotExpr(final ValueBox opBox) {
        return new LogicNotExpr(opBox);
    }

    public LogicImpliesExpr newLogicImpliesExpr(final Value op1, final Value op2) {
        return new LogicImpliesExpr(op1, op2);
    }

    public ForallExpr newLogicForallExpr(final Chain<Local> ls, final Value v) {
        return new ForallExpr(ls, v);
    }

    public ForallExpr newLogicForallExpr(final Local l, final Value v) {
        final HashChain<Local> ls = new HashChain<>();
        ls.add(l);

        return new ForallExpr(ls, v);
    }

    public ExistsExpr newLogicExistsExpr(final Chain<Local> ls, final Value v) {
        return new ExistsExpr(ls, v);
    }

    public ExistsExpr newLogicExistsExpr(final Local l, final Value v) {
        final HashChain<Local> ls = new HashChain<>();
        ls.add(l);

        return new ExistsExpr(ls, v);
    }

    public GtExpr newGtExpr(final Value op1, final Value op2) {
        return new LogicGtExpr(op1, op2);
    }

    public GtExpr newGtExpr(final ValueBox op1box, final ValueBox op2box) {
        return new LogicGtExpr(op1box, op2box);
    }

    public GeExpr newGeExpr(final Value op1, final Value op2) {
        return new LogicGeExpr(op1, op2);
    }

    public GeExpr newGeExpr(final ValueBox op1box, final ValueBox op2box) {
        return new LogicGeExpr(op1box, op2box);
    }

    public LtExpr newLtExpr(final Value op1, final Value op2) {
        return new LogicLtExpr(op1, op2);
    }

    public LtExpr newLtExpr(final ValueBox op1box, final ValueBox op2box) {
        return new LogicLtExpr(op1box, op2box);
    }

    public LeExpr newLeExpr(final Value op1, final Value op2) {
        return new LogicLeExpr(op1, op2);
    }

    public LeExpr newLeExpr(final ValueBox op1box, final ValueBox op2box) {
        return new LogicLeExpr(op1box, op2box);
    }

    public EqExpr newEqExpr(final ValueBox op1box, final ValueBox op2box) {
        return new LogicEqExpr(op1box, op2box);
    }

    public EqExpr newEqExpr(final Value op1, final Value op2) {
        return new LogicEqExpr(op1, op2);
    }

    public NeExpr newNeExpr(final ValueBox op1box, final ValueBox op2box) {
        return new LogicNeExpr(op1box, op2box);
    }

    public NeExpr newNeExpr(final Value op1, final Value op2) {
        return new LogicNeExpr(op1, op2);
    }

    public InstanceOfExpr newInstanceOfExpr(final Value operand, final Type type) {
        return new LogicInstanceOfExpr(operand, type);
    }

    public CaughtExceptionRef newCaughtExceptionRef() {
        return new ConcreteCaughtExceptionRef();
    }

    public OldExpr newOldExpr(final Value operand) {
        return new OldExpr(operand);
    }

    public CallExpr newCallExpr(final SootMethodRef methodRef, final List<Value> args) {
        return new CallExpr(methodRef, args);
    }

    public OldExpr newOldExpr(final ValueBox opBox) {
        return new OldExpr(opBox);
    }

    public NestedExpr newNestedExpr(final AssignStmt definition) {
        return new NestedExpr(definition);
    }

}
