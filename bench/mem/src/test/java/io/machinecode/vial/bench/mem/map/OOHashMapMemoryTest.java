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
package io.machinecode.vial.bench.mem.map;

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.koloboke.collect.hash.HashConfig;
import com.koloboke.collect.map.hash.HashObjObjMaps;
import gnu.trove.map.hash.THashMap;
import io.machinecode.vial.bench.mem.Memory;
import io.machinecode.vial.core.map.OOHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Test;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OOHashMapMemoryTest {

  private static final float FACTOR = 0.75f;
  private static final int CAPACITY = 1000;
  private static final int SEED = 0x654265;

  @Test
  public void testJdk() {
    _test("jdk", new HashMap<Long, Long>(CAPACITY, FACTOR));
  }

  @Test
  public void testVial() {
    _test("vial", new OOHashMap<Long, Long>(CAPACITY, FACTOR));
  }

  @Test
  public void testTrove() {
    _test("trove", new THashMap<Long, Long>(CAPACITY, FACTOR));
  }

  @Test
  public void testFastUtil() {
    _test("fastutil", new Object2ObjectOpenHashMap<Long, Long>(CAPACITY));
  }

  @Test
  public void testHppc() {
    final Random r = new Random();
    r.setSeed(SEED);
    final ObjectObjectMap<Long, Long> hppc = new ObjectObjectHashMap<>(CAPACITY, FACTOR);
    for (int i = 0; i < CAPACITY; ++i) {
      final long n = r.nextLong();
      hppc.put(n, n);
    }
    Memory.printSizeOf("hppc", hppc);
  }

  @Test
  public void testKoloboke() {
    _test(
        "koloboke",
        HashObjObjMaps.getDefaultFactory()
            .withHashConfig(
                HashConfig.fromLoads(Math.max(FACTOR / 2, 0.1), FACTOR, Math.min(FACTOR * 2, 0.9)))
            .<Long, Long>newMutableMap(CAPACITY));
  }

  @Test
  public void testGs() {
    _test("gs", new UnifiedMap<Long, Long>(CAPACITY, FACTOR));
  }

  private static void _test(final String name, final Map<Long, Long> that) {
    fill(that);
    Memory.printSizeOf(name, that);
  }

  private static void fill(final Map<Long, Long> that) {
    final Random r = new Random();
    r.setSeed(SEED);
    for (int i = 0; i < CAPACITY; ++i) {
      final long n = r.nextLong();
      that.put(n, n);
    }
  }
}
