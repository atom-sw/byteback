package byteback.converter.common.body;

import byteback.syntax.member.method.body.transformer.BodyTransformer;

import java.io.PrintWriter;

public abstract class BodyEncoder extends BodyTransformer {

    protected final PrintWriter writer;

    public BodyEncoder(final PrintWriter writer) {
        this.writer = writer;
    }

}
