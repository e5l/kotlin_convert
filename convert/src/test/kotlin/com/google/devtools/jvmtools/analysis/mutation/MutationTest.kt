/*
 * Copyright 2025 Google LLC
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

package com.google.devtools.jvmtools.analysis.mutation

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Smoke tests for [Mutation] lattice operations.
 */
class MutationTest {

  @Test
  fun `UNUSED is bottom`() {
    assertTrue(Mutation.UNUSED.isBottom)
    assertFalse(Mutation.READ.isBottom)
    assertFalse(Mutation.MODIFIED.isBottom)
  }

  @Test
  fun `UNUSED implies all values`() {
    assertTrue(Mutation.UNUSED.implies(Mutation.UNUSED))
    assertTrue(Mutation.UNUSED.implies(Mutation.READ))
    assertTrue(Mutation.UNUSED.implies(Mutation.MODIFIED))
  }

  @Test
  fun `READ implies READ and MODIFIED`() {
    assertFalse(Mutation.READ.implies(Mutation.UNUSED))
    assertTrue(Mutation.READ.implies(Mutation.READ))
    assertTrue(Mutation.READ.implies(Mutation.MODIFIED))
  }

  @Test
  fun `MODIFIED implies only itself`() {
    assertFalse(Mutation.MODIFIED.implies(Mutation.UNUSED))
    assertFalse(Mutation.MODIFIED.implies(Mutation.READ))
    assertTrue(Mutation.MODIFIED.implies(Mutation.MODIFIED))
  }

  @Test
  fun `join with UNUSED returns other value`() {
    assertEquals(Mutation.UNUSED, Mutation.UNUSED.join(Mutation.UNUSED))
    assertEquals(Mutation.READ, Mutation.UNUSED.join(Mutation.READ))
    assertEquals(Mutation.MODIFIED, Mutation.UNUSED.join(Mutation.MODIFIED))
  }

  @Test
  fun `join with READ returns max`() {
    assertEquals(Mutation.READ, Mutation.READ.join(Mutation.UNUSED))
    assertEquals(Mutation.READ, Mutation.READ.join(Mutation.READ))
    assertEquals(Mutation.MODIFIED, Mutation.READ.join(Mutation.MODIFIED))
  }

  @Test
  fun `join with MODIFIED returns MODIFIED`() {
    assertEquals(Mutation.MODIFIED, Mutation.MODIFIED.join(Mutation.UNUSED))
    assertEquals(Mutation.MODIFIED, Mutation.MODIFIED.join(Mutation.READ))
    assertEquals(Mutation.MODIFIED, Mutation.MODIFIED.join(Mutation.MODIFIED))
  }

  @Test
  fun `join is commutative`() {
    for (a in Mutation.entries) {
      for (b in Mutation.entries) {
        assertEquals(a.join(b), b.join(a), "join($a, $b) should be commutative")
      }
    }
  }

  @Test
  fun `join is idempotent`() {
    for (m in Mutation.entries) {
      assertEquals(m, m.join(m), "join($m, $m) should equal $m")
    }
  }

  @Test
  fun `total order property`() {
    // In a total order, for any two values, one must imply the other
    val mutations = Mutation.entries
    assertTrue(mutations[0].ordinal < mutations[1].ordinal)
    assertTrue(mutations[1].ordinal < mutations[2].ordinal)
  }
}
