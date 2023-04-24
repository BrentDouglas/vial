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
package io.machinecode.vial.core;

import io.machinecode.vial.api.Spread;

/**
 * @author <a href="mailto:brent.douglas@gmail.com">Brent Douglas</a>
 */
public enum Spreads implements Spread {
  NONE {
    @Override
    public final int spread(final int h) {
      return h;
    }
  },
  QUICK {
    @Override
    public final int spread(final int h) {
      return h ^ h >>> 16;
    }
  },
  MURMUR3 {
    @Override
    public final int spread(int h) {
      h ^= h >> 16;
      h *= 0x85ebca6b;
      h ^= h >> 13;
      h *= 0xc2b2ae35;
      h ^= h >> 16;
      return h;
    }
  }
}
