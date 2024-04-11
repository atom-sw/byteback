package byteback.encoder.common.body;

import byteback.analysis.global.common.transformer.MethodTransformer;
import byteback.analysis.local.common.transformer.body.BodyTransformer;

import java.io.PrintWriter;

public abstract class BodyEncoder extends BodyTransformer {

    protected final PrintWriter writer;

    public BodyEncoder(final PrintWriter writer) {
        this.writer = writer;
    }

}
