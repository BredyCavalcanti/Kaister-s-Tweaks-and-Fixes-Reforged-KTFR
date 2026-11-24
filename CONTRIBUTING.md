# Contributing to KTFR

Thanks for helping improve Kaister's Tweaks and Fixes Reforged.

## Before changing code

- Read the repository [LICENSE](LICENSE). Contributions, modifications, and redistribution are subject to its terms and require the author's permission.
- Open an issue or discuss the proposed change before starting substantial work.
- Keep pull requests focused; avoid mixing unrelated fixes.
- Do not commit generated build output, local run directories, credentials, or personal logs.

## Build and verify

Use **JDK 21** and the Gradle wrapper. The full multiloader build compiles Forge 1.20.1 and NeoForge 1.21.1 and assembles the distributable JAR.

- Windows: `gradlew.bat clean universalJar`
- Linux/macOS: `./gradlew clean universalJar`

The universal JAR is written to `build/libs/*-multiloader-universal.jar`. CI additionally checks metadata, entrypoints, and that every Mixin class listed in the loader configs is present in the archive.

Individual payload tasks are available for focused work:

- Forge 1.20.1: `./gradlew :jar`
- NeoForge 1.21.1: `./gradlew :neoforge:build`

A successful build is not a substitute for in-game testing.

## Compatibility changes

For optional integrations, verify both cases:
1. The target mod is installed at the supported version.
2. The target mod is absent.

When possible, also test the exact version combinations documented in [wiki/Compatibility.md](wiki/Compatibility.md). Do not mark a fix as in-game tested until the behavior has been reproduced and confirmed in Minecraft.

## Bug reports and pull requests

Use the repository's issue templates and pull request checklist. Include:

- Minecraft version and loader.
- KTFR version and target-mod versions.
- Whether the issue occurs with the target mod alone or only with a larger modpack.
- Reproduction steps and expected/actual behavior.
- Relevant `latest.log`, `debug.log`, or crash report, with personal paths, usernames, tokens, and other private information removed.

For pull requests, state which loader(s) were built and which game tests were actually performed. Distinguish **implemented**, **build verified**, and **in-game tested**.
