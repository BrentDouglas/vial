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
package io.machinecode.vial.core.set;

import io.machinecode.vial.api.Spread;
import io.machinecode.vial.api.set.OSet;
import io.machinecode.vial.core.Spreads;
import java.util.Collection;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OHashSetTest {

  public static Test suite() {
    final TestSuite suite = new TestSuite(OHashSet.class.getName());
    for (final Spreads spread : Spreads.values()) {
      OSetSuite.createSuite(
          suite,
          OHashSet.class,
          spread.name(),
          new OSetSuite.CreateSet() {
            @Override
            public <K> OSet<K> make() {
              return new OHashSet<>(4, 0.75f, spread);
            }

            @Override
            public <K> OSet<K> create() {
              return new OHashSet<>();
            }

            @Override
            public <K> OSet<K> create(int cap) {
              return new OHashSet<>(cap);
            }

            @Override
            public <K> OSet<K> create(float factor) {
              return new OHashSet<>(factor);
            }

            @Override
            public <K> OSet<K> create(int cap, float factor) {
              return new OHashSet<>(cap, factor);
            }

            @Override
            public <K> OSet<K> create(int cap, float factor, Spread spread) {
              return new OHashSet<>(cap, factor, spread);
            }

            @Override
            public <K> OSet<K> create(K[] set) {
              return new OHashSet<>(set);
            }

            @Override
            public <K> OSet<K> create(Collection<K> set) {
              return new OHashSet<>(set);
            }
          });
    }
    return suite;
  }
}
