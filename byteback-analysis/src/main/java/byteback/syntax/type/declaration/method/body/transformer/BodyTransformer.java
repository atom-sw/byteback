package byteback.syntax.type.declaration.method.body.transformer;

import byteback.syntax.type.declaration.method.body.context.BodyContext;
import byteback.syntax.type.declaration.method.body.walker.BodyWalker;
import soot.Body;

/**
 * Base class for the transformer of a body of a method.
 *
 * @author paganma
 */
public abstract class BodyTransformer extends BodyWalker<BodyContext> {

    @Override
    public BodyContext makeBodyContext(final Body body) {
        return new BodyContext(body);
    }

}
