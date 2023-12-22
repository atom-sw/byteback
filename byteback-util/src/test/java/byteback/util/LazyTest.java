package byteback.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class LazyTest {

	@Test
	public void Get_GivenInitializedValue_ReturnsSameInitializedValue() {
		final Object value = new Object();
		final Lazy<Object> lazyValue = Lazy.from(() -> value);
		assertEquals(lazyValue.get(), value);
	}

	@Test
	public void Get_CalledTwice_ReturnsSameValue() {
		final Lazy<Object> lazyValue = Lazy.from(Object::new);
		final Object first = lazyValue.get();
		final Object second = lazyValue.get();
		assertEquals(first, second);
	}

	@Test(expected = IllegalStateException.class)
	public void Get_CalledOnEmpty_ThrowsIllegalStateException() {
		Lazy.empty().get();
	}

	public void invalidate_CalledBetweenGets_ResultsInDifferentValues() {
		final Lazy<Object> lazyValue = Lazy.from(Object::new);
		final Object first = lazyValue.get();
		lazyValue.invalidate();
		final Object second = lazyValue.get();
		assertNotEquals(first, second);
	}

}
