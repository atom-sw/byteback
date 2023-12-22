/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.generics;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;
import byteback.annotations.Contract.Pure;

public class List<T> {

	private final T element;

	private final List<T> tail;

	@Pure
	@Predicate
	public boolean field_values(final T element, final List<T> tail) {
		return eq(this.element, element) & eq(this.tail, tail);
	}

	@Predicate
	public boolean field_values(final T element) {
		return field_values(element, null);
	}

	@Ensure("field_values")
	public List(final T element, final List<T> tail) {
		this.element = element;
		this.tail = tail;
	}

	@Ensure("field_values")
	public List(final T element) {
		this(element, null);
	}

	@Pure
	public T getElement() {
		return element;
	}

	@Pure
	public List<T> getTail() {
		return tail;
	}

	public static void main() {
		final Object a = new Object();
		final List<Object> l = new List<>(a, null);
		assertion(eq(l.getElement(), a));
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
