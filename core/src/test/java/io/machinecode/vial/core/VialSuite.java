/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.vial.core;

import junit.framework.TestCase;

import java.util.HashMap;

/**
 * @author <a href="mailto:brent.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class VialSuite extends TestCase {

    final String spreadName;

    public VialSuite(final String method, final String spreadName) {
        super(method);
        this.spreadName = spreadName;
    }

    @Override
    public String toString() {
        if (spreadName == null) {
            return getName() + "(" + getClass().getName() + ")";
        }
        return getName() + "[" + spreadName+ "](" + getClass().getName() + ")";
    }

    protected <K,V> HashMap<K,V> jmap(final K k, final V v) {
        final HashMap<K,V> map = new HashMap<>();
        map.put(k, v);
        return map;
    }
}
