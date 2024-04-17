package byteback.syntax.encoder;

import byteback.syntax.encoder.context.EncoderContext;

public interface Encoder<T extends EncoderContext> {

    void encode(final T encodingContext);

}
