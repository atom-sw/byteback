package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.visitor.VimpValueSwitch;
import byteback.analysis.body.common.syntax.Local;
import byteback.analysis.model.syntax.type.Type;
import soot.UnitPrinter;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.jimple.syntax.AssignStmt;
import byteback.analysis.body.jimple.syntax.internal.JimpleLocal;
import soot.util.Switch;

import java.util.ArrayList;
import java.util.Stack;

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

    public AssignStmt getDefinition() {
        return definition;
    }

    public final ArrayList<AssignStmt> getInnerDefinitions() {
        final var innerDefinitions = new ArrayList<AssignStmt>();
        final var nextNestedExprs = new Stack<NestedExpr>();

        nextNestedExprs.add(this);

        while (!nextNestedExprs.isEmpty()) {
            final NestedExpr nestedExpr = nextNestedExprs.pop();

            if (nestedExpr.getValue() instanceof NestedExpr subExpr) {
                innerDefinitions.add(subExpr.getDefinition());
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
    public NestedExpr clone() {
        return new NestedExpr((AssignStmt) definition.clone());
    }

    @Override
    public boolean equivTo(final Object object) {
        return object instanceof final NestedExpr assignStmt
                && assignStmt.getDefinition().getLeftOp().equivTo(definition.getLeftOp())
                && assignStmt.getDefinition().getRightOp().equivTo(definition.getRightOp());
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
