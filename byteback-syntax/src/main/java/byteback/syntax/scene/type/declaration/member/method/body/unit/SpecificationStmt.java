package byteback.syntax.scene.type.declaration.member.method.body.unit;

import byteback.syntax.scene.type.declaration.member.method.body.Vimp;
import byteback.syntax.scene.type.declaration.member.method.body.unit.printer.InlineUnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.AbstractStmt;

import java.util.ArrayList;
import java.util.List;

/**
 * Base specification statement.
 *
 * @author paganma
 */
public abstract class SpecificationStmt extends AbstractStmt {

    private final ValueBox conditionBox;

    public SpecificationStmt(final Value condition) {
        this.conditionBox = Vimp.v().newImmediateBox(condition);
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
        final var printer = new InlineUnitPrinter();
        toString(printer);

        return printer.toString();
    }

}
