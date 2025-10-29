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

package com.google.devtools.jvmtools.analysis

import com.google.devtools.jvmtools.analysis.nullness.Nullness
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Smoke tests for Value extension functions.
 */
class ValueExtensionsTest {

  @Test
  fun `nullable join with both null returns null`() {
    val result: Nullness? = null join null
    assertNull(result)
  }

  @Test
  fun `nullable join with left null returns right`() {
    val result: Nullness? = null join Nullness.NONULL
    assertEquals(Nullness.NONULL, result)
  }

  @Test
  fun `nullable join with right null returns left`() {
    val result: Nullness? = Nullness.NONULL join null
    assertEquals(Nullness.NONULL, result)
  }

  @Test
  fun `nullable join with both non-null performs regular join`() {
    val result: Nullness? = Nullness.NONULL join Nullness.NULL
    assertEquals(Nullness.NULLABLE, result)
  }

  @Test
  fun `nullable join treats null as bottom`() {
    // null acts as BOTTOM, so joining with any value returns that value
    assertEquals(Nullness.NULLABLE, null join Nullness.NULLABLE)
    assertEquals(Nullness.PARAMETRIC, null join Nullness.PARAMETRIC)
    assertEquals(Nullness.BOTTOM, null join Nullness.BOTTOM)
  }
}
