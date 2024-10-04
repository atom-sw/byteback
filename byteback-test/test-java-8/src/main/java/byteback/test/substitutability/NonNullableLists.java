/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}CollectionSpec -c %{ghost}ListSpec -c %{ghost}ArraysSpec -c %{ghost}ArrayListSpec -c %{ghost}LinkedListSpec -c %{ghost}CollectionsSpec -o %t.bpl
 */
package byteback.test.substitutability;

import java.util.List;

import byteback.specification.Contract.Raise;

public class NonNullableLists {

	@Raise(exception = UnsupportedOperationException.class)
	public void main1() {
		final List<Object> l1 = List.of(new Object(), new Object());
		l1.add(new Object());
	}

	@Raise(exception = NullPointerException.class)
	public void main2() {
		final List<Object> l1 = List.of(null, new Object());
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
