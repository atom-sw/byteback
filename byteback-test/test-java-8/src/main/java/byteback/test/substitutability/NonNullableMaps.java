/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}CollectionsSpec -c %{ghost}MapSpec -c %{ghost}HashMapSpec -c %{ghost}LinkedHashMapSpec -o %t.bpl
 */
package byteback.test.substitutability;

import java.util.Map;

import byteback.specification.Contract.Raise;

public class NonNullableMaps {

	@Raise(exception = UnsupportedOperationException.class)
	public void main1() {
		final Map<Object, Object> s1 = Map.of(new Object(), new Object());
		s1.put(new Object(), new Object());
	}

	@Raise(exception = NullPointerException.class)
	public void main2() {
		final Map<Object, Object> s1 = Map.of(null, new Object());
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
