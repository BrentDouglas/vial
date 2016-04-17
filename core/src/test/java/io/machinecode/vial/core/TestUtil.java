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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestUtil {

    public static <K,V, M extends Map<K,V>> M populate(final M c, Map.Entry<K,V>[] entries) {
        for (final Map.Entry<K,V> entry : entries) {
            c.put(entry.getKey(), entry.getValue());
        }
        return c;
    }

    @SuppressWarnings("unchecked")
    public static <V, S extends Set<V>> S populate(final S c, Object[] entries) {
        for (final Object entry : entries) {
            c.add((V) entry);
        }
        return c;
    }

    @SuppressWarnings("unchecked")
    public static <V, S extends Set<V>> S populate(final S c, Collection<?> entries) {
        for (final Object entry : entries) {
            c.add((V) entry);
        }
        return c;
    }

    @SuppressWarnings("unchecked")
    public static <V, S extends List<V>> S populate(final S c, Object[] entries) {
        for (final Object entry : entries) {
            c.add((V) entry);
        }
        return c;
    }

    @SuppressWarnings("unchecked")
    public static <V, S extends List<V>> S populate(final S c, Collection<?> entries) {
        for (final Object entry : entries) {
            c.add((V) entry);
        }
        return c;
    }

    public static boolean arrayContains(final Object[] a, final Object v) {
        for (final Object x : a) {
            if (v == null) {
                if (x == null) return true;
            } else {
                if (v.equals(x)) return true;
            }
        }
        return false;
    }
}
