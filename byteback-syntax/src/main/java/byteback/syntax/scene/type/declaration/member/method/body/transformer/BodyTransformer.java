package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformerContext;
import byteback.syntax.scene.type.declaration.member.method.body.walker.BodyWalker;
import soot.Body;

/**
 * Base class for the transformer of a method's body.
 *
 * @author paganma
 */
public abstract class BodyTransformer extends BodyWalker<BodyTransformerContext> {

    @Override
    public BodyTransformerContext makeBodyContext(final Body body) {
        return new BodyTransformerContext(body);
    }

}
