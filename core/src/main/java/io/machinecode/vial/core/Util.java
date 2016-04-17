/*
 * Copyright (C) 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.vial.core;

import java.util.Arrays;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Util {

    private static final int LINE_SIZE = 1024; //Integer.decode(System.getProperty("io.machinecode.vial.line.size","1024"));

    /**
     * Fill the specified segment of an array with the provided value. Initializes a small amount of values
     * directly, larger values are them copied using {@link System#arraycopy(Object, int, Object, int, int)}
     * on the previous section in order to make sure the accessed values are in the L1 cache.
     *
     * @param array The array to fill. Must not be null.
     * @param start The first index of the array to be set to value inclusive.
     * @param end The last index of the array to be set to value exclusive.
     * @param value The value to put in the array.
     */
    public static void fill(final Object[] array, final int start, final int end, final Object value) {
        assert start >= 0 && start <= end;
        assert end <= array.length;
        assert array.length == 0 || array.length == pow2(array.length);
        final int n = LINE_SIZE;
        int i = Math.min(n, end);
        Arrays.fill(array, start, i, value);
        while (i < end) {
            System.arraycopy(array, i - n, array, i, n);
            i += n;
        }
    }

    /**
     * Compute an acceptable size for the backing array of a hash collection.
     *
     * @param size The amount items required to fit in the array before rehashing. Must be larger than 0.
     * @param factor The load factor of the array.
     * @param max The largest acceptable array size. Must be a power of two.
     * @return The lesser of next power of two greater than or equal to size / factor or max.
     */
    public static int capacity(final int size, final float factor, final int max) {
        assert size > 0;
        int c = (int) Math.ceil(size / factor);
        c--;
        c |= c >> 1;
        c |= c >> 2;
        c |= c >> 4;
        c |= c >> 8;
        c |= c >> 16;
        c++;
        return c >= max ? max : c;
    }

    public static int pow2(int c) {
        assert c > 0;
        c--;
        c |= c >> 1;
        c |= c >> 2;
        c |= c >> 4;
        c |= c >> 8;
        c |= c >> 16;
        c++;
        return c;
    }
}
