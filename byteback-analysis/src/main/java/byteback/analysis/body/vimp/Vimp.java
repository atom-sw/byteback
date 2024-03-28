package byteback.analysis.body.vimp;

import byteback.analysis.body.vimp.syntax.*;
import byteback.analysis.body.vimp.syntax.ConditionExpr;
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

    public Value cloneIfNecessary(final Value value) {
        if (value instanceof Local || value instanceof Constant) {
            return value;
        } else {
            return (Value) value.clone();
        }
    }

    public ValueBox newArgBox(final Value value) {
        return new ImmediateBox(value);
    }

    public ValueBox newImmediateBox(final Value value) {
        return new ImmediateBox(value);
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

    public ConjExpr newLogicAndExpr(final Value op1, final Value op2) {
        return new ConjExpr(op1, op2);
    }

    public ConjExpr newLogicAndExpr(final ValueBox op1box, final ValueBox op2box) {
        return new ConjExpr(op1box, op2box);
    }

    public DisjExpr newLogicOrExpr(final Value op1, final Value op2) {
        return new DisjExpr(op1, op2);
    }

    public DisjExpr newLogicOrExpr(final ValueBox op1box, final ValueBox op2box) {
        return new DisjExpr(op1box, op2box);
    }

    public LogicXorExpr newLogicXorExpr(final ValueBox op1box, final ValueBox op2box) {
        return new LogicXorExpr(op1box, op2box);
    }

    public IffExpr newLogicIffExpr(final Value op1, final Value op2) {
        return new IffExpr(op1, op2);
    }

    public NotExpr newLogicNotExpr(final Value v) {
        return new NotExpr(v);
    }

    public NotExpr newLogicNotExpr(final ValueBox opBox) {
        return new NotExpr(opBox);
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

    public InstanceOfExpr newInstanceOfExpr(final Value value, final Type type) {
        return new LogicInstanceOfExpr(value, type);
    }

    public CaughtExceptionRef newCaughtExceptionRef() {
        return new ConcreteCaughtExceptionRef();
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

    public ConditionExpr newConditionExpr(final Value op1, final Value op2, final Value op3) {
        return new ConditionExpr(op1, op2, op3);
    }

    public NestedExpr newNestedExpr(final AssignStmt def) {
        return new NestedExpr(def);
    }

    public IfStmt newIfStmt(final Value condition, final Unit target) {
        return new NestedIfStmt(condition, target);
    }

}
