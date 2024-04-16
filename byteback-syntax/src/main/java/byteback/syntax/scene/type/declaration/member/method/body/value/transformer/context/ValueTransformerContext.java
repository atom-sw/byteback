package byteback.syntax.scene.type.declaration.member.method.body.value.transformer.context;

import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.context.UnitTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import soot.ValueBox;

public class ValueTransformerContext extends ValueContext<UnitTransformerContext> {

    public ValueTransformerContext(final UnitTransformerContext unitContext, final ValueBox valueBox) {
        super(unitContext, valueBox);
    }

}
