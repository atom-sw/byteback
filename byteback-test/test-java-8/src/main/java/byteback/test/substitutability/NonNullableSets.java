/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}CollectionSpec -c %{ghost}CollectionsSpec -c %{ghost}SetSpec -c %{ghost}LinkedHashSetSpec -c %{ghost}HashSetSpec -o %t.bpl
 */
package byteback.test.substitutability;

import java.util.Set;

import byteback.specification.Contract.Raise;

public class NonNullableSets {

	@Raise(exception = UnsupportedOperationException.class)
	public void main1() {
		final Set<Object> s1 = Set.of(new Object(), new Object());
		s1.add(new Object());
	}

	@Raise(exception = NullPointerException.class)
	public void main2() {
		final Set<Object> s1 = Set.of(null, new Object());
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
