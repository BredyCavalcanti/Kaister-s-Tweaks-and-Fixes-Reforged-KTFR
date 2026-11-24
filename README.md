# Kaister's Tweaks and Fixes Reforged (KTFR)

[![Build Universal JAR](https://img.shields.io/badge/Build%20Universal%20JAR-GitHub%20Actions-2ea44f?style=for-the-badge&logo=github)](https://github.com/BredyCavalcanti/Kaister-s-Tweaks-and-Fixes-Reforged-KTFR/actions/workflows/build.yml)

A collection of targeted fixes, compatibility patches, and quality-of-life tweaks for Minecraft. KTFR focuses on making existing mod interactions behave correctly without adding unnecessary content.

## Wiki

The project documentation is organized in the [KTFR Wiki](wiki/Home.md), including the [compatibility matrix](wiki/Compatibility.md) and [installation guide](wiki/Installation.md).

## Compatibility

| Minecraft | Loader | Module |
|---|---|---|
| 1.20.1 | Forge | Root project |
| 1.21.1 | NeoForge | `:neoforge` |

The project produces one **multiloader universal JAR** containing the separate Forge and NeoForge payloads. Each loader keeps its own entrypoint, metadata, and loader-specific mixins.

## Current fixes and integrations

### Simply Bows
- Integrates Simply Bows with **Infinity** and **Endless Quiver**, allowing supported bows to use quiver-based infinite ammunition.
- Prevents arrows fired with Infinity or Endless Quiver from being picked up.
- Preserves the original Simply Bows arrow appearance.
- Preserves Homing Arrow effects when potion effects are transferred.
- Includes compatibility with Relics and Supplementaries quivers on NeoForge.
- Includes additional handling for multi-projectile bows, including the Ice Bow's custom ammunition check.

### Simple Storage Network (Forge 1.20.1)
- Fixes shift-crafting with stackable ingredients and crafting remainders.
- Consumes only the required ingredient amount instead of replacing an entire stack with its container item.
- Returns container items to the player's inventory when possible.

### Other compatibility fixes
- **Curios:** guards against invalid slot indices in `DynamicStackHandler`.
- **Legendary Tooltips + Relics (NeoForge):** avoids a crash caused by an outdated Relics API package reference.
- **GTBC SpellLib (NeoForge):** prevents an exception when a summon health modifier is already present.

Integrations are optional where applicable and are activated only when their target mod is installed.

## Build

Requirements:
- JDK 21. The Forge 1.20.1 module uses a Java 17 toolchain; NeoForge 1.21.1 is built with Java 21 and targets Java 17 bytecode.
- Internet access on the first build so Gradle can download dependencies.

**Windows**
```bat
gradlew.bat clean universalJar
```

**Linux / macOS**
```bash
./gradlew clean universalJar
```

The universal JAR is generated in `build/libs/` with the `-multiloader-universal.jar` suffix.

To build an individual payload:
```bash
./gradlew :jar
./gradlew :neoforge:build
```

## GitHub Actions

The [Build Universal JAR workflow](https://github.com/BredyCavalcanti/Kaister-s-Tweaks-and-Fixes-Reforged-KTFR/actions/workflows/build.yml) builds both loader modules, assembles the universal JAR, checks that the expected metadata and entrypoints are present, and uploads the artifact for 14 days.

A successful CI build confirms compilation and archive structure; it does not replace in-game testing. Optional integrations should be tested both with and without their target mods installed.

## Repository layout

- `src/main/java/` and `src/main/resources/`: Forge 1.20.1 implementation.
- `neoforge/`: NeoForge 1.21.1 implementation and metadata.
- `libs/`: compile-time Simple Storage Network dependency for the Forge integration. It is not bundled into the mod.
- `.github/workflows/`: build and repository maintenance automation.
- `LICENSE`: Kaister Mod License (KML) v1.0.

## Development notes

- Keep generated files (`build/`, IDE metadata, logs, and local run directories) out of commits.
- Keep loader-specific classes isolated; do not link Forge-only APIs from NeoForge code or vice versa.
- Test the exact universal JAR in actual Forge and NeoForge instances before publishing a release.
- Test optional Mixin integrations both with and without their target mods installed.

## License

This project is distributed under the **Kaister Mod License (KML) v1.0**. It is a custom source-available license, not an OSI-approved open-source license. Read [LICENSE](LICENSE) before using, modifying, or redistributing this project.
