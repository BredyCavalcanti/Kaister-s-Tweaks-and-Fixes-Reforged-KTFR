# Compatibility

This page lists the fixes and integrations included in KTFR 1.0.1 and the current development line.

**Legend**
- **Supported** — KTFR contains a targeted compatibility patch for the behavior described.
- **Optional integration** — the target mod is not a required dependency of KTFR; its patch activates only when that mod is installed.

## Minecraft and loader support

| Minecraft | Loader | KTFR support |
|---|---|---|
| 1.20.1 | Forge | Supported |
| 1.21.1 | NeoForge | Supported |

The project is built as one multiloader universal JAR. Forge and NeoForge implementations are kept separate inside the archive.

## Compatibility matrix

| Target mod(s) | Minecraft / loader | Supported behavior |
|---|---|---|
| Simply Bows + Infinity | 1.20.1 Forge; 1.21.1 NeoForge | Treats Infinity as infinite ammunition for Simply Bows bows; fired arrows cannot be picked up. |
| Simply Bows + Endless Quiver | 1.20.1 Forge; 1.21.1 NeoForge | Treats Apothic Enchanting/Apotheosis Endless Quiver as infinite ammunition for Simply Bows bows; fired arrows cannot be picked up. |
| Simply Bows + Relics quivers | 1.21.1 NeoForge | Makes Relics quivers available to Simply Bows' ammunition lookup. In-game compatibility confirmed. |
| Simply Bows + Supplementaries quivers | 1.21.1 NeoForge | Makes Supplementaries quiver ammunition available to Simply Bows. Uses the quiver's selected arrow stack so normal ammunition consumption updates the real quiver. In-game compatibility confirmed. |
| Simply Bows — Homing Arrow | 1.20.1 Forge; 1.21.1 NeoForge | Preserves potion effects from the source arrow when a Homing Arrow is created. |
| Simply Bows — multi-projectile bows | 1.21.1 NeoForge | Applies Endless Quiver's infinite-ammunition behavior to custom ammunition checks, including the Ice Bow's multi-arrow path. |
| Simple Storage Network | 1.20.1 Forge; 1.21.1 NeoForge | Fixes shift-crafting with stackable ingredients and crafting remainders. Only the required ingredient amount is consumed; unused ingredients remain in the grid and container items are returned to the player or dropped if the inventory is full. |
| Curios | 1.20.1 Forge; 1.21.1 NeoForge | Guards `DynamicStackHandler` against invalid or stale slot indices when reading or writing previous slot contents. |
| Legendary Tooltips + Relics | 1.21.1 NeoForge | Prevents a client tooltip crash caused by Legendary Tooltips referencing a Relics API package removed in the 1.21 line. The affected optional Relics tooltip decoration hook is safely disabled. |
| GTBC SpellLib | 1.21.1 NeoForge | Makes the summon-health helper safe to call more than once by ignoring an already-present identical attribute modifier. |
| Nightfall + Epic Fight — Yamato Dodge | 1.21.1 NeoForge | Provides Yamato Dodge with a dedicated registered integer jump counter, redirecting its access away from the problematic Epic Fight jump-count holder. |

## Integration details

### Simply Bows

KTFR extends Simply Bows' infinite-ammunition checks so enchanted bows can use Infinity-style ammunition behavior.

- **Infinity:** supported without changing vanilla Infinity's normal behavior.
- **Endless Quiver:** recognized as infinite ammunition. The enchantment registry ID differs by game line:
  - Forge 1.20.1: `apotheosis:endless_quiver`
  - NeoForge 1.21.1: `apothic_enchanting:endless_quiver`
- **Arrow pickup:** arrows fired with Infinity or Endless Quiver are marked as creative-only pickup.
- **Homing Arrows:** potion contents/effects are carried over from the source arrow.
- **Relics and Supplementaries quivers:** their arrow stacks are exposed to Simply Bows on NeoForge 1.21.1. Both integrations have been tested in-game.
- **Multi-projectile bows:** the NeoForge patch also covers the Ice Bow's custom infinite-ammunition check.

The quiver integrations do not make Relics or Supplementaries mandatory dependencies.

### Simple Storage Network

The shift-crafting patch addresses a stack-remainder bug: when a recipe consumes one item from a stack and returns a container, the entire ingredient stack must not be replaced by that container.

KTFR consumes one ingredient, keeps the remaining stack in the crafting grid, and returns the container item to the player's inventory. If it cannot fit, the container is dropped instead.

### Curios

The patch guards the previous-stack accessors in Curios' `DynamicStackHandler`. Invalid slot indices return an empty stack or skip the invalid write instead of throwing an index exception.

### Legendary Tooltips + Relics

On NeoForge 1.21.1, the optional Relics tooltip hook in the affected Legendary Tooltips integration refers to an outdated Relics API package. KTFR replaces that hook with a safe fallback to prevent the crash. This means that this specific Relics tooltip decoration is disabled while the patch is active.

### GTBC SpellLib

The summon-health helper can attempt to add an attribute modifier that is already present. KTFR checks for the modifier first and only adds it when missing.

### Nightfall + Epic Fight — Yamato Dodge

KTFR includes a Nightfall-specific compatibility patch for Yamato Dodge on NeoForge 1.21.1.

- Registers a KTFR-owned skill-data key in Epic Fight's skill-data registry.
- Uses an integer codec and an initial value of `0`.
- Restricts the key to Nightfall's `YamatoDodge` skill.
- Redirects Yamato Dodge's accesses to Epic Fight's global `JUMP_COUNT` holder to the dedicated key.
- Does not replace or redirect Murasama Dodge's own Nightfall data key.

**Target versions:** Nightfall 3.4.0 and Epic Fight 21.17.3.1 on Minecraft 1.21.1 / NeoForge.

Nightfall's own metadata declares these requirements for the 3.4.0 build:

| Requirement | Declared version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.219 or newer |
| Epic Fight | 21.17 or newer |
| Invincible | 21.15 or newer |
| Epic Fight: Avalon | 21.12.6.2 or newer |
| GeckoLib | 4.9.1 or newer |
| VIX | 1.0.9 or newer |
| AAA Particles | Optional, 2.2 or newer |

The Nightfall patch is optional: KTFR does not require Nightfall or Epic Fight to launch.

## Optional integrations and dependencies

KTFR's compatibility mixins are gated by the presence of their target mods. The target mods listed above are not global hard dependencies of KTFR. The Minecraft loader itself remains required.

## Version scope

A compatibility entry applies only to the Minecraft version and loader shown in its row. Do not assume that a Forge patch automatically applies to NeoForge, or that a patch for one Minecraft release works on another release.

For a current build, check the repository's [Actions page](https://github.com/BredyCavalcanti/Kaister-s-Tweaks-and-Fixes-Reforged-KTFR/actions) and use the generated universal JAR.
