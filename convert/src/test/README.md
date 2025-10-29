# Tests

## Running Tests

To run all tests:
```bash
./gradlew test
```

## Test Structure

### Unit Tests
- **NullnessTest.kt** - Tests for nullness lattice operations
- **MutationTest.kt** - Tests for mutation analysis lattice
- **ValueExtensionsTest.kt** - Tests for value extension functions
- **MappedMethodTest.kt** - Tests for method mapping data structures

### Integration Tests
- **TranslationSmokeTest.kt** - Basic smoke tests for translation infrastructure
- **Psi2kTranslatorIntegrationTest.kt** - End-to-end integration tests (requires IntelliJ Platform)

## Notes

The integration tests require the IntelliJ Platform PSI/UAST infrastructure. They will:
- Skip gracefully if the infrastructure is not available
- Require network connectivity for first-time dependency download
- Work best in environments with full IntelliJ Platform support

For CI/CD environments, ensure:
1. Network connectivity for Gradle dependency resolution
2. Sufficient memory for IntelliJ Platform initialization
3. JDK 11 or later
