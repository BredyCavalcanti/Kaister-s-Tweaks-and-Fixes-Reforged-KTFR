# Kaister's Tweaks and Fixes Reforged (KTFR)

[![Build JAR](https://img.shields.io/badge/Build%20JAR-Run%20on%20GitHub-2ea44f?style=for-the-badge&logo=github)](https://github.com/BredyCavalcanti/Kaister-s-Tweaks-and-Fixes-Reforged-KTFR/actions/workflows/build.yml)

A collection of small fixes, compatibility patches, and quality-of-life tweaks for Minecraft.

## Project status

The current source in this repository targets **Minecraft 1.20.1 with Forge**. The repository is being prepared for a future multi-loader build, but a single Forge + NeoForge JAR is **not available yet**. Do not treat the current Forge JAR as NeoForge-compatible.

The existing optional integrations are gated by mod presence through the Mixin compatibility plugin. Their target mod versions still need runtime testing.

## Build the JAR

### Build on your computer

Requirements:
- Java 17 (JDK)
- Internet connection on the first build so Gradle can download dependencies

**Windows (PowerShell or CMD):**
```bat
gradlew.bat clean build
```

**Linux / macOS:**
```bash
chmod +x gradlew
./gradlew clean build
```

The ForgeGradle reobfuscated JAR is generated at:

```
build/reobfJar/output.jar
```

The `build/` directory is generated automatically and is intentionally not tracked by Git.

### Build using GitHub Actions

1. Open the [Build JAR workflow](https://github.com/BredyCavalcanti/Kaister-s-Tweaks-and-Fixes-Reforged-KTFR/actions/workflows/build.yml).
2. Select **Run workflow** and choose the branch you want to build.
3. Wait for the build to finish.
4. Open the successful run and download the **KTFR-Forge-1.20.1** artifact.

The workflow also runs automatically when code is pushed or a pull request is opened. Build artifacts are retained by GitHub Actions for 14 days.

## Repository notes

- `src/main/java/`: mod source code.
- `src/main/resources/`: mod metadata, Mixin configuration, and resources.
- `libs/`: compile-time-only dependency used by the current Simple Storage Network integration. It is not bundled into the mod JAR.
- `gradle/`, `gradlew`, and `gradlew.bat`: Gradle wrapper, so contributors do not need to install Gradle manually.
- `LICENSE`: Kaister Mod License (KML) v1.0. Read it before using, modifying, or redistributing this project.

## Development workflow

- Keep generated files (especially `build/`, IDE metadata, logs, and local run directories) out of commits.
- Make changes on a feature or maintenance branch instead of directly on `main`.
- Check the GitHub Actions build before considering a change ready.
- Optional Mixin integrations must be tested both with and without their target mods installed.

## License

This project is distributed under the **Kaister Mod License (KML) v1.0**. It is a custom source-available license, not an OSI-approved open-source license. See [LICENSE](LICENSE) for the complete terms.
