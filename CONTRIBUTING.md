# Contributing to KTFR

Thanks for helping improve Kaister's Tweaks and Fixes Reforged.

## Before changing code

- Read the repository [LICENSE](LICENSE). Contributions, modifications, and redistribution are subject to its terms and require the author's permission.
- Open an issue or discuss the proposed change before starting substantial work.
- Keep pull requests focused; avoid mixing unrelated fixes.

## Build and verify

Use Java 17 and the Gradle wrapper:

- Windows: `gradlew.bat clean build`
- Linux/macOS: `./gradlew clean build`

The reobfuscated Forge JAR is written to `build/reobfJar/output.jar`. Check that the GitHub Actions build succeeds before submitting a change.

## Compatibility changes

For optional integrations, verify both cases:
1. The target mod is installed at the supported version.
2. The target mod is absent.

Include the Minecraft version, loader, target-mod version, reproduction steps, and relevant logs in bug reports.
