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

import com.carrotsearch.hppc.ObjectObjectHashMap;
import com.carrotsearch.hppc.ObjectObjectMap;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.koloboke.collect.hash.HashConfig;
import com.koloboke.collect.map.hash.HashObjObjMaps;
import gnu.trove.map.hash.THashMap;
import io.machinecode.tools.bench.BaseBench;
import io.machinecode.vial.core.map.OOHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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

@BenchmarkMode({Mode.SingleShotTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, batchSize = 1000000)
@Measurement(iterations = 20, batchSize = 1000000)
@Timeout(time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class OOHashMapPut {

  public static void main(String... args) throws Exception {
    BaseBench.run(OOHashMapPut.class);
  }

  @Param({"0.75"})
  float factor;

  @Param({"8", "1000000"})
  int capacity;

  private Random r;

  private Map<Long, Long> vial;
  private Map<Long, Long> jdk;
  private Map<Long, Long> trove;
  private Map<Long, Long> fastutil;
  private ObjectObjectMap<Long, Long> hppc;
  private Map<Long, Long> koloboke;
  private Map<Long, Long> gs;

  @Setup(Level.Iteration)
  public void init() {
    r = new Random();
    r.setSeed(0x654265);
    jdk = new HashMap<>(capacity, factor);
    vial = new OOHashMap<>(capacity, factor);
    trove = new THashMap<>(capacity, factor);
    fastutil = new Object2ObjectArrayMap<>(capacity);
    hppc = new ObjectObjectHashMap<>(capacity, factor);
    koloboke =
        HashObjObjMaps.getDefaultFactory()
            .withHashConfig(
                HashConfig.fromLoads(Math.max(factor / 2, 0.1), factor, Math.min(factor * 2, 0.9)))
            .newMutableMap(capacity);
    gs = new UnifiedMap<>(capacity, factor);
  }

  @Benchmark
  public Long jdk() {
    final Long key = r.nextLong();
    return jdk.put(key, key);
  }

  @Benchmark
  public Long vial() {
    final Long key = r.nextLong();
    return vial.put(key, key);
  }

  @Benchmark
  public Long trove() {
    final Long key = r.nextLong();
    return trove.put(key, key);
  }

  @Benchmark
  public Long fastutil() {
    final Long key = r.nextLong();
    return fastutil.put(key, key);
  }

  @Benchmark
  public Long hppc() {
    final Long key = r.nextLong();
    return hppc.put(key, key);
  }

  @Benchmark
  public Long koloboke() {
    final Long key = r.nextLong();
    return koloboke.put(key, key);
  }

  @Benchmark
  public Long gs() {
    final Long key = r.nextLong();
    return gs.put(key, key);
  }
}
