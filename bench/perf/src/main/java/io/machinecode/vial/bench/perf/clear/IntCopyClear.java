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
package io.machinecode.vial.bench.perf.clear;

import io.machinecode.tools.bench.BaseBench;
import java.util.Arrays;
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
@Warmup(iterations = 10)
@Measurement(iterations = 20)
@Timeout(time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class IntCopyClear {

  public static void main(String... args) throws Exception {
    BaseBench.run(IntCopyClear.class);
  }

  @Param({"536870912"})
  int capacity;

  @Param({"512", "1024", "2048", "4096"})
  int N;

  private int[] array;

  @Setup(Level.Trial)
  public void init() {
    array = new int[capacity];
  }

  @Benchmark
  public int copy() {
    final int n = N;
    final int[] array = this.array;
    final int end = array.length;
    int i = Math.min(n, end);
    Arrays.fill(array, 0, i, -1);
    while (i < end) {
      System.arraycopy(array, i - n, array, i, n);
      i += n;
    }
    return array.length;
  }

  @Benchmark
  public int imp() {
    final int n = N;
    final int[] array = this.array;
    final int sl = array.length;
    int i = Math.min(8, sl);
    Arrays.fill(array, 0, i, -1);
    int x = Math.min(n, sl);
    int r = i;
    while (i < x) {
      System.arraycopy(array, i - r, array, i, r);
      i += r;
      r += r;
    }
    while (i < sl) {
      System.arraycopy(array, i - n, array, i, n);
      i += n;
    }
    return array.length;
  }
}
