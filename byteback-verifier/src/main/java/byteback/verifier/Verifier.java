package byteback.verifier;

import java.util.stream.Stream;

public interface Verifier<C extends Configuration, M extends Message> {

	public Stream<M> verify(final C configuration);

}
