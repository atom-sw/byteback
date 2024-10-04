/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}CollectionSpec -c %{ghost}CollectionsSpec -c %{ghost}SetSpec -c %{ghost}LinkedHashSetSpec -c %{ghost}HashSetSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class UnmodifiableSets {

	@Return
	public void main1() {
		final HashSet<Object> s0 = new HashSet<>();
		s0.add(new Object());
	}

	@Behavior
	public boolean s0_is_mutable(final Set<Object> s0) {
		return Ghost.of(CollectionSpec.class, s0).is_mutable();
	}

	@Return(when = "s0_is_mutable")
	public void main2(final Set<Object> s0) {
		s0.add(new Object());
	}

	@Return
	public void main3() {
		final HashSet<Object> s0 = new HashSet<>();
		main2(s0);
	}

	@Return
	public void main4(final Set<Object> s0) {
		if (s0 instanceof HashSet) {
			final HashSet<Object> s1 = (HashSet<Object>) s0;
			s1.add(new Object());
		}
	}

	@Behavior
	public boolean returns_mutable(final int i, final Set<Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_mutable();
	}

	@Ensure("returns_mutable")
	@Return
	public Set<Object> main5(int i) {
		final Set<Object> s1;

		if (i % 2 == 0) {
			s1 = new HashSet<Object>();
		} else {
			s1 = new LinkedHashSet<Object>();
		}

		return s1;
	}

	@Behavior
	public boolean returns_mutable(final Set<Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_mutable();
	}

	@Return
	@Ensure("returns_mutable")
	public Set<Object> makeMutableSet() {
		return new HashSet<>();
	}

	@Return
	public void main6() {
		final Set<Object> s1 = makeMutableSet();
		main2(s1);
	}

	@Ensure("returns_mutable")
	@Return
	public Set<Object> main7() {
		final Set<Object> s1 = main5(2);

		return s1;
	}

	@Ensure("returns_mutable")
	@Return
	public Set<Object> main8() {
		final Set<Object> s1 = main5(2);
		s1.add(new Object());

		return s1;
	}

	@Raise(exception = UnsupportedOperationException.class)
	public void main10() {
		final Set<Object> s = Collections.unmodifiableSet(new HashSet<>());
		s.add(new Object());
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 12 verified, 0 errors
 */
