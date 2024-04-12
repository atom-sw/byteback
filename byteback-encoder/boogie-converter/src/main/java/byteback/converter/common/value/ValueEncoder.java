package byteback.converter.common.value;

import byteback.syntax.value.transformer.ValueTransformer;

import java.io.PrintWriter;

public abstract class ValueEncoder extends ValueTransformer {

    protected final PrintWriter writer;

    public ValueEncoder(final PrintWriter writer) {
        this.writer = writer;
    }

}
