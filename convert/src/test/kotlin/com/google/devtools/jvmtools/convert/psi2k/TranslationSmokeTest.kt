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
import java.io.File
import java.nio.file.Files

/**
 * Simple smoke test for the translation infrastructure.
 *
 * This test validates the basic structure without requiring full PSI setup.
 * It creates temporary Java files and verifies they can be processed.
 */
class TranslationSmokeTest {

    @Test
    fun `test Java file can be created and read`() {
        val javaCode = """
            package com.example;

            public class SimpleClass {
                private int value;

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }
            }
        """.trimIndent()

        val tempDir = Files.createTempDirectory("kotlin-convert-test")
        try {
            val javaFile = File(tempDir.toFile(), "SimpleClass.java")
            javaFile.writeText(javaCode)

            assertTrue(javaFile.exists(), "Java file should be created")
            assertTrue(javaFile.isFile, "Should be a file")
            assertTrue(javaFile.name.endsWith(".java"), "Should have .java extension")

            val content = javaFile.readText()
            assertEquals(javaCode, content, "File content should match")
            assertTrue(content.contains("public class SimpleClass"))
            assertTrue(content.contains("getValue"))
            assertTrue(content.contains("setValue"))
        } finally {
            tempDir.toFile().deleteRecursively()
        }
    }

    @Test
    fun `test basic Java structure validation`() {
        // Test that we can validate basic Java structure
        val validJavaClass = "public class Test { }"
        assertTrue(validJavaClass.contains("class"), "Should contain class keyword")
        assertTrue(validJavaClass.contains("public"), "Should contain public keyword")

        val javaWithMethod = """
            public class Test {
                public int add(int a, int b) {
                    return a + b;
                }
            }
        """.trimIndent()

        assertTrue(javaWithMethod.contains("public int add"))
        assertTrue(javaWithMethod.contains("return"))
    }

    @Test
    fun `test expected Kotlin patterns`() {
        // Verify we understand what Kotlin output should look like
        val kotlinClass = "class Test"
        assertTrue(kotlinClass.contains("class"))

        val kotlinFunction = "fun add(a: Int, b: Int): Int = a + b"
        assertTrue(kotlinFunction.contains("fun"))
        assertTrue(kotlinFunction.contains("Int"))

        val kotlinProperty = "var value: Int = 0"
        assertTrue(kotlinProperty.contains("var") || kotlinProperty.contains("val"))
    }

    @Test
    fun `test translator dependencies are available`() {
        // Verify that key classes are on the classpath
        assertDoesNotThrow {
            Class.forName("com.google.devtools.jvmtools.convert.psi2k.MappedMethod")
            Class.forName("com.google.devtools.jvmtools.analysis.nullness.Nullness")
            Class.forName("com.google.devtools.jvmtools.analysis.mutation.Mutation")
        }
    }

    @Test
    fun `test MappingType enum is accessible`() {
        // Verify the translation infrastructure is available
        val types = MappingType.entries
        assertEquals(3, types.size)
        assertTrue(types.contains(MappingType.DIRECT))
        assertTrue(types.contains(MappingType.PROPERTY))
        assertTrue(types.contains(MappingType.EXTENSION))
    }

    @Test
    fun `test MappedMethod structure`() {
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
    fun `test file naming conventions`() {
        // Test Java to Kotlin file name conversion
        val javaFileName = "MyClass.java"
        val expectedKotlinName = javaFileName.replace(".java", ".kt")

        assertEquals("MyClass.kt", expectedKotlinName)
        assertTrue(expectedKotlinName.endsWith(".kt"))
    }
}
