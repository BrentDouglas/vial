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

import com.gs.collections.api.map.primitive.MutableLongLongMap;
import com.gs.collections.impl.map.mutable.primitive.LongLongHashMap;
import com.koloboke.collect.hash.HashConfig;
import com.koloboke.collect.map.hash.HashLongLongMaps;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import io.machinecode.vial.api.map.LLMap;
import io.machinecode.vial.core.map.LLHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
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
public class LLHashMapPut {

    @Param({"0.75"})
    float factor;

    @Param({"8", "1000000"})
    int capacity;

    private Random r;

    private Map<Long,Long> jdk;
    private LLMap vial;
    private TLongLongMap trove;
    private Long2LongMap fastutil;
    private com.carrotsearch.hppc.LongLongMap hppc;
    private com.koloboke.collect.map.LongLongMap koloboke;
    private MutableLongLongMap gs;

    @Setup(Level.Iteration)
    public void init() {
        r = new Random();
        r.setSeed(0x654265);
        vial = new LLHashMap(capacity, factor);
        jdk = new HashMap<>(capacity, factor);
        trove = new TLongLongHashMap(capacity, factor);
        fastutil = new Long2LongOpenHashMap(capacity, factor);
        hppc = new com.carrotsearch.hppc.LongLongHashMap(capacity, factor);
        koloboke = HashLongLongMaps.getDefaultFactory()
                .withHashConfig(HashConfig.fromLoads(Math.max(factor / 2, 0.1), factor, Math.min(factor * 2, 0.9)))
                .newMutableMap();
        gs = new LongLongHashMap(capacity);
    }

    @Benchmark
    public Long jdk() {
        final long key = r.nextLong();
        return jdk.put(key, key);
    }

    @Benchmark
    public Long vial() {
        final long key = r.nextLong();
        return vial.put(key, key);
    }

    @Benchmark
    public long xvial() {
        final long key = r.nextLong();
        return vial.xput(key, key);
    }

    @Benchmark
    public long trove() {
        final long key = r.nextLong();
        return trove.put(key, key);
    }

    @Benchmark
    public long fastutil() {
        final long key = r.nextLong();
        return fastutil.put(key, key);
    }

    @Benchmark
    public long hppc() {
        final long key = r.nextLong();
        return hppc.put(key, key);
    }

    @Benchmark
    public long koloboke() {
        final long key = r.nextLong();
        return koloboke.put(key, key);
    }

    @Benchmark
    public long gs() {
        final long key = r.nextLong();
        gs.put(key, key);
        return key;
    }
}
