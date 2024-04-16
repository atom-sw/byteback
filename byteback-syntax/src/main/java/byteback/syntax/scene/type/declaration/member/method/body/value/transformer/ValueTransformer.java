package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.context.UnitContext;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.context.UnitTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.context.ValueContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.context.ValueTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.walker.ValueWalker;
import soot.Body;
import soot.UnitBox;
import soot.ValueBox;

/**
 * Body transformer that applies a transformation to each value *used and defined* in the Body.
 *
 * @author paganma
 */
public abstract class ValueTransformer
        extends ValueWalker<BodyTransformerContext, UnitTransformerContext, ValueTransformerContext> {

    @Override
    public BodyTransformerContext makeBodyContext(final Body body) {
        return new BodyTransformerContext(body);
    }

    @Override
    public UnitTransformerContext makeUnitContext(final BodyTransformerContext bodyContext,
                                                  final UnitBox unitBox) {
        return new UnitTransformerContext(bodyContext, unitBox);
    }

    @Override
    public ValueTransformerContext makeLocalValueContext(final UnitTransformerContext unitContext,
                                                         final ValueBox valueBox) {
        return new ValueTransformerContext(unitContext, valueBox);
    }

}
