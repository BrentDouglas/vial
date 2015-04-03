package io.machinecode.vial.bench.perf.map;

import com.carrotsearch.hppc.LongLongOpenHashMap;
import com.gs.collections.api.map.primitive.MutableLongLongMap;
import com.gs.collections.impl.map.mutable.primitive.LongLongHashMap;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import io.machinecode.vial.api.map.LLMap;
import io.machinecode.vial.core.map.LLHashMap;
import io.machinecode.vial.core.map.LLHashMapF;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.openhft.koloboke.collect.hash.HashConfig;
import net.openhft.koloboke.collect.map.hash.HashLongLongMaps;
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
import org.openjdk.jmh.annotations.Warmup;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode({Mode.SingleShotTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, batchSize = 1000000)
@Measurement(iterations = 20, batchSize = 1000000)
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
    private LLMap vialF;
    private TLongLongMap trove;
    private Long2LongMap fastutil;
    private com.carrotsearch.hppc.LongLongMap hppc;
    private net.openhft.koloboke.collect.map.LongLongMap koloboke;
    private MutableLongLongMap gs;

    @Setup(Level.Iteration)
    public void init() {
        r = new Random();
        r.setSeed(0x654265);
        vial = new LLHashMap(capacity, factor);
        vialF = new LLHashMapF(capacity, factor);
        jdk = new HashMap<>(capacity, factor);
        trove = new TLongLongHashMap(capacity, factor);
        fastutil = new Long2LongOpenHashMap(capacity, factor);
        hppc = new LongLongOpenHashMap(capacity, factor);
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
    public long vial() {
        final long key = r.nextLong();
        return vial.put(key, key);
    }

    @Benchmark
    public long vialF() {
        final long key = r.nextLong();
        return vialF.put(key, key);
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
    public void gs() {
        final long key = r.nextLong();
        gs.put(key, key);
    }
}
