package io.machinecode.vial.api;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MurmurSpreadTest extends SpreadTest {
    @Override
    protected Spread create() {
        return Spread.MURMUR3;
    }
}
