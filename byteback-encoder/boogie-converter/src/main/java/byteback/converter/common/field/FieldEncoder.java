package byteback.converter.common.field;

import byteback.analysis.global.common.transformer.FieldTransformer;

import java.io.PrintWriter;

public abstract class FieldEncoder extends FieldTransformer {

    protected final PrintWriter writer;

    public FieldEncoder(final PrintWriter writer) {
        this.writer = writer;
    }

}
