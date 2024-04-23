package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.UnitTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import soot.Unit;
import soot.UnitBox;
import soot.ValueBox;

import java.util.List;

/**
 * Body transformer that applies a transformation to each value *used and defined* in the Body.
 *
 * @author paganma
 */
public abstract class ValueTransformer extends UnitTransformer {

    public abstract void transformValue(final ValueContext valueContext);

    public List<ValueBox> extractValueBoxes(final Unit unit) {
        return unit.getUseAndDefBoxes();
    }

    @Override
    public void transformUnit(final UnitContext unitContext) {
        final UnitBox unitBox = unitContext.getUnitBox();
        final Unit unit = unitBox.getUnit();

        for (final ValueBox valueBox : extractValueBoxes(unit)) {
            final var valueContext = new ValueContext(unitContext, valueBox);
            transformValue(valueContext);
        }
    }

}
