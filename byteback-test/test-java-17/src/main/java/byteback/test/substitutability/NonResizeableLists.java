/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}CollectionSpec -c %{ghost}ListSpec -c %{ghost}ArrayListSpec -c %{ghost}ArraysSpec -o %t.bpl
 */

package byteback.test.substitutability;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class NonResizeableLists {

	@Raise(exception = UnsupportedOperationException.class)
	public void main1() {
		final List<Object> l = Arrays.asList(new Object[]{ new Object(), new Object() });
		l.add(new Object());
	}

	@Return
	public void main2() {
		final List<Object> l = Arrays.asList(new Object[]{ new Object(), new Object() });
		l.set(0, new Object());
	}

	@Raise(exception = IndexOutOfBoundsException.class)
	public void main3() {
		final List<Object> l = Arrays.asList(new Object[]{ new Object(), new Object() });
		l.set(10, new Object());
	}

	@Return
	public void main4(int i) {
		final List<Object> l;

		if (i < 0) {
			l = Arrays.asList(new Object[]{ new Object(), new Object(), new Object() });
		} else {
			l = new ArrayList<>();
			l.add(new Object());
			l.add(new Object());
			l.add(new Object());
		}

		l.set(0, null);
	}

	@Raise(exception = IndexOutOfBoundsException.class)
	public void main5(int i) {
		final List<Object> l;

		if (i < 0) {
			l = Arrays.asList(new Object[]{ new Object(), new Object(), new Object() });
		} else {
			l = new ArrayList<>();
			l.add(new Object());
			l.add(new Object());
			l.add(new Object());
		}

		l.set(4, null);
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
