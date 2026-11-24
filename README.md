# Kaister's Tweaks and Fixes Reforged (KTFR)

[![Build Universal JAR](https://img.shields.io/badge/Build%20Universal%20JAR-GitHub%20Actions-2ea44f?style=for-the-badge&logo=github)](https://github.com/BredyCavalcanti/Kaister-s-Tweaks-and-Fixes-Reforged-KTFR/actions/workflows/build.yml)

A collection of small fixes, compatibility patches, and quality-of-life tweaks for Minecraft.

## Compatibility targets

The project is being migrated to a **single distributable JAR** with two isolated loader payloads:

| Minecraft | Loader | Build module | Status |
|---|---|---|---|
| 1.20.1 | Forge | Root project | Existing implementation |
| 1.21.1 | NeoForge | `:neoforge` | Port in progress |

The universal archive is assembled from both loader builds. This is not achieved by pretending Forge and NeoForge APIs are identical: each environment has its own metadata, entrypoint, and loader-specific implementation, while the release workflow packages them into one file.

**Important:** the NeoForge module is currently only a bootstrap scaffold. The universal artifact is experimental until the existing compatibility mixins have been ported and the same final JAR has passed startup tests on both loaders. Do not use it as a finished release yet.

## Build

Requirements:
- JDK 21 (the Forge 1.20.1 subproject selects Java 17 through its toolchain).
- Internet connection on the first build so Gradle can download dependencies.

**Windows:**
```bat
gradlew.bat clean universalJar
```

**Linux / macOS:**
```bash
./gradlew clean universalJar
```

The combined archive is generated in `build/libs/` with the `-universal.jar` classifier.

To build only the existing Forge payload:
```bash
./gradlew :jar
```

To build only the NeoForge 1.21.1 bootstrap:
```bash
./gradlew :neoforge:build
```

## GitHub Actions

The [Build Universal JAR workflow](https://github.com/BredyCavalcanti/Kaister-s-Tweaks-and-Fixes-Reforged-KTFR/actions/workflows/build.yml) runs on pushes, pull requests, and manual dispatch. It builds both modules and uploads an experimental universal artifact for 14 days.

## Repository layout

- `src/main/java/` and `src/main/resources/`: Forge 1.20.1 implementation.
- `neoforge/`: NeoForge 1.21.1 implementation and metadata.
- `libs/`: compile-time-only Simple Storage Network dependency for the Forge 1.20.1 integration. It is not bundled into the mod.
- `.github/workflows/`: build and repository maintenance automation.
- `LICENSE`: Kaister Mod License (KML) v1.0. Read it before using, modifying, or redistributing this project.

## Development workflow

- Keep generated files (`build/`, IDE metadata, logs, and local run directories) out of commits.
- Make changes on a feature or maintenance branch instead of directly on `main`.
- Keep loader-specific classes isolated. Avoid linking Forge-only classes from NeoForge code or vice versa.
- A change is ready only after both module builds pass and the final combined JAR is tested in actual Forge and NeoForge game instances.
- Optional Mixin integrations must be tested both with and without their target mods installed.

## License

This project is distributed under the **Kaister Mod License (KML) v1.0**. It is a custom source-available license, not an OSI-approved open-source license. See [LICENSE](LICENSE) for the complete terms.
