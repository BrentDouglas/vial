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
package io.machinecode.vial.bench.perf.map.put;

import com.carrotsearch.hppc.LongObjectMap;
import com.gs.collections.api.map.primitive.MutableLongObjectMap;
import com.gs.collections.impl.map.mutable.primitive.LongObjectHashMap;
import com.koloboke.collect.hash.HashConfig;
import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.vial.api.map.LOMap;
import io.machinecode.vial.core.map.LOHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.SingleShotTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, batchSize = 1000000)
@Measurement(iterations = 20, batchSize = 1000000)
@Timeout(time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class LOHashMapPut {

    @Param({"0.75"})
    float factor;

    @Param({"8", "1000000"})
    int capacity;

    private Random r;

    private Map<Long,Long> jdk;
    private LOMap<Long> vial;
    private TLongObjectMap<Long> trove;
    private Long2ObjectMap<Long> fastutil;
    private LongObjectMap<Long> hppc;
    private HashLongObjMap<Long> koloboke;
    private MutableLongObjectMap<Long> gs;

    @Setup(Level.Iteration)
    public void init() {
        r = new Random();
        r.setSeed(0x654265);
        jdk = new HashMap<>(capacity, factor);
        vial = new LOHashMap<>(capacity, factor);
        trove = new TLongObjectHashMap<>(capacity, factor);
        fastutil = new Long2ObjectOpenHashMap<>(capacity);
        hppc = new com.carrotsearch.hppc.LongObjectHashMap<>(capacity, factor);
        koloboke = HashLongObjMaps.getDefaultFactory()
                .withHashConfig(HashConfig.fromLoads(Math.max(factor / 2, 0.1), factor, Math.min(factor * 2, 0.9)))
                .newMutableMap(capacity);
        gs = new LongObjectHashMap<>(capacity);
    }

    @Benchmark
    public Long jdk() {
        final Long key = r.nextLong();
        return jdk.put(key, key);
    }

    @Benchmark
    public Long vial() {
        final long key = r.nextLong();
        return vial.put(key, (Long)key);
    }

    @Benchmark
    public Long trove() {
        final long key = r.nextLong();
        return trove.put(key, key);
    }

    @Benchmark
    public Long fastutil() {
        final long key = r.nextLong();
        return fastutil.put(key, (Long)key);
    }

    @Benchmark
    public Long hppc() {
        final long key = r.nextLong();
        return hppc.put(key, key);
    }

    @Benchmark
    public Long koloboke() {
        final long key = r.nextLong();
        return koloboke.put(key, (Long)key);
    }

    @Benchmark
    public Long gs() {
        final long key = r.nextLong();
        return gs.put(key, key);
    }
}
