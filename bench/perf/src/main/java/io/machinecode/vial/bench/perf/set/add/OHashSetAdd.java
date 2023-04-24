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
package io.machinecode.vial.bench.perf.set.add;

import com.carrotsearch.hppc.ObjectSet;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.koloboke.collect.hash.HashConfig;
import com.koloboke.collect.set.hash.HashObjSets;
import gnu.trove.set.hash.THashSet;
import io.machinecode.tools.bench.BaseBench;
import io.machinecode.vial.core.set.OHashSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
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
public class OHashSetAdd {

  public static void main(String... args) throws Exception {
    BaseBench.run(OHashSetAdd.class);
  }

  @Param({"0.75"})
  float factor;

  @Param({"8", "1000000"})
  int capacity;

  private Random r;

  private Set<Long> vial;
  private Set<Long> jdk;
  private Set<Long> trove;
  private Set<Long> fastutil;
  private ObjectSet<Long> hppc;
  private Set<Long> koloboke;
  private Set<Long> gs;

  @Setup(Level.Iteration)
  public void init() {
    r = new Random();
    r.setSeed(0x654265);
    vial = new OHashSet<>(capacity, factor);
    jdk = new HashSet<>(capacity, factor);
    trove = new THashSet<>(capacity, factor);
    fastutil = new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet<>(capacity);
    hppc = new com.carrotsearch.hppc.ObjectHashSet<>(capacity, factor);
    koloboke =
        HashObjSets.getDefaultFactory()
            .withHashConfig(
                HashConfig.fromLoads(Math.max(factor / 2, 0.1), factor, Math.min(factor * 2, 0.9)))
            .newMutableSet(capacity);
    gs = new UnifiedSet<>(capacity, factor);
  }

  @Benchmark
  public boolean vial() {
    final Long key = r.nextLong();
    return vial.add(key);
  }

  @Benchmark
  public boolean jdk() {
    final Long key = r.nextLong();
    return jdk.add(key);
  }

  @Benchmark
  public boolean trove() {
    final Long key = r.nextLong();
    return trove.add(key);
  }

  @Benchmark
  public boolean fastutil() {
    final Long key = r.nextLong();
    return fastutil.add(key);
  }

  @Benchmark
  public boolean hppc() {
    final Long key = r.nextLong();
    return hppc.add(key);
  }

  @Benchmark
  public boolean koloboke() {
    final Long key = r.nextLong();
    return koloboke.add(key);
  }

  @Benchmark
  public boolean gs() {
    final Long key = r.nextLong();
    return gs.add(key);
  }
}
