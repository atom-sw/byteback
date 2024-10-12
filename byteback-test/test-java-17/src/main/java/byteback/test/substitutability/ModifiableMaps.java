/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}CollectionSpec -c %{ghost}MapSpec -c %{ghost}HashMapSpec -c %{ghost}LinkedHashMapSpec -c %{ghost}CollectionsSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Return;

public class ModifiableMaps {

	@Return
	public void Put_In_HashMap() {
		final HashMap<Object, Object> m0 = new HashMap<>();
		m0.put(new Object(), new Object());
	}

	@Behavior
	public boolean m0_is_mutable(final Map<Object, Object> m0) {
		return Ghost.of(CollectionSpec.class, m0).is_mutable();
	}

	@Return(when = "m0_is_mutable")
	public void Put_In_Mutable_Map(final Map<Object, Object> m0) {
		m0.put(new Object(), new Object());
	}

	@Return
	public void Indirect_Put_In_HashMap() {
		final HashMap<Object, Object> m0 = new HashMap<>();
		Put_In_Mutable_Map(m0);
	}

	@Return
	public void Put_If_InstanceOf_HashMap(final Map<Object, Object> m0) {
		if (m0 instanceof HashMap) {
			final HashMap<Object, Object> m1 = (HashMap<Object, Object>) m0;
			m1.put(new Object(), new Object());
		}
	}

	@Behavior
	public boolean returns_mutable(final int i, final Map<Object, Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_mutable();
	}

	@Ensure("returns_mutable")
	@Return
	public Map<Object, Object> Make_Either_Mutable_HashMap_Or_LinkedHashMap(int i) {
		final Map<Object, Object> m1;

		if (i % 2 == 0) {
			m1 = new HashMap<Object, Object>();
		} else {
			m1 = new LinkedHashMap<Object, Object>();
		}

		return m1;
	}

	@Behavior
	public boolean returns_mutable(final Map<Object, Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_mutable();
	}

	@Return
	@Ensure("returns_mutable")
	public Map<Object, Object> Make_Mutable_Map() {
		return new HashMap<>();
	}

	@Return
	public void Indirect_Put_In_Mutable_Map() {
		final Map<Object, Object> m1 = Make_Mutable_Map();
		Put_In_Mutable_Map(m1);
	}

	@Ensure("returns_mutable")
	@Return
	public Map<Object, Object> Indirect_Make_And_Return_Mutable_Map() {
		final Map<Object, Object> m1 = Make_Either_Mutable_HashMap_Or_LinkedHashMap(2);

		return m1;
	}

	@Ensure("returns_mutable")
	@Return
	public Map<Object, Object> Indirect_Make_Put_And_Return_Mutable_Map() {
		final Map<Object, Object> m1 = Make_Either_Mutable_HashMap_Or_LinkedHashMap(2);
		m1.put(new Object(), new Object());

		return m1;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 11 verified, 0 errors
 */
