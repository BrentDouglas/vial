/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.vial.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class UtilTest extends Assert {

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
        Util.fill(that, 0, that.length, 0);
        for (final Object val : that) {
            assertEquals(0, val);
        }
    }

    @Test
    public void testCapacity() {
        assertEquals(2, Util.capacity(1, 0.75f, 4));
        assertEquals(4, Util.capacity(3, 0.75f, 8));
        assertEquals(8, Util.capacity(4, 0.75f, 32));
        assertEquals(16, Util.capacity(7, 0.75f, 32));
    }

    @Test
    public void testMaxCapacity() {
        assertEquals(4, Util.capacity(10, 0.75f, 4));
    }
}
