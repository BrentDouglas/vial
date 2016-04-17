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
package io.machinecode.vial.bench.mem;

import com.google.caliper.memory.ObjectGraphMeasurer;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;

import java.lang.instrument.Instrumentation;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Memory {

    private static volatile Instrumentation instrumentation;

    public static void premain(final String args, final Instrumentation instrumentation) {
        Memory.instrumentation = instrumentation;
    }

    public static void agentmain(final String args, final Instrumentation instrumentation) {
        Memory.instrumentation = instrumentation;
    }

    public static void printSizeOf(final String name, final Object that) {
        assert instrumentation != null;
        System.out.println(name + " " + that.getClass().getSimpleName());
        System.out.println("Instrumentation");
        System.out.println("size: " + instrumentation.getObjectSize(that));
        System.out.println("Caliper ObjectGraphMeasurer");
        System.out.println(ObjectGraphMeasurer.measure(that));
        System.out.println("JOL GraphLayout");
        final GraphLayout gl = GraphLayout.parseInstance(that.getClass());
        System.out.println(gl.toFootprint());
        System.out.println(gl.toPrintable());
        System.out.println(gl.totalSize());
        System.out.println("JOL ClassLayout");
        final ClassLayout cl = ClassLayout.parseClass(that.getClass());
        System.out.println(cl.toPrintable(that));
        System.out.println(cl.instanceSize());
        System.out.println(cl.headerSize());
    }
}
