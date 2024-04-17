package byteback.syntax.scene.type.declaration.encoder.context;

import byteback.syntax.encoder.context.EncoderContext;
import byteback.syntax.scene.encoder.context.SceneEncoderContext;
import byteback.syntax.scene.type.declaration.context.ClassContext;
import soot.SootClass;

import java.io.PrintWriter;

public class ClassEncoderContext extends ClassContext<SceneEncoderContext> implements EncoderContext {

    private final PrintWriter writer;

    public ClassEncoderContext(final SceneEncoderContext sceneContext, final SootClass sootClass) {
        super(sceneContext, sootClass);
        this.writer = sceneContext.getWriter();
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

}
