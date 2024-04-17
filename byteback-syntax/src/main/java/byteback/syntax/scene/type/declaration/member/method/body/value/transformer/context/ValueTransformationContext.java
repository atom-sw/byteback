package byteback.syntax.scene.type.declaration.member.method.body.value.transformer.context;

import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.context.UnitTransformationContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import soot.ValueBox;

public class ValueTransformationContext extends ValueContext<UnitTransformationContext> {

    public ValueTransformationContext(final UnitTransformationContext unitContext, final ValueBox valueBox) {
        super(unitContext, valueBox);
    }

}
