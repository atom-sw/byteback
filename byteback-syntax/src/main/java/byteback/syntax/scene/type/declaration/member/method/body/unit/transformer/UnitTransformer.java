package byteback.syntax.scene.type.declaration.member.method.body.unit.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.context.UnitTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.walker.UnitWalker;
import soot.*;

/**
 * Body transformer that applies a transformation to each unit.
 *
 * @author paganma
 */
public abstract class UnitTransformer extends UnitWalker<BodyTransformerContext, UnitTransformerContext> {

    @Override
    public BodyTransformerContext makeBodyContext(final Body body) {
        return new BodyTransformerContext(body);
    }

    @Override
    public UnitTransformerContext makeUnitContext(final BodyTransformerContext bodyContext,
                                                  final UnitBox unitBox) {
        return new UnitTransformerContext(bodyContext, unitBox);
    }

}
