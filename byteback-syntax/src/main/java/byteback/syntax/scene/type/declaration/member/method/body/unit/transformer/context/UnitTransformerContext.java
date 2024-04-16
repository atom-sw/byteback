package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.context;

import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import soot.UnitBox;

public class UnitTransformerContext extends UnitContext<BodyTransformerContext> {

    public UnitTransformerContext(final BodyTransformerContext bodyContext, UnitBox unitBox) {
        super(bodyContext, unitBox);
    }

}
