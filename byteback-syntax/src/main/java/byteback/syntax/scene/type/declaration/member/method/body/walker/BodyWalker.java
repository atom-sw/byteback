package byteback.syntax.scene.type.declaration.member.method.body.walker;

import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.walker.Walker;
import soot.Body;
import soot.BodyTransformer;

import java.util.Map;

public abstract class BodyWalker<B extends BodyContext> extends BodyTransformer implements Walker {

    public abstract B makeBodyContext(final Body body);

    public abstract void walkBody(final B bodyContext);

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        final B bodyContext = makeBodyContext(body);
        walkBody(bodyContext);
    }

}
