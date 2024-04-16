package byteback.converter.common.value;

import byteback.syntax.type.declaration.method.body.value.walker.ValueWalker;

import java.io.PrintWriter;

public abstract class ValueEncoder extends ValueWalker {

    protected final PrintWriter writer;

    public ValueEncoder(final PrintWriter writer) {
        this.writer = writer;
    }

}
