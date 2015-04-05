package io.machinecode.vial.core.map;

import io.machinecode.vial.api.Spread;
import io.machinecode.vial.api.map.OOCursor;
import io.machinecode.vial.api.map.OOMap;
import io.machinecode.vial.core.BadHashCode;
import io.machinecode.vial.core.TestUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OOHashMapExtTest extends Assert {

    protected <K,V> OOMap<K,V> create(final float factor) {
        return new OOHashMap<>(factor);
    }

    protected <K,V> OOMap<K,V> create() {
        return new OOHashMap<>();
    }

    private <K,V> OOMap<K,V> vmap(final K k, final V v) {
        final OOMap<K,V> map = create();
        map.put(k, v);
        return map;
    }

    private <K,V> HashMap<K,V> jmap(final K k, final V v) {
        final HashMap<K,V> map = new HashMap<>();
        map.put(k, v);
        return map;
    }

    @Test
    public void testConstructors() {
        doTestConstructors();
    }

    protected void doTestConstructors() {
        final OOHashMap<Long, Long> a = new OOHashMap<>(4);
        final OOHashMap<Long, Long> b = new OOHashMap<>(0.5f);
        final OOHashMap<Long, Long> c = new OOHashMap<>(4, 0.5f);
        final OOHashMap<Long, Long> d = new OOHashMap<>(4, 0.5f, Spread.MURMUR3);
        assertEquals(a, b);
        assertEquals(a, c);
        assertEquals(a, d);
        assertEquals(b, c);
        assertEquals(b, d);
        assertEquals(c, d);
        assertTrue(a.isEmpty());
        assertTrue(b.isEmpty());
        assertTrue(c.isEmpty());
        assertTrue(d.isEmpty());
        a.put(1L, 2L);
        a.put(2L, 3L);
        assertEquals(2, a.size());
        final OOHashMap<Long, Long> e = new OOHashMap<>(a);
        assertEquals(a, e);
        assertEquals(2, e.size());
        assertTrue(e.containsKey(1L));
        assertTrue(e.containsKey(2L));
        assertTrue(e.containsValue(2L));
        assertTrue(e.containsValue(3L));
    }

    @Test
    public void testNullKey() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, 1L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(1L));
        assertEquals(new Long(1L), map.get(null));

        assertNotNull(map.putIfAbsent(null, 2L));
        assertEquals(new Long(1L), map.get(null));

        assertEquals(new Long(1L), map.remove(null));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(1L));

        assertNull(map.remove(null));

        assertNull(map.putIfAbsent(null, 2L));
        assertEquals(new Long(2L), map.get(null));
    }

    @Test
    public void testNullKeyAndValue() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, null));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(null));
        assertEquals(null, map.get(null));

        assertNull(map.putIfAbsent(null, 2L));
        assertNull(map.get(null));

        assertNull(map.remove(null));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(null));

        assertNull(map.remove(null));

        assertNull(map.putIfAbsent(null, 2L));
        assertEquals(new Long(2L), map.get(null));
    }

    @Test
    public void testNullValue() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(1L, null));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsValue(null));
        assertNull(map.get(1L));

        assertNull(map.putIfAbsent(1L, 2L));
        assertNull(map.get(1L));

        assertNull(map.remove(1L));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(1L));
        assertFalse(map.containsValue(null));

        assertNull(map.putIfAbsent(1L, null));
        assertEquals(null, map.get(1L));
    }

    @Test
    public void testValue() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(1L, 2L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsValue(2L));
        assertEquals(new Long(2L), map.get(1L));

        assertNotNull(map.putIfAbsent(1L, 2L));
        assertEquals(new Long(2L), map.get(1L));

        assertEquals(new Long(2L), map.remove(1L));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(1L));
        assertFalse(map.containsValue(2L));

        assertNull(map.putIfAbsent(1L, 3L));
        assertEquals(new Long(3L), map.get(1L));
    }

    @Test
    public void testPutWithRehash() {
        final OOMap<Long,Long> map = create();
        for (long i = 0; i < 10; ++i) {
            assertNull(map.put(i, i));
        }
        assertEquals(10, map.size());
        for (long i = 0; i < 10; ++i) {
            assertTrue(map.containsKey(i));
            assertTrue(map.containsValue(i));
            assertEquals(new Long(i), map.get(i));
        }
    }

    @Test
    public void testPutIfAbsentWithRehash() {
        final OOMap<Long,Long> map = create();
        for (long i = 0; i < 10; ++i) {
            assertNull(map.putIfAbsent(i, i));
        }
        assertEquals(10, map.size());
        for (long i = 0; i < 10; ++i) {
            assertTrue(map.containsKey(i));
            assertTrue(map.containsValue(i));
            assertEquals(new Long(i), map.get(i));
        }
    }

    @Test
    public void testPutIfAbsentBadHashCode() {
        final OOMap<BadHashCode,Long> map = create();
        final BadHashCode[] arr = new BadHashCode[10];
        for (long i = 0; i < 10; ++i) {
            assertNull(map.putIfAbsent(arr[(int) i] = new BadHashCode(4), i));
        }
        assertEquals(10, map.size());
        for (long i = 0; i < 10; ++i) {
            final BadHashCode bad = arr[(int)i];
            assertTrue(map.containsKey(bad));
            assertTrue(map.containsValue(i));
            assertEquals(new Long(i), map.get(bad));
        }
    }

    @Test
    public void testClear() {
        final OOMap<Long,Long> map = create();
        for (long i = 0; i < 10; ++i) {
            assertNull(map.put(i, i));
        }
        assertEquals(10, map.size());
        map.clear();
        assertEquals(0, map.size());
        for (long i = 0; i < 10; ++i) {
            assertFalse(map.containsKey(i));
            assertFalse(map.containsValue(i));
            assertNull(map.get(i));
        }
        for (long i = 0; i < 10; ++i) {
            assertNull(map.put(i, i));
        }
    }

    @Test
    public void testRemoveKey() {
        final OOMap<Long,Long> map = create();
        for (long i = 0; i < 10; ++i) {
            assertNull(map.put(i, i));
        }
        assertEquals(10, map.size());
        for (long i = 0; i < 10; ++i) {
            assertTrue(map.containsKey(i));
            assertTrue(map.containsValue(i));

            assertEquals(new Long(i), map.remove(i));
            assertNull(map.remove(i));
        }
        assertEquals(0, map.size());
        for (long i = 0; i < 10; ++i) {
            assertNull(map.put(i, i));
        }
    }

    @Test
    public void testRemoveKeyNullKey() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, 1L));
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(1L));
        assertEquals(1, map.size());

        assertEquals(new Long(1L), map.remove(null));
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(1L));
        assertEquals(0, map.size());
    }

    @Test
    public void testRemoveKeyNullKeyAndValue() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, null));
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(null));
        assertEquals(1, map.size());

        assertNull(map.remove(null));
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(null));
        assertEquals(0, map.size());
    }

    @Test
    public void testRemoveDefaultKey() {
        final OOMap<Long,Long> map = create();
        for (long i = 0; i < 10; ++i) {
            assertNull(map.put(i, i));
        }
        assertEquals(10, map.size());
        assertFalse(map.remove(null, 2L));
        assertFalse(map.remove(null, null));
        for (long i = 0; i < 10; ++i) {
            assertTrue(map.containsKey(i));
            assertTrue(map.containsValue(i));

            assertFalse(map.remove(i, 11));
            assertTrue(map.remove(i, i));
            assertFalse(map.remove(i, i));
        }
        assertEquals(0, map.size());
        for (long i = 0; i < 10; ++i) {
            assertNull(map.put(i, i));
        }
    }

    @Test
    public void testRemoveDefaultKeyNullKey() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, 1L));
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(1L));
        assertEquals(1, map.size());

        assertFalse(map.remove(null, 2L));
        assertEquals(1, map.size());
        assertFalse(map.remove(null, null));
        assertEquals(1, map.size());

        assertTrue(map.remove(null, 1L));
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(1L));
        assertEquals(0, map.size());
    }

    @Test
    public void testRemoveKeyDefaultNullKeyAndValue() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, null));
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(null));
        assertEquals(1, map.size());


        assertFalse(map.remove(null, 2L));
        assertEquals(1, map.size());

        assertTrue(map.remove(null, null));
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(null));
        assertEquals(0, map.size());
    }

    @Test
    public void testRemoveValue() {
        final OOMap<Long,Long> map = create();
        for (long i = 0; i < 10; ++i) {
            assertNull(map.put(i, i));
        }
        assertEquals(10, map.size());
        for (long i = 0; i < 10; ++i) {
            assertTrue(map.containsKey(i));
            assertTrue(map.containsValue(i));

            assertTrue(map.removeValue(i));
            assertFalse(map.removeValue(i));
        }
        assertEquals(0, map.size());
        for (long i = 0; i < 10; ++i) {
            assertNull(map.put(i, 0L));
        }
        for (long i = 0; i < 9; ++i) {
            assertTrue(map.removeValue(0L));
            assertTrue(map.containsValue(0L));
        }
        assertFalse(map.removeValue(null));
        assertEquals(1, map.size());

        assertTrue(map.removeValue(0L));
        assertFalse(map.containsValue(0L));
        assertEquals(0, map.size());
    }

    @Test
    public void testRemoveValueNullKey() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, 1L));
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(1L));
        assertEquals(1, map.size());

        assertFalse(map.removeValue(null));
        assertEquals(1, map.size());

        assertFalse(map.removeValue(2L));
        assertEquals(1, map.size());

        assertTrue(map.removeValue(1L));
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(1L));
        assertEquals(0, map.size());
    }

    @Test
    public void testRemoveValueNullKeyAndValue() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, null));
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(null));
        assertEquals(1, map.size());

        assertFalse(map.removeValue(2L));
        assertEquals(1, map.size());

        assertTrue(map.removeValue(null));
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(null));
        assertEquals(0, map.size());
    }

    @Test
    public void testReplaceNullKey() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, 1L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(1L));
        assertEquals(new Long(1L), map.get(null));

        assertEquals(new Long(1L), map.putIfAbsent(null, 3L));
        assertEquals(new Long(1L), map.get(null));

        assertFalse(map.replace(null, null, 3L));
        assertEquals(new Long(1L), map.get(null));
        
        assertEquals(new Long(1L), map.replace(null, 2L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(2L));
        assertFalse(map.containsValue(1L));
        assertEquals(new Long(2L), map.get(null));

        assertTrue(map.replace(null, 2L, null));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(null));
        assertFalse(map.containsValue(2L));
        assertTrue(map.containsValue(null));
        assertNull(map.get(1L));

        assertTrue(map.replace(null, null, 1L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(null));
        assertFalse(map.containsValue(null));
        assertFalse(map.containsValue(2L));
        assertTrue(map.containsValue(1L));
        assertEquals(new Long(1L), map.get(null));

        assertEquals(new Long(1L), map.remove(null));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(1L));
        assertFalse(map.containsValue(2L));
        assertFalse(map.containsValue(3L));

        assertNull(map.putIfAbsent(null, 2L));
        assertEquals(new Long(2L), map.get(null));
        assertTrue(map.containsKey(null));
        assertFalse(map.containsValue(1L));
        assertTrue(map.containsValue(2L));
        assertFalse(map.containsValue(3L));
    }

    @Test
    public void testReplaceNullKeyAndValue() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, null));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(null));
        assertEquals(null, map.get(null));

        assertNull(map.putIfAbsent(null, 1L));
        assertNull(map.get(null));

        assertFalse(map.replace(null, 1L, 1L));
        assertNull(map.get(null));

        assertNull(map.replace(null, 1L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(null));
        assertTrue(map.containsValue(1L));
        assertFalse(map.containsValue(null));
        assertEquals(new Long(1L), map.get(null));

        assertTrue(map.replace(null, 1L, null));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(null));
        assertFalse(map.containsValue(1L));
        assertTrue(map.containsValue(null));
        assertNull(map.get(null));

        assertTrue(map.replace(null, null, 2L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(null));
        assertFalse(map.containsValue(null));
        assertFalse(map.containsValue(1L));
        assertTrue(map.containsValue(2L));
        assertEquals(new Long(2L), map.get(null));

        assertEquals(new Long(2L), map.remove(null));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(null));
        assertFalse(map.containsValue(1L));

        assertNull(map.putIfAbsent(null, 2L));
        assertEquals(new Long(2L), map.get(null));
        assertTrue(map.containsKey(null));
        assertFalse(map.containsValue(null));
        assertFalse(map.containsValue(1L));
        assertTrue(map.containsValue(2L));
    }

    @Test
    public void testReplaceNullValue() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(1L, null));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsValue(null));
        assertNull(map.get(1L));

        assertNull(map.putIfAbsent(1L, 2L));
        assertNull(map.get(1L));

        assertFalse(map.replace(1L, 1L, 3L));
        assertNull(map.get(1L));

        assertNull(map.replace(1L, 1L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsValue(1L));
        assertFalse(map.containsValue(null));
        assertEquals(new Long(1L), map.get(1L));

        assertTrue(map.replace(1L, 1L, null));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(1L));
        assertFalse(map.containsValue(1L));
        assertTrue(map.containsValue(null));
        assertNull(map.get(1L));

        assertTrue(map.replace(1L, null, 2L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(1L));
        assertFalse(map.containsValue(null));
        assertFalse(map.containsValue(1L));
        assertTrue(map.containsValue(2L));
        assertEquals(new Long(2L), map.get(1L));

        assertFalse(map.replace(1L, null, null));

        assertEquals(new Long(2L), map.remove(1L));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(1L));
        assertFalse(map.containsValue(null));
        assertFalse(map.containsValue(1L));

        assertNull(map.putIfAbsent(1L, null));
        assertEquals(null, map.get(1L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsValue(null));
        assertFalse(map.containsValue(1L));
    }

    @Test
    public void testReplaceValue() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(1L, 2L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsValue(2L));
        assertEquals(new Long(2L), map.get(1L));

        assertEquals(new Long(2L), map.putIfAbsent(1L, 3L));
        assertEquals(new Long(2L), map.get(1L));

        assertFalse(map.replace(1L, 1L, 3L));
        assertEquals(new Long(2L), map.get(1L));

        assertEquals(new Long(2L), map.replace(1L, 1L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsValue(1L));
        assertFalse(map.containsValue(2L));
        assertEquals(new Long(1L), map.get(1L));

        assertTrue(map.replace(1L, 1L, 2L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(1L));
        assertFalse(map.containsValue(1L));
        assertTrue(map.containsValue(2L));
        assertEquals(new Long(2L), map.get(1L));

        assertEquals(new Long(2L), map.remove(1L));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(1L));
        assertFalse(map.containsValue(1L));
        assertFalse(map.containsValue(2L));

        assertNull(map.putIfAbsent(1L, 3L));
        assertEquals(new Long(3L), map.get(1L));
        assertEquals(1, map.size());
        assertTrue(map.containsKey(1L));
        assertFalse(map.containsValue(1L));
        assertFalse(map.containsValue(2L));
        assertTrue(map.containsValue(3L));
    }

    @Test
    public void testReplaceNoKey() {
        final OOMap<Long,Long> map = create();
        assertNull(map.replace(1L, 1L));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(1L));
        assertFalse(map.containsValue(1L));
        assertFalse(map.containsValue(2L));
        assertNull(map.get(1L));

        assertFalse(map.replace(1L, 1L, 2L));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(1L));
        assertFalse(map.containsValue(1L));
        assertFalse(map.containsValue(2L));
        assertNull(map.get(1L));

        assertNull(map.replace(null, 1L));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(1L));
        assertFalse(map.containsValue(2L));
        assertNull(map.get(1L));

        assertFalse(map.replace(null, 1L, 2L));
        assertEquals(0, map.size());
        assertFalse(map.containsKey(null));
        assertFalse(map.containsValue(1L));
        assertFalse(map.containsValue(2L));
        assertNull(map.get(1L));
    }

    @Test
    public void testReplaceBadHashCode() {
        final OOMap<BadHashCode,Long> map = create();
        final BadHashCode key = new BadHashCode(4);

        assertNull(map.put(new BadHashCode(4), 123L));
        assertNull(map.put(key, 2L));
        assertEquals(2, map.size());
        assertTrue(map.containsKey(key));
        assertTrue(map.containsValue(2L));
        assertEquals(new Long(2L), map.get(key));

        assertEquals(new Long(2L), map.putIfAbsent(key, 3L));
        assertEquals(new Long(2L), map.get(key));

        assertFalse(map.replace(key, 1L, 3L));
        assertEquals(new Long(2L), map.get(key));

        assertEquals(new Long(2L), map.replace(key, 1L));
        assertEquals(2, map.size());
        assertTrue(map.containsKey(key));
        assertTrue(map.containsValue(1L));
        assertFalse(map.containsValue(2L));
        assertEquals(new Long(1L), map.get(key));

        assertTrue(map.replace(key, 1L, 2L));
        assertEquals(2, map.size());
        assertTrue(map.containsKey(key));
        assertFalse(map.containsValue(1L));
        assertTrue(map.containsValue(2L));
        assertEquals(new Long(2L), map.get(key));

        assertEquals(new Long(2L), map.remove(key));
        assertEquals(1, map.size());
        assertFalse(map.containsKey(key));
        assertFalse(map.containsValue(1L));
        assertFalse(map.containsValue(2L));

        assertNull(map.putIfAbsent(key, 3L));
        assertEquals(new Long(3L), map.get(key));
        assertEquals(2, map.size());
        assertTrue(map.containsKey(key));
        assertFalse(map.containsValue(1L));
        assertFalse(map.containsValue(2L));
        assertTrue(map.containsValue(3L));
    }

    @Test
    public void testGetOrDefault() {
        final OOMap<Long,Long> map = create();
        assertNull(map.getOrDefault(null, null));
        assertEquals(new Long(1L), map.getOrDefault(null, 1L));

        assertNull(map.getOrDefault(1l, null));
        assertEquals(new Long(2L), map.getOrDefault(1l, 2L));

        assertNull(map.put(null, 1L));
        assertNull(map.put(1L, 2L));
        assertNull(map.put(2L, null));
        assertEquals(3, map.size());
        assertTrue(map.containsKey(null));
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsKey(2L));
        assertTrue(map.containsValue(null));
        assertTrue(map.containsValue(1L));
        assertTrue(map.containsValue(2L));

        assertEquals(new Long(1L), map.getOrDefault(null, null));
        assertEquals(new Long(2L), map.getOrDefault(1L, null));
        assertNull(map.getOrDefault(2L, 3L));
    }

    @Test
    public void testGetOrDefaultBadHashCode() {
        final OOMap<BadHashCode,Long> map = create();

        assertNull(map.getOrDefault(null, null));
        assertEquals(new Long(1L), map.getOrDefault(null, 1L));

        assertNull(map.getOrDefault(new BadHashCode(4), null));
        assertEquals(new Long(2L), map.getOrDefault(new BadHashCode(4), 2L));

        final BadHashCode[] arr = new BadHashCode[10];

        assertNull(map.put(null, 1L));
        assertNull(map.put(arr[1] = new BadHashCode(4), 2L));
        assertNull(map.put(arr[2] = new BadHashCode(4), null));
        assertEquals(3, map.size());
        assertTrue(map.containsKey(null));
        assertTrue(map.containsKey(arr[1]));
        assertTrue(map.containsKey(arr[2]));
        assertTrue(map.containsValue(null));
        assertTrue(map.containsValue(1L));
        assertTrue(map.containsValue(2L));

        assertEquals(new Long(1L), map.getOrDefault(null, null));
        assertEquals(new Long(2L), map.getOrDefault(arr[1], null));
        assertNull(map.getOrDefault(arr[2], 3L));
    }

    @Test
    public void testToArray() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, 1L));
        assertNull(map.put(1L, 2L));
        assertNull(map.put(2L, 3L));
        assertNull(map.put(3L, null));
        assertTrue(map.containsKey(null));
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsKey(2L));
        assertTrue(map.containsKey(3L));
        assertTrue(map.containsValue(null));
        assertTrue(map.containsValue(1L));
        assertTrue(map.containsValue(2L));
        assertTrue(map.containsValue(3L));
        assertEquals(4, map.size());

        {
            final Object[] array = map.keySet().toArray();
            assertTrue(TestUtil.arrayContains(array, null));
            assertTrue(TestUtil.arrayContains(array, 1L));
            assertTrue(TestUtil.arrayContains(array, 2L));
            assertTrue(TestUtil.arrayContains(array, 3l));
            assertEquals(4, array.length);
        }
        {
            final Object[] array = map.values().toArray();
            assertTrue(TestUtil.arrayContains(array, null));
            assertTrue(TestUtil.arrayContains(array, 1L));
            assertTrue(TestUtil.arrayContains(array, 2L));
            assertTrue(TestUtil.arrayContains(array, 3l));
            assertEquals(4, array.length);
        }
    }

    @Test
    public void testToArrayT() {
        final OOMap<Long,Long> map = create();
        assertNull(map.put(null, 1L));
        assertNull(map.put(1L, 2L));
        assertNull(map.put(2L, 3L));
        assertNull(map.put(3L, null));
        assertTrue(map.containsKey(null));
        assertTrue(map.containsKey(1L));
        assertTrue(map.containsKey(2L));
        assertTrue(map.containsKey(3L));
        assertTrue(map.containsValue(null));
        assertTrue(map.containsValue(1L));
        assertTrue(map.containsValue(2L));
        assertTrue(map.containsValue(3L));
        assertEquals(4, map.size());

        {
            final Long[] array = map.keySet().toArray(new Long[4]);
            assertTrue(TestUtil.arrayContains(array, null));
            assertTrue(TestUtil.arrayContains(array, 1L));
            assertTrue(TestUtil.arrayContains(array, 2L));
            assertTrue(TestUtil.arrayContains(array, 3l));
            assertEquals(4, array.length);
        }
        {
            final Long[] array = map.values().toArray(new Long[4]);
            assertTrue(TestUtil.arrayContains(array, null));
            assertTrue(TestUtil.arrayContains(array, 1L));
            assertTrue(TestUtil.arrayContains(array, 2L));
            assertTrue(TestUtil.arrayContains(array, 3l));
            assertEquals(4, array.length);
        }
    }

    @Test
    public void testCursor() {
        final OOMap<Long,Long> map = create();

        for (long i = 0; i < 10; ++i) {
            assertNull(map.put(i, i));
        }
        assertNull(map.put(10L, null));

        final OOCursor<Long, Long> c = map.iterator();
        assertTrue(c.hasNext());
        for (final OOCursor<Long, Long> x : map) {
            assertNotNull(c.next());
            assertEquals(x, c);
            assertNotSame(x, c);
            assertEquals(x.key(), c.key());
            assertEquals(x.value(), c.value());
            assertEquals(x.toString(), c.toString());
        }
        assertFalse(c.hasNext());

        c.reset();
        int i = 0;
        for (final OOCursor<Long, Long> x : c) {
            ++i;
            assertSame(x, c);
            assertEquals(x, c);
            assertEquals(x.toString(), c.toString());
        }
        assertEquals(11, i);

        c.reset();
        c.next();
        assertFalse(c.equals(null));
        assertFalse(c.equals(new Object()));
    }

    @Test
    public void testCursorEquals() {
        final OOMap<Integer,Integer> map = create();
        assertNull(map.put(null, null));
        assertTrue(map.iterator().next().equals(new Cur(null, null)));
        assertFalse(map.iterator().next().equals(new Cur(null, 1)));
        assertFalse(map.iterator().next().equals(new Cur(null, 2)));
        assertFalse(map.iterator().next().equals(new Cur(1, null)));
        assertFalse(map.iterator().next().equals(new Cur(1, 1)));
        assertFalse(map.iterator().next().equals(new Cur(1, 2)));
        assertFalse(map.iterator().next().equals(new Cur(2, null)));
        assertFalse(map.iterator().next().equals(new Cur(2, 1)));
        assertFalse(map.iterator().next().equals(new Cur(2, 2)));
        map.clear();
        assertNull(map.put(null, 1));
        assertFalse(map.iterator().next().equals(new Cur(null, null)));
        assertTrue(map.iterator().next().equals(new Cur(null, 1)));
        assertFalse(map.iterator().next().equals(new Cur(null, 2)));
        assertFalse(map.iterator().next().equals(new Cur(1, null)));
        assertFalse(map.iterator().next().equals(new Cur(1, 1)));
        assertFalse(map.iterator().next().equals(new Cur(1, 2)));
        assertFalse(map.iterator().next().equals(new Cur(2, null)));
        assertFalse(map.iterator().next().equals(new Cur(2, 1)));
        assertFalse(map.iterator().next().equals(new Cur(2, 2)));
        map.clear();
        assertNull(map.put(1, 1));
        assertFalse(map.iterator().next().equals(new Cur(null, null)));
        assertFalse(map.iterator().next().equals(new Cur(null, 1)));
        assertFalse(map.iterator().next().equals(new Cur(null, 2)));
        assertFalse(map.iterator().next().equals(new Cur(1, null)));
        assertTrue(map.iterator().next().equals(new Cur(1, 1)));
        assertFalse(map.iterator().next().equals(new Cur(1, 2)));
        assertFalse(map.iterator().next().equals(new Cur(2, null)));
        assertFalse(map.iterator().next().equals(new Cur(2, 1)));
        assertFalse(map.iterator().next().equals(new Cur(2, 2)));
        map.clear();
        assertNull(map.put(1, null));
        assertFalse(map.iterator().next().equals(new Cur(null, null)));
        assertFalse(map.iterator().next().equals(new Cur(null, 1)));
        assertFalse(map.iterator().next().equals(new Cur(null, 2)));
        assertTrue(map.iterator().next().equals(new Cur(1, null)));
        assertFalse(map.iterator().next().equals(new Cur(1, 1)));
        assertFalse(map.iterator().next().equals(new Cur(1, 2)));
        assertFalse(map.iterator().next().equals(new Cur(2, null)));
        assertFalse(map.iterator().next().equals(new Cur(2, 1)));
        assertFalse(map.iterator().next().equals(new Cur(2, 2)));
    }

    @Test
    public void testCursorRemove() {
        final OOMap<BadHashCode,Integer> map = create();
        final BadHashCode[] arr = new BadHashCode[10];
        for (int i = 0; i < 10; ++i) {
            assertNull(map.put(arr[i] = new BadHashCode(4), i));
        }
        assertEquals(10, map.size());

        int i = 10;
        for (final OOCursor<BadHashCode, Integer> x : map) {
            assertEquals(i--, map.size());
            x.remove();
            assertEquals(i, map.size());
        }
        assertEquals(0, map.size());
    }

    /*
     * The map has 8 elements and can insert 5 before it expands.
     * We're inserting 5 elements with hc 5 which should wrap the
     * last two in the backing array and trigger a
     * copy when removing the element.
     *
     * 55xxx555
     *      ^ remove this one
     */
    @Test
    public void testCursorRemoveCopyWrap() {
        final OOMap<BadHashCode,Integer> map = create();
        for (int i = 0; i < 5; ++i) {
            assertNull(map.put(new BadHashCode(5), i));
        }
        assertEquals(5, map.size());

        final OOCursor<BadHashCode, Integer> it = map.iterator();
        it.next().next();
        for (final OOCursor<BadHashCode, Integer> x : it) {
            x.remove();
        }
    }

    @Test
    public void testEntrySetContains() {
        final OOMap<Integer,Integer> map = create();
        assertNull(map.put(null, null));
        assertFalse(map.entrySet().contains(new En<>(map, 1, 1)));
        assertFalse(map.entrySet().contains(new En<>(map, 1, null)));
        assertFalse(map.entrySet().contains(new En<>(map, null, 1)));
        assertTrue(map.entrySet().contains(new En<>(map, null, null)));
        map.clear();
        assertNull(map.put(null, 1));
        assertFalse(map.entrySet().contains(new En<>(map, null, null)));
        assertTrue(map.entrySet().contains(new En<>(map, null, 1)));
        assertFalse(map.entrySet().contains(new En<>(map, null, 2)));
        assertFalse(map.entrySet().contains(new En<>(map, 1, null)));
        assertFalse(map.entrySet().contains(new En<>(map, 1, 1)));
        map.clear();
        assertNull(map.put(1, null));
        assertFalse(map.entrySet().contains(new En<>(map, null, null)));
        assertFalse(map.entrySet().contains(new En<>(map, null, 1)));
        assertFalse(map.entrySet().contains(new En<>(map, null, 2)));
        assertTrue(map.entrySet().contains(new En<>(map, 1, null)));
        assertFalse(map.entrySet().contains(new En<>(map, 1, 1)));
        assertFalse(map.entrySet().contains(new En<>(map, 2, null)));
        assertFalse(map.entrySet().contains(new En<>(map, 2, 1)));
        map.clear();
        assertNull(map.put(1, 1));
        assertFalse(map.entrySet().contains(new En<>(map, null, null)));
        assertFalse(map.entrySet().contains(new En<>(map, null, 1)));
        assertFalse(map.entrySet().contains(new En<>(map, null, 2)));
        assertFalse(map.entrySet().contains(new En<>(map, 1, null)));
        assertTrue(map.entrySet().contains(new En<>(map, 1, 1)));
        assertFalse(map.entrySet().contains(new En<>(map, 2, null)));
        assertFalse(map.entrySet().contains(new En<>(map, 2, 1)));
    }

    @Test
    public void testEqualsNullKey() {
        final OOMap<Integer,Integer> map = vmap(null, 1);
        assertFalse(map.equals(jmap(null, null)));
        assertTrue(map.equals(jmap(null, 1)));
        assertFalse(map.equals(jmap(1,null)));
        assertFalse(map.equals(jmap(1,1)));
        assertFalse(map.equals(jmap(1,2)));
        assertFalse(map.equals(jmap(2,2)));
    }

    @Test
    public void testEqualsNullValue() {
        final OOMap<Integer,Integer> map = vmap(1, null);
        assertFalse(map.equals(jmap(null, null)));
        assertFalse(map.equals(jmap(null, 1)));
        assertTrue(map.equals(jmap(1,null)));
        assertFalse(map.equals(jmap(1,1)));
        assertFalse(map.equals(jmap(1,2)));
        assertFalse(map.equals(jmap(2,2)));
    }

    @Test
    public void testEqualsNullKeyValue() {
        final OOMap<Integer,Integer> map = vmap(null, null);
        assertTrue(map.equals(jmap(null, null)));
        assertFalse(map.equals(jmap(null, 1)));
        assertFalse(map.equals(jmap(1,null)));
        assertFalse(map.equals(jmap(1,1)));
        assertFalse(map.equals(jmap(1,2)));
        assertFalse(map.equals(jmap(2,2)));
    }

    @Test
    public void testEqualsKeyValue() {
        final OOMap<Integer,Integer> map = vmap(1, 1);
        assertFalse(map.equals(jmap(null, null)));
        assertFalse(map.equals(jmap(null, 1)));
        assertFalse(map.equals(jmap(1,null)));
        assertTrue(map.equals(jmap(1,1)));
        assertFalse(map.equals(jmap(1,2)));
        assertFalse(map.equals(jmap(2,2)));
    }

    public static class Cur implements OOCursor<Integer, Integer> {

        final Integer k, v;

        private Cur(final Integer k, final Integer v) {
            this.k = k;
            this.v = v;
        }

        @Override
        public Integer key() {
            return k;
        }

        @Override
        public Integer value() {
            return v;
        }

        @Override
        public void reset() {}

        @Override
        public Iterator<OOCursor<Integer, Integer>> iterator() {
            return null;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public OOCursor<Integer, Integer> next() {
            return this;
        }

        @Override
        public void remove() {}
    }
}
