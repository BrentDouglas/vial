package io.machinecode.vial.bench.perf.clear;

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

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.SingleShotTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10)
@Measurement(iterations = 20)
@Timeout(time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class LongCopyClear {

    @Param({"268435456"})
    int capacity;

    @Param({"1024", "2048", "4096"})
    int N;

    private long[] array;

    @Setup(Level.Trial)
    public void init() {
        array = new long[capacity];
    }

    @Benchmark
    public int copy() {
        final int n = N;
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
