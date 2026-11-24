# KTFR Wiki

Welcome to the **Kaister's Tweaks and Fixes Reforged (KTFR)** wiki.

KTFR is a collection of focused bug fixes and compatibility patches for Minecraft modded environments. It does not aim to add new content; it makes existing mods work together more reliably.

## Pages

- [Compatibility](Compatibility) — supported Minecraft versions, loaders, integrations, and exactly what each patch changes.
- [Installation](Installation) — requirements and how to install the universal JAR.

## Project targets

| Minecraft | Mod loader | KTFR module |
|---|---|---|
| 1.20.1 | Forge | Root project |
| 1.21.1 | NeoForge | `neoforge/` |

KTFR is distributed as one **multiloader universal JAR** containing both loader-specific implementations. Install that same file in the instance matching your Minecraft version and loader.

## Compatibility status

The compatibility list documents the integrations currently included in KTFR. A listed integration means the corresponding patch is present in the project; it does not imply that every possible version of the target mod is supported.

See [Compatibility](Compatibility) for per-loader scope, supported behavior, and known version requirements.

## Useful links

- [Source code](https://github.com/BredyCavalcanti/Kaister-s-Tweaks-and-Fixes-Reforged-KTFR)
- [Builds and CI](https://github.com/BredyCavalcanti/Kaister-s-Tweaks-and-Fixes-Reforged-KTFR/actions)
- [Issues and bug reports](https://github.com/BredyCavalcanti/Kaister-s-Tweaks-and-Fixes-Reforged-KTFR/issues)
- [License — Kaister Mod License (KML) v1.0](../LICENSE)

## Reporting a compatibility issue

When reporting a problem, include:

1. Minecraft version and mod loader version.
2. KTFR version and the exact universal JAR filename.
3. Versions of the affected mods.
4. Steps to reproduce the issue.
5. `latest.log` and, when available, the crash report.

Please test with the exact loader-specific setup you are reporting. A successful build confirms compilation and archive structure, not in-game behavior.
