package byteback.syntax.scene.encoder.to_bpl;

import byteback.common.function.Lazy;
import byteback.syntax.scene.encoder.SceneEncoder;
import byteback.syntax.scene.encoder.context.SceneEncoderContext;
import byteback.syntax.scene.type.declaration.encoder.context.ClassEncoderContext;
import byteback.syntax.scene.type.declaration.encoder.to_bpl.ClassToBplEncoder;
import soot.Scene;
import soot.SootClass;

public class SceneToBplEncoder extends SceneEncoder {

    private static final Lazy<SceneToBplEncoder> INSTANCE = Lazy.from(SceneToBplEncoder::new);

    public static SceneToBplEncoder v() {
        return INSTANCE.get();
    }

    private SceneToBplEncoder() {
    }

    @Override
    public void encode(final SceneEncoderContext sceneEncoderContext) {
        final Scene scene = sceneEncoderContext.getScene();

        for (final SootClass sootClass : scene.getClasses()) {
            final var classEncoderContext = new ClassEncoderContext(sceneEncoderContext, sootClass);
            ClassToBplEncoder.v().encode(classEncoderContext);
        }
    }

}
