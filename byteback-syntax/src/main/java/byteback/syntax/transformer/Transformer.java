package byteback.syntax.transformer;

import byteback.syntax.transformer.context.TransformationContext;

public interface Transformer<T extends TransformationContext> {

    void transform(final T transformationContext);

}
