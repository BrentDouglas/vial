package io.machinecode.vial.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class IllegalKeyTest extends Assert {

    @Test
    public void testToString() {
        assertEquals("Illegal Key", new IllegalKey().toString());
    }
}
