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

import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.SingleShotTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10)
@Measurement(iterations = 20)
@Timeout(time = 10, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class IntBlankClear {

    @Param({"536870912"})
    int capacity;

    @Param({"1024", "2048", "4096"})
    int N;

    private int[] blank;
    private int[] array;

    @Setup(Level.Trial)
    public void init() {
        array = new int[capacity];
        blank = new int[N];
    }

    @Benchmark
    public int blank() {
        for (int i = 0, dl = array.length, sl = blank.length; i < dl; i += sl) {
            System.arraycopy(blank, 0, array, i, Math.min(sl, dl - i));
        }
        return array.length;
    }
}
