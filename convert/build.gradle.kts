// Copyright 2024 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("com.jetbrains.intellij.platform:uast:241.15989.155")
    implementation("org.jetbrains.kotlin:kotlin-compiler:1.9.22")

    // Test dependencies
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    // IntelliJ test infrastructure for integration tests
    testImplementation("com.jetbrains.intellij.platform:core:241.15989.155")
    testImplementation("com.jetbrains.intellij.platform:core-impl:241.15989.155")
    testImplementation("com.jetbrains.intellij.java:java-psi:241.15989.155")
    testImplementation("com.jetbrains.intellij.java:java-psi-impl:241.15989.155")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
