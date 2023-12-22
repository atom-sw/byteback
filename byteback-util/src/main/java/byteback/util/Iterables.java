package byteback.util;

import java.util.LinkedList;

public class Iterables {

	public static <T> LinkedList<T> reversed(final Iterable<T> iterable) {
		final var reversed = new LinkedList<T>();

		for (final T element : iterable) {
			reversed.add(element);
		}

		return reversed;
	}

}
