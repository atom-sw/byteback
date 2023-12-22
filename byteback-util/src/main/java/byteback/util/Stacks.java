package byteback.util;

import java.util.Stack;
import java.util.function.BiFunction;

public class Stacks {

	public static <T> void pushAll(final Stack<T> stack, final Iterable<T> elements) {
		for (final T element : elements) {
			stack.push(element);
		}
	}

	public static <T> void popAll(final Stack<T> stack, final Iterable<T> elements) {
		for (final T element : elements) {
			final T popped = stack.pop();
			assert popped == element;
		}
	}

	public static <T> T reduce(final Stack<T> stack, BiFunction<T, T, T> reducer) {
		T accumulator = reducer.apply(stack.pop(), stack.pop());

		while (!stack.isEmpty()) {
			accumulator = reducer.apply(accumulator, stack.pop());
		}

		return accumulator;
	}

}
