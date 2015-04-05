package io.machinecode.vial.core;

import java.util.Arrays;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Util {

    private static final int N = 1024; //TODO Make this adjustable?

    public static void fill(final Object[] array, final int start, final int end, final Object value) {
        assert start >= 0 && start <= end;
        assert end <= array.length;
        final int n = N;
        int i = Math.min(n, end);
        Arrays.fill(array, start, i, value);
        while (i < end) {
            System.arraycopy(array, i - n, array, i, n);
            i += n;
        }
    }
}
