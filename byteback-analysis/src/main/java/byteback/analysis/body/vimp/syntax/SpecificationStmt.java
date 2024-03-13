package byteback.analysis.body.vimp.syntax;

import byteback.analysis.body.vimp.Vimp;
import byteback.analysis.body.common.syntax.Value;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.internal.AbstractStmt;

import java.util.ArrayList;
import java.util.List;

public abstract class SpecificationStmt extends AbstractStmt {

    private final ValueBox conditionBox;

    public SpecificationStmt(final Value condition) {
        this.conditionBox = Vimp.v().newArgBox(condition);
    }

    public ValueBox getConditionBox() {
        return conditionBox;
    }

    public Value getCondition() {
        return conditionBox.getValue();
    }

    @Override
    public boolean branches() {
        return false;
    }

    @Override
    public boolean fallsThrough() {
        return true;
    }

    @Override
    public List<ValueBox> getUseBoxes() {
        final ArrayList<ValueBox> useBoxes = new ArrayList<>();
        useBoxes.add(conditionBox);
        useBoxes.addAll(conditionBox.getValue().getUseBoxes());

        return useBoxes;
    }

    @Override
    public String toString() {
        final InlineUnitPrinter printer = new InlineUnitPrinter();
        toString(printer);

        return printer.toString();
    }

}
