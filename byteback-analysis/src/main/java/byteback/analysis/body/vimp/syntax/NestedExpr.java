package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.internal.JimpleLocal;
import soot.util.Switch;

import java.util.ArrayList;
import java.util.Stack;

/**
 * An expression that can be nested on multiple levels, breaking the three-address-code form of Jimple. Each
 * subexpression must be indirectly associated to an assignment to a local variable.
 * Nested expressions can be treated as locals in a way that does not break the three-address-code form of the
 * representation. It is always possible given a nested expression to extract an equivalent sequence of local
 * assignments, and hence they can be considered as a convenient mean to creating more complex and idiomatic
 * expressions.
 *
 * @author paganma
 */
public class NestedExpr extends JimpleLocal {

    private final AssignStmt definition;

    private final Local local;

    public NestedExpr(final AssignStmt definition) {
        this(((Local) definition.getLeftOp()), definition);
    }

    public NestedExpr(final Local local, final AssignStmt definition) {
        super(local.getName(), definition.getLeftOp().getType());
        this.local = local;
        this.definition = definition;
    }

    /**
     * @return The definition associated to this expression.
     */
    public AssignStmt getDef() {
        return definition;
    }

    /**
     * @return The chain of definitions associated to this expression.
     */
    public final ArrayList<AssignStmt> getInnerDefs() {
        final var innerDefinitions = new ArrayList<AssignStmt>();
        final var nextNestedExprs = new Stack<NestedExpr>();

        nextNestedExprs.add(this);

        while (!nextNestedExprs.isEmpty()) {
            final NestedExpr nestedExpr = nextNestedExprs.pop();

            if (nestedExpr.getValue() instanceof NestedExpr subExpr) {
                innerDefinitions.add(subExpr.getDef());
                nextNestedExprs.add(subExpr);
            }
        }

        return innerDefinitions;
    }

    public Local getLocal() {
        return local;
    }

    public Value getValue() {
        return definition.getRightOp();
    }

    @Override
    public boolean isStackLocal() {
        return getLocal().isStackLocal();
    }

    @Override
    public Type getType() {
        return getLocal().getType();
    }

    @Override
    public void apply(final Switch visitor) {
        if (visitor instanceof VimpValueSwitch<?> vimpValueSwitch) {
            vimpValueSwitch.caseNestedExpr(this);
        }
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof final NestedExpr assignStmt
                && assignStmt.getDef().getLeftOp().equivTo(definition.getLeftOp())
                && assignStmt.getDef().getRightOp().equivTo(definition.getRightOp());
    }

    @Override
    public int equivHashCode() {
        return definition.getLeftOp().equivHashCode() * 101 + definition.getRightOp().equivHashCode() * 17;
    }

    @Override
    public void toString(final UnitPrinter printer) {
        printer.literal("(");
        definition.getRightOp().toString(printer);
        printer.literal(")");
    }

}
