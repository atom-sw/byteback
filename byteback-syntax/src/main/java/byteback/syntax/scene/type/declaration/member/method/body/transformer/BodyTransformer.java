package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import soot.Body;

import java.util.Map;

public abstract class BodyTransformer extends soot.BodyTransformer {

    public abstract void transformBody(final BodyContext bodyContext);

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        final var bodyContext = new BodyContext(body);
        transformBody(bodyContext);
    }

}
