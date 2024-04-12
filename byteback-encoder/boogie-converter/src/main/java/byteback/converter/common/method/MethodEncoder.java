package byteback.converter.common.method;

import byteback.analysis.global.common.transformer.MethodTransformer;

import java.io.PrintWriter;

public abstract class MethodEncoder extends MethodTransformer {

    protected final PrintWriter writer;

    public MethodEncoder(final PrintWriter writer) {
        this.writer = writer;
    }

}
