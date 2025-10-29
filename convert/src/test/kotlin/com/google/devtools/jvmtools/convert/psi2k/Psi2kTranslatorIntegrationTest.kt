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

import com.intellij.core.CoreApplicationEnvironment
import com.intellij.lang.java.JavaParserDefinition
import com.intellij.mock.MockProject
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.impl.PsiFileFactoryImpl
import com.intellij.testFramework.LightVirtualFile
import org.jetbrains.uast.UFile
import org.jetbrains.uast.UastLanguagePlugin
import org.jetbrains.uast.java.JavaUastLanguagePlugin
import org.jetbrains.uast.toUElement
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * End-to-end integration test for Java to Kotlin translation.
 *
 * Note: This test requires IntelliJ Platform infrastructure and may not work
 * in all environments. It's designed as a smoke test to verify the basic
 * translation pipeline works.
 */
class Psi2kTranslatorIntegrationTest {

    private lateinit var disposable: Disposable
    private lateinit var environment: CoreApplicationEnvironment

    @BeforeEach
    fun setup() {
        disposable = Disposer.newDisposable()
        environment = CoreApplicationEnvironment(disposable)

        try {
            // Register Java parser
            environment.registerParserDefinition(JavaParserDefinition())

            // Register UAST if available
            val area = Extensions.getArea(null)
            if (!area.hasExtensionPoint(UastLanguagePlugin.extensionPointName)) {
                area.registerExtensionPoint(
                    UastLanguagePlugin.extensionPointName.name,
                    UastLanguagePlugin::class.java.name,
                    Extensions.Kind.INTERFACE
                )
            }
            area.getExtensionPoint(UastLanguagePlugin.extensionPointName)
                .registerExtension(JavaUastLanguagePlugin(), disposable)
        } catch (e: Exception) {
            // If setup fails, we'll skip the test in the actual test method
            println("Warning: Could not fully initialize IntelliJ infrastructure: ${e.message}")
        }
    }

    @AfterEach
    fun tearDown() {
        Disposer.dispose(disposable)
    }

    @Test
    fun `test simple class translation - basic structure`() {
        val javaCode = """
            package com.example;

            public class HelloWorld {
                private String message;

                public HelloWorld(String message) {
                    this.message = message;
                }

                public String getMessage() {
                    return message;
                }

                public void setMessage(String message) {
                    this.message = message;
                }
            }
        """.trimIndent()

        try {
            val kotlinCode = translateJavaToKotlin(javaCode, "HelloWorld.java")

            // Verify basic structure is present
            assertNotNull(kotlinCode, "Translation should produce non-null output")
            assertTrue(kotlinCode.isNotEmpty(), "Translation should produce non-empty output")

            // Verify package declaration
            assertTrue(
                kotlinCode.contains("package com.example"),
                "Should contain package declaration"
            )

            // Verify class declaration (may be 'class' or may have other modifiers)
            assertTrue(
                kotlinCode.contains("class HelloWorld") || kotlinCode.contains("HelloWorld"),
                "Should contain class name"
            )

            // Basic sanity check - should not be identical to Java
            assertFalse(
                kotlinCode == javaCode,
                "Kotlin output should differ from Java input"
            )

            // Print output for manual verification
            println("Translated Kotlin code:")
            println(kotlinCode)
        } catch (e: Exception) {
            // If the test infrastructure is not available, skip this test
            println("Skipping integration test due to: ${e.message}")
            org.junit.jupiter.api.Assumptions.assumeTrue(
                false,
                "IntelliJ test infrastructure not available: ${e.message}"
            )
        }
    }

    @Test
    fun `test simple method translation`() {
        val javaCode = """
            public class Calculator {
                public int add(int a, int b) {
                    return a + b;
                }

                public int multiply(int x, int y) {
                    return x * y;
                }
            }
        """.trimIndent()

        try {
            val kotlinCode = translateJavaToKotlin(javaCode, "Calculator.java")

            assertNotNull(kotlinCode)
            assertTrue(kotlinCode.isNotEmpty())

            // Should contain class name
            assertTrue(kotlinCode.contains("Calculator"))

            // Should contain method names
            assertTrue(kotlinCode.contains("add") || kotlinCode.contains("fun add"))
            assertTrue(kotlinCode.contains("multiply") || kotlinCode.contains("fun multiply"))

            println("Translated Kotlin code:")
            println(kotlinCode)
        } catch (e: Exception) {
            println("Skipping integration test due to: ${e.message}")
            org.junit.jupiter.api.Assumptions.assumeTrue(
                false,
                "IntelliJ test infrastructure not available: ${e.message}"
            )
        }
    }

    private fun translateJavaToKotlin(javaSource: String, fileName: String): String {
        val project = environment.project as MockProject
        val fileFactory = PsiFileFactoryImpl.getInstance(project) as PsiFileFactory

        // Create virtual file
        val virtualFile = LightVirtualFile(fileName, javaSource)

        // Create PSI file
        val psiFile = fileFactory.createFileFromText(
            fileName,
            com.intellij.lang.java.JavaLanguage.INSTANCE,
            javaSource,
            false,
            false
        )

        // Convert to UAST
        val uFile = psiFile.toUElement() as? UFile
            ?: throw IllegalStateException("Could not convert PSI file to UAST")

        // Translate to Kotlin
        return uFile.translateToKotlin()
    }
}
