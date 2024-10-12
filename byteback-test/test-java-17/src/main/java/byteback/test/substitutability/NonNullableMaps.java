/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}MapSpec -c %{ghost}HashMapSpec -c %{ghost}CollectionsSpec -c %{ghost}CollectionSpec -o %t.bpl
 */

package byteback.test.substitutability;

import static byteback.specification.Operators.*;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.CollectionSpec;

import java.util.Map;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class NonNullableMaps {

	@Raise(exception = UnsupportedOperationException.class)
	public void Put_To_UnmodifiableMap() {
		final Map<Object, Object> m1 = Map.of(new Object(), new Object());
		m1.put(new Object(), new Object());
	}

	@Behavior
	public boolean m0_is_nonnullable(final Map<Object, Object> m0) {
		return Ghost.of(CollectionSpec.class, m0).is_nonnullable();
	}

	@Require("m0_is_nonnullable")
	@Raise(exception = UnsupportedOperationException.class)
	public void Put_To_NonNullable_Map(final Map<Object, Object> m0) {
		m0.put(new Object(), new Object());
	}

	@Behavior
	public boolean returns_nonnullable(final Map<Object, Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_nonnullable();
	}

	@Return
	@Ensure("returns_nonnullable")
	public Map<Object, Object> Make_NonNullable_Map() {
		return Map.of(new Object(), new Object());
	}

	@Return
	@Ensure("returns_nonnullable")
	public Map<Object, Object> Make_Empty_NonNullable_Map() {
		return Map.of();
	}

	@Raise(exception = NullPointerException.class)
	public Map<Object, Object> Make_NonNullable_Map_With_Null_Arg() {
		return Map.of(null, new Object());
	}

	@Raise(exception = UnsupportedOperationException.class)
	public void Indirect_Put_To_NonNullable_Map() {
		final Map<Object, Object> m0 = Map.of(new Object(), new Object());

		Put_To_NonNullable_Map(m0);
	}

	@Behavior
	public boolean i_is_even(final int i) {
		return eq(i % 2, 0);
	}

	@Require("i_is_even")
	@Return
	public void Put_To_NonNullable_Map_If_i_NotEven(int i) {
		final Map<Object, Object> m0 = Map.of(new Object(), new Object());

		if (i % 2 != 0) {
			Put_To_NonNullable_Map(m0);
		}
	}
}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 9 verified, 0 errors
 */
