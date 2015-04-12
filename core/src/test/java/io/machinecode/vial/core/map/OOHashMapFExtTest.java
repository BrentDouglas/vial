package io.machinecode.vial.core.map;

import io.machinecode.vial.api.Spread;
import io.machinecode.vial.api.map.OOCursor;
import io.machinecode.vial.api.map.OOMap;
import io.machinecode.vial.core.BadHashCode;

import java.util.HashMap;

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
        final OOHashMapF<Integer, Integer> a = new OOHashMapF<>(4);
        final OOHashMapF<Integer, Integer> b = new OOHashMapF<>(0.5f);
        final OOHashMapF<Integer, Integer> c = new OOHashMapF<>(4, 0.5f);
        final OOHashMapF<Integer, Integer> d = new OOHashMapF<>(4, 0.5f, Spread.MURMUR3);
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
        a.put(1, 2);
        a.put(2, 3);
        assertEquals(2, a.size());

        final OOHashMapF<Integer, Integer> e = new OOHashMapF<>(a);
        assertEquals(a, e);
        assertEquals(2, e.size());
        assertTrue(e.containsKey(1));
        assertTrue(e.containsKey(2));
        assertTrue(e.containsValue(2));
        assertTrue(e.containsValue(3));

        final OOHashMapF<Integer, Integer> f = new OOHashMapF<>(new HashMap<Integer,Integer>(){{
            put(1, 2);
            put(2, 3);
        }});
        assertEquals(a, f);
        assertEquals(2, f.size());
        assertTrue(f.containsKey(1));
        assertTrue(f.containsKey(2));
        assertTrue(f.containsValue(2));
        assertTrue(f.containsValue(3));
    }
}
