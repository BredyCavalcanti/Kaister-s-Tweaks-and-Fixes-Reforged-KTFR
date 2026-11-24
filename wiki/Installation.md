# Installation

## Requirements

| Minecraft | Loader | Java |
|---|---|---|
| 1.20.1 | Forge 47.4.10 or newer | Java 17 |
| 1.21.1 | NeoForge 21.1.x | Java 21 |

KTFR is distributed as a single multiloader universal JAR containing both implementations.

## Install

1. Download the latest KTFR universal JAR from the project's release or build artifact.
2. Place the JAR in the instance's `mods` folder.
3. Launch the matching Minecraft version and loader:
   - Minecraft 1.20.1 with Forge, or
   - Minecraft 1.21.1 with NeoForge.
4. Install the target mods required by the specific integration you want to use. See [Compatibility](Compatibility).

Do not install separate Forge and NeoForge copies of KTFR in the same instance. Use the single universal JAR.

## Optional target mods

KTFR's integrations activate only when their target mods are present. For example, Simply Bows patches require Simply Bows; the Nightfall Yamato Dodge patch requires Nightfall and Epic Fight. The full list and Nightfall's declared dependencies are on the [Compatibility](Compatibility) page.

## Troubleshooting

When reporting a crash or a patch that does not work:

- Confirm that Minecraft and the loader match the supported pair.
- Confirm that the target mod is installed and that its version is within the documented scope.
- Include the complete `latest.log`, crash report, KTFR version, and target-mod versions.
- For an issue involving a specific integration, test whether it occurs with that target mod installed and absent.

A successful Gradle or GitHub Actions build only verifies compilation and JAR structure. In-game behavior must be verified in Minecraft.
