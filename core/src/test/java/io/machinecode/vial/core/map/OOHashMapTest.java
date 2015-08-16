/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package io.machinecode.vial.core.map;

import io.machinecode.vial.api.Spread;
import io.machinecode.vial.core.Spreads;
import io.machinecode.vial.api.map.OOMap;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OOHashMapTest {

    public static Test suite() {
        final TestSuite suite = new TestSuite(OOHashMap.class.getName());
        for (final Spreads spread : Spreads.values()) {
            OOMapSuite.createSuite(suite, OOHashMap.class, spread.name(), new OOMapSuite.CreateMap() {
                @Override
                public <K, V> OOMap<K, V> make() {
                    return new OOHashMap<>(4, 0.75f, spread);
                }

                @Override
                public <K, V> OOMap<K, V> create() {
                    return new OOHashMap<>();
                }

                @Override
                public <K, V> OOMap<K, V> create(int cap) {
                    return new OOHashMap<>(cap);
                }

                @Override
                public <K, V> OOMap<K, V> create(float factor) {
                    return new OOHashMap<>(factor);
                }

                @Override
                public <K, V> OOMap<K, V> create(int cap, float factor) {
                    return new OOHashMap<>(cap, factor);
                }

                @Override
                public <K, V> OOMap<K, V> create(final int cap, final float factor, final Spread spread) {
                    return new OOHashMap<>(cap, factor, spread);
                }

                @Override
                public <K, V> OOMap<K, V> create(Map<K, V> map) {
                    return new OOHashMap<>(map);
                }
            });
        }
        return suite;
    }
}
