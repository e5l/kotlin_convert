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

package com.google.devtools.jvmtools.convert.psi2k

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Smoke tests for [MappedMethod] data structures.
 */
class MappedMethodTest {

  @Test
  fun `MappedMethod can be created`() {
    val method = MappedMethod(
      className = "java.lang.String",
      javaMethodName = "length",
      kotlinName = "length",
      type = MappingType.PROPERTY
    )

    assertEquals("java.lang.String", method.className)
    assertEquals("length", method.javaMethodName)
    assertEquals("length", method.kotlinName)
    assertEquals(MappingType.PROPERTY, method.type)
  }

  @Test
  fun `MappingType enum has expected values`() {
    assertEquals(3, MappingType.entries.size)
    assertNotNull(MappingType.DIRECT)
    assertNotNull(MappingType.PROPERTY)
    assertNotNull(MappingType.EXTENSION)
  }

  @Test
  fun `MappedMethod get returns null for non-existent method`() {
    // Without PSI infrastructure, we can't test the full lookup
    // but we can verify the method doesn't crash with null class
    val result = MappedMethod.get("nonExistentMethod", null)
    assertNull(result)
  }

  @Test
  fun `MappedMethod get handles null class`() {
    // Should not crash with null class
    assertDoesNotThrow {
      MappedMethod.get("length", null)
      MappedMethod.get("size", null)
      MappedMethod.get("getBytes", null)
    }
  }

  @Test
  fun `MappedMethod equality works`() {
    val method1 = MappedMethod(
      className = "java.lang.String",
      javaMethodName = "length",
      kotlinName = "length",
      type = MappingType.PROPERTY
    )

    val method2 = MappedMethod(
      className = "java.lang.String",
      javaMethodName = "length",
      kotlinName = "length",
      type = MappingType.PROPERTY
    )

    val method3 = MappedMethod(
      className = "java.lang.String",
      javaMethodName = "getBytes",
      kotlinName = "toByteArray",
      type = MappingType.DIRECT
    )

    assertEquals(method1, method2)
    assertNotEquals(method1, method3)
  }

  @Test
  fun `MappedMethod copy works`() {
    val original = MappedMethod(
      className = "java.lang.String",
      javaMethodName = "length",
      kotlinName = "length",
      type = MappingType.PROPERTY
    )

    val copy = original.copy(kotlinName = "size")

    assertEquals("java.lang.String", copy.className)
    assertEquals("length", copy.javaMethodName)
    assertEquals("size", copy.kotlinName)
    assertEquals(MappingType.PROPERTY, copy.type)
  }
}
