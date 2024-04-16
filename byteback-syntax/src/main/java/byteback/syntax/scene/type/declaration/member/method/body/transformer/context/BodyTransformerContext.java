package byteback.syntax.scene.type.declaration.member.method.body.transformer.context;

import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import soot.Body;

public class BodyTransformerContext extends BodyContext {

    /**
     * Constructs a new {@link BodyContext}.
     *
     * @param body The body within this context.
     */
    public BodyTransformerContext(Body body) {
        super(body);
    }

}
