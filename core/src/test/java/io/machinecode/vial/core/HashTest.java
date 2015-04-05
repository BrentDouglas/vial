package io.machinecode.vial.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class HashTest extends Assert {

    @Test
    public void testFill() {
        _fill(0);
        _fill(1);
        _fill(2048);
        _fill(1048576);
    }

    private void _fill(final int size) {
        final Object[] that = new Object[size];
        Arrays.fill(that, 4);
        Hash.fill(that, 0, that.length, 0);
        for (final Object val : that) {
            assertEquals(0, val);
        }
    }

    @Test
    public void testCapacity() {
        assertEquals(2, Hash.capacity(1, 0.75f, 4));
        assertEquals(4, Hash.capacity(3, 0.75f, 8));
        assertEquals(8, Hash.capacity(4, 0.75f, 32));
        assertEquals(16, Hash.capacity(7, 0.75f, 32));
    }

    @Test
    public void testMaxCapacity() {
        assertEquals(4, Hash.capacity(10, 0.75f, 4));
    }

    @Test
    public void testIllegalKeyToString() {
        assertNotNull(Hash.ILLEGAL.toString());
    }
}
