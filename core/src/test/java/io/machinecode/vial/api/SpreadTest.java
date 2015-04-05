package io.machinecode.vial.api;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SpreadTest extends Assert {

    protected Spread create() {
        return Spread.NONE;
    }

    @Test
    public void testSpread() {
        final Spread spread = create();
        //TODO For now just test it doesn't throw or something
        spread.spread(4);
    }

}
