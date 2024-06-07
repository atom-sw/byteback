package byteback.common.collection;

import org.junit.Test;

import static org.junit.Assert.*;

public class SetHashMapTest {

    @Test
    public void Add_ToEmptySetHashMap_CreatesNonEmptyHashSet() {
        final var setHashMap = new SetHashMap<String, String>();
        setHashMap.add("key", "value");

        assertNotNull(setHashMap.get("key"));
        assertTrue(setHashMap.get("key").contains("value"));
    }

}
