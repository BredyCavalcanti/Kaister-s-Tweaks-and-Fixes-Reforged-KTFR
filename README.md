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

### Simple Storage Network (Forge 1.20.1 + NeoForge 1.21.1)
- Fixes shift-crafting with stackable ingredients and crafting remainders.
- Consumes only the required ingredient amount instead of replacing an entire stack with its container item.
- Returns container items to the player's inventory when possible, or drops them if the inventory is full.

### Other compatibility fixes
- **Curios:** guards against invalid slot indices in `DynamicStackHandler`.
- **Legendary Tooltips + Relics (NeoForge):** avoids a crash caused by an outdated Relics API package reference. The affected optional Relics tooltip decoration is disabled as a safe fallback.
- **GTBC SpellLib (NeoForge):** prevents an exception when a summon health modifier is already present.
- **Epic Fight: Avalon (NeoForge):** provides a compatibility bridge for Avalon 21.12.6.2's legacy `PrepareModelEvent` reference against the newer Epic Fight event API.
- **Nightfall + Epic Fight (NeoForge 1.21.1):** provides Yamato Dodge with a dedicated registered jump counter.
- **Spore:SRP! (NeoForge 1.21.1):** falls back to the config value's declared default if its client tick reads that value before NeoForge finishes loading the config.
- **Spore:SRP! + Torchmaster (NeoForge 1.21.1):** prevents Spore's approved natural spawn checks from bypassing Torchmaster's configured blocking-light entity filter and radius.
- **Torchmaster (NeoForge 1.21.1):** lets vanilla walls and fences connect to the Feral Flare Lantern on the side where the lantern is mounted.
- **Wings + Epic Fight (NeoForge 1.21.1):** reuses Epic Fight's creative-flight and idle motions while Wings flight is active, preserving Epic Fight's action animations.

Integrations are optional where applicable and are activated only when their target mod is installed. See the [compatibility matrix](wiki/Compatibility.md) for version scope and test status.

## Test status

A successful CI build confirms compilation and archive structure; it does **not** prove that every integration works in-game.

- **In-game confirmed:** Simply Bows compatibility with Relics and Supplementaries quivers on NeoForge 1.21.1.
- **In-game confirmed:** Wings and Epic Fight render/attack integration on NeoForge 1.21.1. The forward-lean experiment was removed; flight uses Epic Fight's normal creative-flight pose.
- **Implemented, in-game validation not recorded:** other compatibility patches unless the compatibility matrix explicitly says otherwise.

Please do not interpret an implemented patch or successful build as an in-game compatibility guarantee.

## Build

Requirements:
- **JDK 21** to run the complete multiloader build. The Forge 1.20.1 module uses a Java 17 toolchain; NeoForge 1.21.1 is compiled with Java 21 and emits Java 17-compatible bytecode.
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

The [Build Universal JAR workflow](https://github.com/BredyCavalcanti/Kaister-s-Tweaks-and-Fixes-Reforged-KTFR/actions/workflows/build.yml) builds both loader modules, validates the universal archive and every registered Mixin class, and uploads the artifact for 14 days. Changes under `logs/` do not trigger this workflow.

To publish a release, update `mod_version` in `gradle.properties`, add the matching version changelog, and push a tag with the same version prefixed by `v` (for example, `v1.0.3`). The [Release workflow](.github/workflows/release.yml) rebuilds and validates the JAR before attaching it to a GitHub Release.

## Repository layout

- `src/main/java/` and `src/main/resources/`: Forge 1.20.1 implementation.
- `neoforge/`: NeoForge 1.21.1 implementation and metadata.
- `libs/`: compile-time Simple Storage Network dependency for the Forge integration. It is not bundled into the mod.
- `changelogs/`: version history and changelog authoring notes.
- `.github/`: build/release workflows and contribution templates.
- `LICENSE`: Kaister Mod License (KML) v1.0.

## Development notes

- Keep generated files (`build/`, IDE metadata, logs, and local run directories) out of commits.
- Keep loader-specific classes isolated; do not link Forge-only APIs from NeoForge code or vice versa.
- Test the exact universal JAR in actual Forge and NeoForge instances before publishing a release.
- Test optional Mixin integrations both with and without their target mods installed.
- When adding a Mixin, add its class to the relevant config and ensure CI verifies that the class is packaged.

## License

This project is distributed under the **Kaister Mod License (KML) v1.0**. It is a custom source-available license, not an OSI-approved open-source license. Read [LICENSE](LICENSE) before using, modifying, or redistributing this project.
