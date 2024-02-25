package byteback.analysis.body.vimp;

import byteback.common.Lazy;
import soot.*;
import soot.grimp.internal.ExprBox;
import soot.jimple.*;
import soot.util.Chain;
import soot.util.HashChain;

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
        return new ExprBox(value);
    }

    public AssertionStmt newAssertionStmt(final Value c) {
        return new AssertionStmt(c);
    }

    public AssumptionStmt newAssumptionStmt(final Value c) {
        return new AssumptionStmt(c);
    }

    public InvariantStmt newInvariantStmt(final Value c) {
        return new InvariantStmt(c);
    }

    public LogicAndExpr newLogicAndExpr(final Value a, final Value b) {
        return new LogicAndExpr(a, b);
    }

    public LogicAndExpr newLogicAndExpr(final ValueBox abox, final ValueBox bbox) {
        return new LogicAndExpr(abox, bbox);
    }

    public LogicOrExpr newLogicOrExpr(final Value a, final Value b) {
        return new LogicOrExpr(a, b);
    }

    public LogicOrExpr newLogicOrExpr(final ValueBox abox, final ValueBox bbox) {
        return new LogicOrExpr(abox, bbox);
    }

    public LogicXorExpr newLogicXorExpr(final ValueBox abox, final ValueBox bbox) {
        return new LogicXorExpr(abox, bbox);
    }

    public LogicIffExpr newLogicIffExpr(final Value a, final Value b) {
        return new LogicIffExpr(a, b);
    }

    public LogicNotExpr newLogicNotExpr(final Value v) {
        return new LogicNotExpr(v);
    }

    public LogicNotExpr newLogicNotExpr(final ValueBox vbox) {
        return new LogicNotExpr(vbox);
    }

    public LogicImpliesExpr newLogicImpliesExpr(final Value a, final Value b) {
        return new LogicImpliesExpr(a, b);
    }

    public LogicForallExpr newLogicForallExpr(final Chain<Local> ls, final Value v) {
        return new LogicForallExpr(ls, v);
    }

    public LogicForallExpr newLogicForallExpr(final Local l, final Value v) {
        final HashChain<Local> ls = new HashChain<>();
        ls.add(l);

        return new LogicForallExpr(ls, v);
    }

    public LogicExistsExpr newLogicExistsExpr(final Chain<Local> ls, final Value v) {
        return new LogicExistsExpr(ls, v);
    }

    public LogicExistsExpr newLogicExistsExpr(final Local l, final Value v) {
        final HashChain<Local> ls = new HashChain<>();
        ls.add(l);

        return new LogicExistsExpr(ls, v);
    }

    public GtExpr newGtExpr(final Value a, final Value b) {
        return new LogicGtExpr(a, b);
    }

    public GtExpr newGtExpr(final ValueBox abox, final ValueBox bbox) {
        return new LogicGtExpr(abox, bbox);
    }

    public GeExpr newGeExpr(final Value a, final Value b) {
        return new LogicGeExpr(a, b);
    }

    public GeExpr newGeExpr(final ValueBox abox, final ValueBox bbox) {
        return new LogicGeExpr(abox, bbox);
    }

    public LtExpr newLtExpr(final Value a, final Value b) {
        return new LogicLtExpr(a, b);
    }

    public LtExpr newLtExpr(final ValueBox abox, final ValueBox bbox) {
        return new LogicLtExpr(abox, bbox);
    }

    public LeExpr newLeExpr(final Value a, final Value b) {
        return new LogicLeExpr(a, b);
    }

    public LeExpr newLeExpr(final ValueBox abox, final ValueBox bbox) {
        return new LogicLeExpr(abox, bbox);
    }

    public EqExpr newEqExpr(final ValueBox abox, final ValueBox bbox) {
        return new LogicEqExpr(abox, bbox);
    }

    public EqExpr newEqExpr(final Value a, final Value b) {
        return new LogicEqExpr(a, b);
    }

    public NeExpr newNeExpr(final ValueBox abox, final ValueBox bbox) {
        return new LogicNeExpr(abox, bbox);
    }

    public NeExpr newNeExpr(final Value a, final Value b) {
        return new LogicNeExpr(a, b);
    }

    public CallExpr newCallExpr(final InvokeExpr invokeExpr) {
        return new CallExpr(invokeExpr);
    }

    public IfStmt newIfStmt(final Value value, final Unit target) {
        return new LogicIfStmt(value, target);
    }

    public InstanceOfExpr newInstanceOfExpr(final Value value, final Type type) {
        return new LogicInstanceOfExpr(value, type);
    }

    public CaughtExceptionRef newCaughtExceptionRef() {
        return new ConcreteCaughtExceptionRef();
    }

    public OldExpr newOldExpr(final Value value) {
        return new OldExpr(value);
    }

    public OldExpr newOldExpr(final ValueBox vbox) {
        return new OldExpr(vbox);
    }

}
