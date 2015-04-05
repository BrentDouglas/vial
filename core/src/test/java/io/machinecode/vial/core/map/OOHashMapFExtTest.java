package io.machinecode.vial.core.map;

import io.machinecode.vial.api.Spread;
import io.machinecode.vial.api.map.OOCursor;
import io.machinecode.vial.api.map.OOMap;
import io.machinecode.vial.core.BadHashCode;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OOHashMapFExtTest extends OOHashMapExtTest {

    @Override
    protected <K,V> OOMap<K,V> create(final float factor) {
        return new OOHashMapF<>(factor);
    }

    @Override
    protected <K,V> OOMap<K,V> create() {
        return new OOHashMapF<>();
    }

    @Override
    protected void doTestConstructors() {
        final OOHashMapF<Long, Long> a = new OOHashMapF<>(4);
        final OOHashMapF<Long, Long> b = new OOHashMapF<>(0.5f);
        final OOHashMapF<Long, Long> c = new OOHashMapF<>(4, 0.5f);
        final OOHashMapF<Long, Long> d = new OOHashMapF<>(4, 0.5f, Spread.MURMUR3);
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
        final OOHashMapF<Long, Long> e = new OOHashMapF<>(a);
        assertEquals(a, e);
        assertEquals(2, e.size());
        assertTrue(e.containsKey(1L));
        assertTrue(e.containsKey(2L));
        assertTrue(e.containsValue(2L));
        assertTrue(e.containsValue(3L));
    }
}
