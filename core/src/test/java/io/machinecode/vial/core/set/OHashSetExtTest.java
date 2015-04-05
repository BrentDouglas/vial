package io.machinecode.vial.core.set;

import io.machinecode.vial.api.Spread;
import io.machinecode.vial.api.set.OCursor;
import io.machinecode.vial.api.set.OSet;
import io.machinecode.vial.core.BadHashCode;
import io.machinecode.vial.core.TestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OHashSetExtTest extends Assert {

    protected <V> OSet<V> create() {
        return new OHashSet<>();
    }

    @Test
    public void testConstructors() {
        final OHashSet<Long> a = new OHashSet<>(4);
        final OHashSet<Long> b = new OHashSet<>(0.5f);
        final OHashSet<Long> c = new OHashSet<>(4, 0.5f);
        final OHashSet<Long> d = new OHashSet<>(4, 0.5f, Spread.MURMUR3);
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
        a.add(1L);
        a.add(2L);
        assertEquals(2, a.size());

        final OHashSet<Long> e = new OHashSet<>(a);
        assertEquals(a, e);
        assertEquals(2, e.size());
        assertTrue(e.contains(1L));
        assertTrue(e.contains(2L));

        final OHashSet<Long> f = new OHashSet<>(new Long[]{2L, 1L});
        assertEquals(a, f);
        assertEquals(2, f.size());
        assertTrue(f.contains(1L));
        assertTrue(f.contains(2L));
    }

    @Test
    public void testNull() {
        final OSet<Long> set = create();
        assertTrue(set.add(null));
        assertEquals(1, set.size());
        assertTrue(set.contains(null));

        assertTrue(set.remove(null));
        assertEquals(0, set.size());
        assertFalse(set.contains(null));
    }

    @Test
    public void testValue() {
        final OSet<Long> set = create();
        assertTrue(set.add(1L));
        assertEquals(1, set.size());
        assertTrue(set.contains(1L));

        assertTrue(set.remove(1L));
        assertEquals(0, set.size());
        assertFalse(set.contains(1L));
    }

    @Test
    public void testAddWithRehash() {
        final OSet<Long> set = create();
        for (long i = 0; i < 10; ++i) {
            assertTrue(set.add(i));
        }
        assertEquals(10, set.size());
        for (long i = 0; i < 10; ++i) {
            assertTrue(set.contains(i));
        }
    }

    @Test
    public void testClear() {
        final OSet<Long> set = create();
        for (long i = 0; i < 10; ++i) {
            assertTrue(set.add(i));
        }
        assertEquals(10, set.size());
        set.clear();
        assertEquals(0, set.size());
        for (long i = 0; i < 10; ++i) {
            assertFalse(set.contains(i));
        }
        for (long i = 0; i < 10; ++i) {
            assertTrue(set.add(i));
        }
    }

    @Test
    public void testRemoveValue() {
        final OSet<Long> set = create();
        for (long i = 0; i < 10; ++i) {
            assertTrue(set.add(i));
        }
        assertEquals(10, set.size());
        for (long i = 0; i < 10; ++i) {
            assertTrue(set.contains(i));

            assertTrue(set.remove(i));
            assertFalse(set.remove(i));
        }
        assertEquals(0, set.size());
        for (long i = 0; i < 10; ++i) {
            assertTrue(set.add(i));
        }
    }

    @Test
    public void testRemoveBadHashCode() {
        final OSet<BadHashCode> set = create();
        final BadHashCode[] arr = new BadHashCode[10];
        for (int i = 0; i < 10; ++i) {
            assertTrue(set.add(arr[i] = new BadHashCode(4)));
        }
        assertEquals(10, set.size());
        //This will make sure removed keys need moving
        for (int i = 0; i < 5; ++i) {
            assertTrue(set.contains(arr[i]));

            assertTrue(set.remove(arr[i]));
            assertFalse(set.remove(arr[i]));
        }
        //This will make it skip keys before removing them
        for (int i = 9; i > 4; --i) {
            assertTrue(set.contains(arr[i]));

            assertTrue(set.remove(arr[i]));
            assertFalse(set.remove(arr[i]));
        }
        assertEquals(0, set.size());
        for (int i = 0; i < 10; ++i) {
            assertTrue(set.add(arr[i]));
        }
    }

    @Test
    public void testRemoveNull() {
        final OSet<Long> set = create();
        assertTrue(set.add(null));
        assertTrue(set.contains(null));
        assertEquals(1, set.size());

        assertTrue(set.remove(null));
        assertFalse(set.contains(null));
        assertEquals(0, set.size());
    }

    @Test
    public void testRetainAllNull() {
        final OSet<Long> set = create();
        assertTrue(set.add(null));
        assertTrue(set.add(1L));
        assertTrue(set.add(2L));
        assertTrue(set.contains(null));
        assertTrue(set.contains(1L));
        assertTrue(set.contains(2L));
        assertEquals(3, set.size());

        assertTrue(set.retainAll(null, 2L));
        assertFalse(set.contains(1L));
        assertTrue(set.contains(null));
        assertTrue(set.contains(2L));
        assertEquals(2, set.size());

        assertTrue(set.retainAll(null, 3L));
        assertFalse(set.contains(1L));
        assertFalse(set.contains(2L));
        assertTrue(set.contains(null));
        assertEquals(1, set.size());

        try {
            set.retainAll((Object[])null);
            fail();
        } catch (final NullPointerException e) {}
    }

    @Test
    public void testRetainAllValue() {
        final OSet<Long> set = create();
        assertTrue(set.add(null));
        assertTrue(set.add(1L));
        assertTrue(set.add(2L));
        assertTrue(set.contains(null));
        assertTrue(set.contains(1L));
        assertTrue(set.contains(2L));
        assertEquals(3, set.size());

        assertTrue(set.retainAll(1L, 2L));
        assertTrue(set.contains(1L));
        assertFalse(set.contains(null));
        assertTrue(set.contains(2L));
        assertEquals(2, set.size());

        assertTrue(set.retainAll(1L, 3L));
        assertTrue(set.contains(1L));
        assertFalse(set.contains(2L));
        assertFalse(set.contains(null));
        assertEquals(1, set.size());
    }

    @Test
    public void testRemoveAll() {
        final OSet<Long> set = create();
        assertTrue(set.add(null));
        assertTrue(set.add(1L));
        assertTrue(set.add(2L));
        assertTrue(set.add(3L));
        assertTrue(set.contains(null));
        assertTrue(set.contains(1L));
        assertTrue(set.contains(2L));
        assertTrue(set.contains(3L));
        assertEquals(4, set.size());

        assertTrue(set.removeAll(null, 2L));
        assertFalse(set.contains(null));
        assertTrue(set.contains(1L));
        assertFalse(set.contains(2L));
        assertTrue(set.contains(3L));
        assertEquals(2, set.size());

        assertTrue(set.removeAll(3L));
        assertFalse(set.contains(null));
        assertTrue(set.contains(1L));
        assertFalse(set.contains(2L));
        assertFalse(set.contains(3L));
        assertEquals(1, set.size());
    }

    @Test
    public void testContainsAll() {
        final OSet<Long> set = create();
        assertTrue(set.add(null));
        assertTrue(set.add(1L));
        assertTrue(set.add(2L));
        assertTrue(set.add(3L));
        assertTrue(set.contains(null));
        assertTrue(set.contains(1L));
        assertTrue(set.contains(2L));
        assertTrue(set.contains(3L));
        assertEquals(4, set.size());

        assertTrue(set.containsAll(null, 1L, 2L, 3L));
        assertTrue(set.containsAll(1L, 2L, 3L));
        assertTrue(set.containsAll(null, 1L, 2L));
        assertTrue(set.containsAll(null, 2L, 3L));
        assertTrue(set.containsAll(null, 1L, 3L));
        assertTrue(set.containsAll(null, 1L));
        assertTrue(set.containsAll(null, 2L));
        assertTrue(set.containsAll(null, 3L));
        assertTrue(set.containsAll(1L, 2L));
        assertTrue(set.containsAll(1L, 3L));
        assertTrue(set.containsAll(2L, 3L));
        assertTrue(set.containsAll(new Object[]{null}));
        assertTrue(set.containsAll(1L));
        assertTrue(set.containsAll(2L));
        assertTrue(set.containsAll(3L));

        assertFalse(set.containsAll(null, 1L, 2L, 3L, 4L));
        assertFalse(set.containsAll(null, 1L, 2L, 4L));
        assertFalse(set.containsAll(4L));
    }

    @Test
    public void testCursor() {
        final OSet<Long> set = create();

        for (long i = 0; i < 10; ++i) {
            assertTrue(set.add(i));
        }

        final OCursor<Long> c = set.cursor();
        assertTrue(c.hasNext());
        for (final Long x : set) {
            assertNotNull(c.next());
            assertEquals(x, c.value());
        }
        assertFalse(c.hasNext());

        c.reset();

        int i = 0;
        for (final OCursor<Long> x : c) {
            ++i;
            assertSame(x, c);
            assertEquals(x, c);
            assertEquals(x.toString(), c.toString());
        }
        assertEquals(10, i);
    }

    @Test
    public void testToArray() {
        final OSet<Long> set = create();
        assertTrue(set.add(null));
        assertTrue(set.add(1L));
        assertTrue(set.add(2L));
        assertTrue(set.add(3L));
        assertTrue(set.contains(null));
        assertTrue(set.contains(1L));
        assertTrue(set.contains(2L));
        assertTrue(set.contains(3L));
        assertEquals(4, set.size());
        
        final Object[] array = set.toArray();
        assertTrue(TestUtil.arrayContains(array, null));
        assertTrue(TestUtil.arrayContains(array, 1L));
        assertTrue(TestUtil.arrayContains(array, 2L));
        assertTrue(TestUtil.arrayContains(array, 3l));
        assertEquals(4, array.length);
    }

    @Test
    public void testToArrayT() {
        final OSet<Long> set = create();
        assertTrue(set.add(null));
        assertTrue(set.add(1L));
        assertTrue(set.add(2L));
        assertTrue(set.add(3L));
        assertTrue(set.contains(null));
        assertTrue(set.contains(1L));
        assertTrue(set.contains(2L));
        assertTrue(set.contains(3L));
        assertEquals(4, set.size());

        final Long[] array = set.toArray(new Long[4]);
        assertTrue(TestUtil.arrayContains(array, null));
        assertTrue(TestUtil.arrayContains(array, 1L));
        assertTrue(TestUtil.arrayContains(array, 2L));
        assertTrue(TestUtil.arrayContains(array, 3l));
        assertEquals(4, array.length);
    }

    /*
     * The set has 8 elements and can insert 5 before it expands.
     * We're inserting 5 elements with hc 5 which should wrap the
     * last two in the backing array and trigger a
     * copy when removing the element.
     *
     * 55xxx555
     *      ^ remove this one
     */
    @Test
    public void testCursorRemoveCopy() {
        final OSet<BadHashCode> set = create();
        for (int i = 0; i < 5; ++i) {
            assertTrue(set.add(new BadHashCode(5)));
        }
        assertEquals(5, set.size());

        final OCursor<BadHashCode> it = set.cursor();
        it.next().next();
        for (final OCursor<BadHashCode> x : it) {
            x.remove();
        }
    }
}
