/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.jvmtools.analysis.nullness

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Smoke tests for [Nullness] lattice operations.
 */
class NullnessTest {

  @Test
  fun `BOTTOM is bottom`() {
    assertTrue(Nullness.BOTTOM.isBottom)
    assertFalse(Nullness.NONULL.isBottom)
    assertFalse(Nullness.NULL.isBottom)
    assertFalse(Nullness.NULLABLE.isBottom)
    assertFalse(Nullness.PARAMETRIC.isBottom)
  }

  @Test
  fun `BOTTOM implies all values`() {
    assertTrue(Nullness.BOTTOM.implies(Nullness.BOTTOM))
    assertTrue(Nullness.BOTTOM.implies(Nullness.NONULL))
    assertTrue(Nullness.BOTTOM.implies(Nullness.NULL))
    assertTrue(Nullness.BOTTOM.implies(Nullness.NULLABLE))
    assertTrue(Nullness.BOTTOM.implies(Nullness.PARAMETRIC))
  }

  @Test
  fun `NONULL implies correct values`() {
    assertFalse(Nullness.NONULL.implies(Nullness.BOTTOM))
    assertTrue(Nullness.NONULL.implies(Nullness.NONULL))
    assertFalse(Nullness.NONULL.implies(Nullness.NULL))
    assertTrue(Nullness.NONULL.implies(Nullness.NULLABLE))
    assertTrue(Nullness.NONULL.implies(Nullness.PARAMETRIC))
  }

  @Test
  fun `NULL implies correct values`() {
    assertFalse(Nullness.NULL.implies(Nullness.BOTTOM))
    assertFalse(Nullness.NULL.implies(Nullness.NONULL))
    assertTrue(Nullness.NULL.implies(Nullness.NULL))
    assertTrue(Nullness.NULL.implies(Nullness.NULLABLE))
    assertFalse(Nullness.NULL.implies(Nullness.PARAMETRIC))
  }

  @Test
  fun `PARAMETRIC implies correct values`() {
    assertFalse(Nullness.PARAMETRIC.implies(Nullness.BOTTOM))
    assertFalse(Nullness.PARAMETRIC.implies(Nullness.NONULL))
    assertFalse(Nullness.PARAMETRIC.implies(Nullness.NULL))
    assertTrue(Nullness.PARAMETRIC.implies(Nullness.NULLABLE))
    assertTrue(Nullness.PARAMETRIC.implies(Nullness.PARAMETRIC))
  }

  @Test
  fun `NULLABLE implies only itself`() {
    assertFalse(Nullness.NULLABLE.implies(Nullness.BOTTOM))
    assertFalse(Nullness.NULLABLE.implies(Nullness.NONULL))
    assertFalse(Nullness.NULLABLE.implies(Nullness.NULL))
    assertTrue(Nullness.NULLABLE.implies(Nullness.NULLABLE))
    assertFalse(Nullness.NULLABLE.implies(Nullness.PARAMETRIC))
  }

  @Test
  fun `join with BOTTOM returns other value`() {
    assertEquals(Nullness.NONULL, Nullness.BOTTOM.join(Nullness.NONULL))
    assertEquals(Nullness.NULL, Nullness.BOTTOM.join(Nullness.NULL))
    assertEquals(Nullness.NULLABLE, Nullness.BOTTOM.join(Nullness.NULLABLE))
    assertEquals(Nullness.PARAMETRIC, Nullness.BOTTOM.join(Nullness.PARAMETRIC))
    assertEquals(Nullness.BOTTOM, Nullness.BOTTOM.join(Nullness.BOTTOM))
  }

  @Test
  fun `join NONULL with NULL yields NULLABLE`() {
    assertEquals(Nullness.NULLABLE, Nullness.NONULL.join(Nullness.NULL))
    assertEquals(Nullness.NULLABLE, Nullness.NULL.join(Nullness.NONULL))
  }

  @Test
  fun `join NONULL with PARAMETRIC yields PARAMETRIC`() {
    assertEquals(Nullness.PARAMETRIC, Nullness.NONULL.join(Nullness.PARAMETRIC))
    assertEquals(Nullness.PARAMETRIC, Nullness.PARAMETRIC.join(Nullness.NONULL))
  }

  @Test
  fun `join with NULLABLE yields NULLABLE`() {
    assertEquals(Nullness.NULLABLE, Nullness.NONULL.join(Nullness.NULLABLE))
    assertEquals(Nullness.NULLABLE, Nullness.NULL.join(Nullness.NULLABLE))
    assertEquals(Nullness.NULLABLE, Nullness.PARAMETRIC.join(Nullness.NULLABLE))
    assertEquals(Nullness.NULLABLE, Nullness.NULLABLE.join(Nullness.NULLABLE))
  }

  @Test
  fun `join is commutative`() {
    for (a in Nullness.entries) {
      for (b in Nullness.entries) {
        assertEquals(a.join(b), b.join(a), "join($a, $b) should be commutative")
      }
    }
  }

  @Test
  fun `join is idempotent`() {
    for (n in Nullness.entries) {
      assertEquals(n, n.join(n), "join($n, $n) should equal $n")
    }
  }
}
