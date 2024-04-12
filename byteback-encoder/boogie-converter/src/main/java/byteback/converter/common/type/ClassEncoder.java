package byteback.converter.common.type;

import byteback.syntax.type.declaration.transformer.ClassTransformer;

import java.io.PrintWriter;

public abstract class ClassEncoder extends ClassTransformer {

    protected final PrintWriter writer;

    public ClassEncoder(final PrintWriter writer) {
        this.writer = writer;
    }

}
