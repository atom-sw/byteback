/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}MapSpec -c %{ghost}HashMapSpec -c %{ghost}CollectionsSpec -c %{ghost}CollectionSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.CollectionSpec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class UnmodifiableMaps {

    @Raise(exception = UnsupportedOperationException.class)
    public void Put_To_UnmodifiableMap() {
        final Map<Object, Object> m = Collections.unmodifiableMap(new HashMap<>());
        m.put(new Object(), new Object());
    }

    @Behavior
    public boolean m0_is_unmodifiable(final Map<Object, Object> m0) {
        return Ghost.of(CollectionSpec.class, m0).is_unmodifiable();
    }

    @Require("m0_is_unmodifiable")
    @Raise(exception = UnsupportedOperationException.class)
    public void Put_To_Unmodifiable_Map(final Map<Object, Object> m0) {
        m0.put(new Object(), new Object());
    }

    @Behavior
    public boolean returns_unmodifiable(final Map<Object, Object> r) {
        return Ghost.of(CollectionSpec.class, r).is_unmodifiable();
    }

    @Return
    @Ensure("returns_unmodifiable")
    public Map<Object, Object> Make_Unmodifiable_Map() {
        return Collections.unmodifiableMap(new HashMap<>());
    }

    @Return
    @Ensure("returns_unmodifiable")
    public Map<Object, Object> Make_Empty_Unmodifiable_Map() {
        return Collections.unmodifiableMap(new HashMap<>());
    }

    @Raise(exception = UnsupportedOperationException.class)
    public void Indirect_Put_To_Unmodifiable_Map() {
        final Map<Object, Object> m0 = Collections.unmodifiableMap(new HashMap<>());
        Put_To_Unmodifiable_Map(m0);
    }

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
