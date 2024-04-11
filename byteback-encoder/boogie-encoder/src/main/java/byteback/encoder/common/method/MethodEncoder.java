package byteback.encoder.common.method;

import byteback.analysis.global.common.transformer.MethodTransformer;
import byteback.analysis.global.common.transformer.SceneTransformer;

import java.io.PrintWriter;

public abstract class MethodEncoder extends MethodTransformer {

    protected final PrintWriter writer;

    public MethodEncoder(final PrintWriter writer) {
        this.writer = writer;
    }

}
