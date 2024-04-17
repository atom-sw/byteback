package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.context;

import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformationContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import soot.UnitBox;

public class UnitTransformationContext extends UnitContext<BodyTransformationContext> {

    public UnitTransformationContext(final BodyTransformationContext bodyContext, UnitBox unitBox) {
        super(bodyContext, unitBox);
    }

}
