# Build Troubleshooting Guide

## Common Issues and Solutions

### Issue: "Plugin [id: 'org.jetbrains.kotlin.jvm'] was not found"

**Symptom:**
```
Plugin [id: 'org.jetbrains.kotlin.jvm', version: '2.0.21', apply: false] was not found in any of the following sources:
- Gradle Central Plugin Repository
- Plugin Repositories (could not resolve plugin artifact...)
```

**Cause:**
- Network connectivity issues
- Repository access restrictions
- First-time build requires downloading dependencies

**Solutions:**

1. **Ensure network connectivity:**
   ```bash
   # Test connectivity
   curl -I https://plugins.gradle.org
   curl -I https://repo.maven.apache.org/maven2/
   ```

2. **Clear Gradle cache and retry:**
   ```bash
   rm -rf ~/.gradle/caches
   ./gradlew build --refresh-dependencies
   ```

3. **Use Gradle daemon with network access:**
   ```bash
   ./gradlew build --no-daemon
   ```

4. **Check proxy settings (if behind corporate proxy):**
   ```bash
   # Add to ~/.gradle/gradle.properties
   systemProp.http.proxyHost=proxy.company.com
   systemProp.http.proxyPort=8080
   systemProp.https.proxyHost=proxy.company.com
   systemProp.https.proxyPort=8080
   ```

### Issue: Tests failing to compile

**Solutions:**

1. **Ensure JDK 11 is installed:**
   ```bash
   java -version  # Should show version 11 or higher
   ```

2. **Clean and rebuild:**
   ```bash
   ./gradlew clean build
   ```

3. **Check for dependency conflicts:**
   ```bash
   ./gradlew dependencies
   ```

### Issue: IntelliJ Platform dependencies not resolving

**Cause:**
The integration tests require IntelliJ Platform artifacts from JetBrains repositories.

**Solutions:**

1. **Ensure JetBrains repositories are accessible:**
   ```bash
   curl -I https://www.jetbrains.com/intellij-repository/releases/
   ```

2. **Skip integration tests if needed:**
   ```bash
   ./gradlew test -x :convert:test --tests '*Test' --tests '!*IntegrationTest'
   ```

3. **Run only unit tests:**
   ```bash
   ./gradlew test --tests 'com.google.devtools.jvmtools.analysis.*'
   ```

## Running Tests in Different Environments

### Local Development
```bash
# Full test suite
./gradlew test

# Specific test class
./gradlew test --tests NullnessTest

# With debug output
./gradlew test --info
```

### CI/CD (GitHub Actions)
Tests run automatically on:
- Pushes to `main` or `master` branches
- Pull requests targeting `main` or `master`

Manual trigger:
- Go to Actions tab in GitHub
- Select "Tests" workflow
- Click "Run workflow"

### Docker/Container Environments
```bash
# Ensure DNS resolution works
echo "nameserver 8.8.8.8" > /etc/resolv.conf

# Run with network host mode if needed
docker run --network=host ...
```

## Verifying Build Configuration

1. **Check Gradle wrapper is executable:**
   ```bash
   ls -l gradlew
   chmod +x gradlew
   ```

2. **Verify Gradle version:**
   ```bash
   ./gradlew --version
   ```

3. **List available tasks:**
   ```bash
   ./gradlew tasks
   ```

4. **Dry run to see what would execute:**
   ```bash
   ./gradlew test --dry-run
   ```

## Getting Help

If issues persist:

1. **Generate full stack trace:**
   ```bash
   ./gradlew test --stacktrace > build-error.log 2>&1
   ```

2. **Check build scan:**
   ```bash
   ./gradlew test --scan
   ```

3. **Enable debug logging:**
   ```bash
   ./gradlew test --debug > debug.log 2>&1
   ```

4. **Verify network access to required repositories:**
   - https://plugins.gradle.org/m2/
   - https://repo.maven.apache.org/maven2/
   - https://www.jetbrains.com/intellij-repository/releases/
   - https://cache-redirector.jetbrains.com/intellij-dependencies/
