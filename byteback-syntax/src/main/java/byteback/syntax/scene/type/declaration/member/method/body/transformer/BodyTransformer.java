package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.context.BodyContext;
import byteback.syntax.scene.type.declaration.member.method.body.transformer.context.BodyTransformationContext;
import byteback.syntax.walker.Walker;
import soot.Body;

import java.util.Map;

public abstract class BodyTransformer extends soot.BodyTransformer {

    public abstract void transformBody(final BodyTransformationContext bodyContext);

    @Override
    public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
        final var bodyContext = new BodyTransformationContext(body);
        transformBody(bodyContext);
    }

}
