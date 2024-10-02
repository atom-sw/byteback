/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}CollectionSpec -c %{ghost}ListSpec -c %{ghost}ArraysSpec -o %t.bpl
 */

package byteback.test.substitutability;

import java.util.Arrays;
import java.util.List;

import byteback.specification.Contract.Raise;

public class IrresizeableList {

	@Raise(exception = UnsupportedOperationException.class)
	public void main10() {
		final List<Object> l = Arrays.asList(new Object[]{ new Object(), new Object() });
		l.add(new Object());
	}

	public void main11() {
		final List<Object> l = Arrays.asList(new Object[]{ new Object(), new Object() });
		l.set(0, new Object());
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
