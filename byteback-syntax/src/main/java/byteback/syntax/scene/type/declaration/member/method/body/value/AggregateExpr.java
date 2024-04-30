package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.declaration.member.method.body.unit.printer.InlineUnitPrinter;
import soot.*;
import soot.jimple.AssignStmt;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.Stack;

/**
 * An expression that can be nested on multiple levels, breaking the three-address-code form of Jimple. Each
 * subexpression must be indirectly associated to an assignment to a local variable. Nested expressions can be treated
 * as locals in a way that does not break the three-address-code form of the representation. It is always possible given
 * a nested expression to extract an equivalent sequence of local assignments, and hence they can be considered as a
 * convenient mean to creating more complex and idiomatic expressions.
 *
 * @author paganma
 */
public class AggregateExpr extends NestedExpr implements Local {

    private final Local local;

    private final AssignStmt assignStmt;

    /**
     * Constructs a new nested expression based on a local assignment. We need a pretty big assumption: The definition
     * is completely independent of other statements in the body. This entails that the local used in the definition
     * assignment will have exactly one possible definition and one use at the position in which the constructed nested
     * expression is used. Notice that you do not really need to take this into account when creating nested expressions
     * using the ExprFolder classes and the NestedExprConstructor, as they already enforce this condition.
     *
     * @param assignStmt The definition associated with this nested expression.
     */
    public AggregateExpr(final AssignStmt assignStmt) {
        this(((Local) assignStmt.getLeftOp()), assignStmt);
    }

    public AggregateExpr(final Local local, final AssignStmt assignStmt) {
        super(assignStmt.getRightOp());
        this.local = local;
        this.assignStmt = assignStmt;
    }

    /**
     * @return The definition associated to this expression.
     */
    public AssignStmt getDef() {
        return assignStmt;
    }

    /**
     * @return The chain of nested definitions associated to this expression.
     */
    public final Chain<AssignStmt> getInnerDefs() {
        final var innerDefinitions = new HashChain<AssignStmt>();
        final var nextAggregateExprs = new Stack<AggregateExpr>();

        nextAggregateExprs.add(this);

        while (!nextAggregateExprs.isEmpty()) {
            final AggregateExpr aggregateExpr = nextAggregateExprs.pop();
            final Value value = aggregateExpr.getValue();

            if (value instanceof final AggregateExpr subExpr) {
                innerDefinitions.add(subExpr.getDef());
                nextAggregateExprs.add(subExpr);
            }
        }

        return innerDefinitions;
    }

    public Local getLocal() {
        return local;
    }

    public Value getValue() {
        return assignStmt.getRightOp();
    }

    @Override
    public String getName() {
        return local.getName();
    }

    @Override
    public void setName(final String name) {
        local.setName(name);
    }

    @Override
    public void setType(final Type type) {
        local.setType(type);
    }

    @Override
    public boolean isStackLocal() {
        return local.isStackLocal();
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof final AggregateExpr aggregateExpr
                && aggregateExpr.getDef().getLeftOp().equivTo(assignStmt.getLeftOp())
                && aggregateExpr.getDef().getRightOp().equivTo(assignStmt.getRightOp());
    }

    @Override
    public int equivHashCode() {
        return assignStmt.getLeftOp().equivHashCode() * 101 + assignStmt.getRightOp().equivHashCode() * 17;
    }

    @Override
    public void toString(final UnitPrinter printer) {
        printer.literal("(");
        assignStmt.getRightOp().toString(printer);
        printer.literal(")");
    }

    @Override
    public String toString() {
        final var printer = new InlineUnitPrinter();
        toString(printer);

        return printer.toString();
    }

    @Override
    public void setNumber(int number) {
        local.setNumber(number);
    }

    @Override
    public int getNumber() {
        return local.getNumber();
    }
}
