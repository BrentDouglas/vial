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
package io.machinecode.vial.bench.mem.set;

import com.carrotsearch.hppc.ObjectSet;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import gnu.trove.set.hash.THashSet;
import io.machinecode.vial.bench.mem.Memory;
import io.machinecode.vial.core.set.OHashSet;
import net.openhft.koloboke.collect.hash.HashConfig;
import net.openhft.koloboke.collect.set.hash.HashObjSets;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OHashSetMemoryTest {

    private static final float FACTOR = 0.75f;
    private static final int CAPACITY = 1000;
    private static final int SEED = 0x654265;

    @Test
    public void testJdk() {
        _test("jdk", new HashSet<Long>(CAPACITY, FACTOR));
    }

    @Test
    public void testVial() {
        _test("vial", new OHashSet<Long>(CAPACITY, FACTOR));
    }

    @Test
    public void testTrove() {
        _test("trove", new THashSet<Long>(CAPACITY, FACTOR));
    }

    @Test
    public void testFastUtil() {
        _test("fastutil", new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<Long>(CAPACITY));
    }

    @Test
    public void testHppc() {
        final Random r = new Random();
        r.setSeed(SEED);
        final ObjectSet<Long> hppc = new com.carrotsearch.hppc.ObjectOpenHashSet<>(CAPACITY, FACTOR);
        for (int i = 0; i < CAPACITY; ++i) {
            hppc.add(r.nextLong());
        }
        Memory.printSizeOf("hppc", hppc);
    }

    @Test
    public void testKoloboke() {
        _test("koloboke", HashObjSets.getDefaultFactory()
                .withHashConfig(HashConfig.fromLoads(Math.max(FACTOR / 2, 0.1), FACTOR, Math.min(FACTOR * 2, 0.9)))
                .<Long>newMutableSet(CAPACITY));
    }

    @Test
    public void testGs() {
        _test("gs", new UnifiedSet<Long>(CAPACITY, FACTOR));
    }

    private static void _test(final String name, final Set<Long> that) {
        fill(that);
        Memory.printSizeOf(name, that);
    }

    private static void fill(final Set<Long> that) {
        final Random r = new Random();
        r.setSeed(SEED);
        for (int i = 0; i < CAPACITY; ++i) {
            that.add(r.nextLong());
        }
    }
}
