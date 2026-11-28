# KTFR Integrations

This page is the detailed catalogue of the compatibility patches included in **Kaister's Tweaks and Fixes Reforged (KTFR)**. It describes what each patch changes, which mods it interacts with, and the Minecraft version and loader for which it was implemented.

KTFR is a compatibility and fixes mod, not a content mod. Most entries are narrow patches for specific bugs, API changes, or interactions between mods.

> **Important:** an implementation being present in the source code does not guarantee that every version combination has been tested in-game. The status notes below distinguish confirmed behavior from patches that still need in-game validation.

## At a glance

| Integration / target | Minecraft and loader | Main purpose |
|---|---|---|
| Simply Bows + Infinity | 1.20.1 Forge; 1.21.1 NeoForge | Infinite ammunition and arrow pickup behavior |
| Simply Bows + Endless Quiver | 1.20.1 Forge; 1.21.1 NeoForge | Recognize Endless Quiver as infinite ammunition |
| Simply Bows + Relics quivers | 1.21.1 NeoForge | Read arrows stored in Relics quivers |
| Simply Bows + Supplementaries quiver | 1.21.1 NeoForge | Read and consume the selected arrow in the actual quiver |
| Simply Bows — Homing Arrows | 1.20.1 Forge; 1.21.1 NeoForge | Preserve potion effects on converted arrows |
| Simply Bows — multi-projectile bows | 1.21.1 NeoForge | Apply Endless Quiver to custom ammunition checks |
| Simple Storage Network | 1.20.1 Forge; 1.21.1 NeoForge | Correct shift-crafting with stacked ingredients and remainders |
| Curios | 1.20.1 Forge; 1.21.1 NeoForge | Guard invalid indices in dynamic equipment storage |
| Legendary Tooltips + Relics | 1.21.1 NeoForge | Prevent a crash from an outdated Relics API reference |
| GTBC SpellLib | 1.21.1 NeoForge | Prevent duplicate summon-health attribute modifiers |
| Epic Fight + Avalon | 1.21.1 NeoForge | Bridge legacy Avalon events to Epic Fight's newer event API |
| Nightfall + Epic Fight | 1.21.1 NeoForge | Give Yamato Dodge its own registered jump counter |
| Weapons of Miracles + Epic Fight | 1.21.1 NeoForge | Safely register or skip an unavailable WOM sheath skill-data write |
| Spore:SRP! | 1.21.1 NeoForge | Fix data-driven enchantment lookup and early config reads |
| Spore | 1.21.1 NeoForge | Delay menu-music initialization until its sound event is registered |
| Spore:SRP! + Torchmaster | 1.21.1 NeoForge | Make Spore natural spawn checks respect Torchmaster blocking lights |
| Torchmaster | 1.21.1 NeoForge | Connect vanilla fences and walls to the Feral Flare Lantern |
| Wings | 1.21.1 NeoForge | Fix brewing input checks and wing-layer rendering |
| Wings + Enigmatic Delicacy | 1.21.1 NeoForge | Let the Gluttony Charm bypass Wings' food requirements |
| Wings + Epic Fight | 1.21.1 NeoForge | Use Epic Fight's flight motions and render Wings' layer with its player model |
| Client Tweaks + Balm | 1.21.1 NeoForge | Guard Client Tweaks config access during early loading |

---

## Simply Bows

**Supported loaders:** Forge 1.20.1 and NeoForge 1.21.1.

KTFR extends Simply Bows' ammunition lookup and infinite-ammunition checks without replacing the mod's normal bow behavior.

### Infinity

Simply Bows' infinite-ammunition check recognizes the vanilla **Infinity** enchantment. Arrows fired using an Infinity bow are marked as creative-only pickup so they cannot be collected again.

### Endless Quiver

KTFR makes **Endless Quiver** count as infinite ammunition for Simply Bows bows.

The enchantment registry ID differs between the two supported game lines:

- Forge 1.20.1: `apotheosis:endless_quiver`
- NeoForge 1.21.1: `apothic_enchanting:endless_quiver`

Arrows fired with Endless Quiver are also marked as creative-only pickup. On NeoForge, the custom ammunition check used by Simply Bows' multi-projectile bows is covered as well, including the Ice Bow path.

### Relics quivers

**NeoForge 1.21.1 only.** KTFR exposes arrow stacks stored in Relics quivers to Simply Bows' ammunition lookup.

### Supplementaries quiver

**NeoForge 1.21.1 only.** KTFR exposes the active Supplementaries quiver to Simply Bows. It uses the selected arrow stack from the quiver's live contents, rather than a detached copy, so Simply Bows' ordinary ammunition decrement updates the actual quiver.

### Homing Arrows

When Simply Bows creates a Homing Arrow from another arrow stack, KTFR copies the source arrow's potion contents to the new projectile. This preserves tipped-arrow effects through the conversion.

**Test status:** Relics and Supplementaries quiver compatibility has been confirmed in-game on NeoForge 1.21.1. The other Simply Bows patches are implemented; see [Compatibility](Compatibility) for the current test record.

---

## Simple Storage Network

**Supported loaders:** Forge 1.20.1 and NeoForge 1.21.1.

Fixes shift-crafting when a recipe consumes one item from a stack and returns a crafting remainder.

The problematic behavior could replace the entire ingredient stack with the remainder. KTFR instead:

1. Consumes only one item from the ingredient stack.
2. Leaves the remaining ingredients in the crafting grid.
3. Returns the crafting remainder to the player's inventory.
4. Drops the remainder if the inventory has no room.

The patch is limited to the affected shift-crafting path; it does not replace Simple Storage Network's crafting system.

---

## Curios

**Supported loaders:** Forge 1.20.1 and NeoForge 1.21.1.

Guards Curios' `DynamicStackHandler` when it reads or writes a previous stack using an invalid or stale slot index.

- An invalid read returns `ItemStack.EMPTY`.
- An invalid write is ignored.

This prevents an out-of-range slot access from throwing while the dynamic equipment handler is changing size.

---

## Legendary Tooltips + Relics

**Supported loader:** NeoForge 1.21.1.

The affected Legendary Tooltips version references a Relics API package that no longer exists in the 1.21 line. That stale optional integration can crash client tooltip rendering.

KTFR replaces the affected `RelicsHandler.hasTooltipDecor` hook with a safe fallback. **The specific Relics tooltip decoration provided by that hook is disabled** while this patch is active; the fix prioritizes avoiding the crash.

Both Legendary Tooltips and Relics must be installed for this compatibility patch to apply.

---

## GTBC SpellLib

**Supported loader:** NeoForge 1.21.1.

SpellLib's summon-health helper can attempt to add an attribute modifier that is already present. KTFR checks whether the modifier ID is already registered on the attribute instance and only adds it when absent.

This makes that operation idempotent without changing unrelated attribute modifiers.

---

## Epic Fight + Avalon

**Supported loader:** NeoForge 1.21.1.

Avalon 21.12.6.2 expects older Epic Fight NeoForge event classes and registration hooks. Epic Fight 21.17 changed those APIs.

KTFR provides compatibility bridges for the affected event paths:

- Entity-patch registration events are forwarded to the legacy NeoForge mod-bus event expected by Avalon.
- Patched item-renderer registration events are forwarded to Avalon's expected event.
- Patched entity-renderer and layer registration events are forwarded with their renderer map and context.

The bridge adapts the event API; it does not bundle Avalon or Epic Fight. Epic Fight must be present for these patches to apply.

---

## Nightfall + Epic Fight — Yamato Dodge

**Supported loader:** NeoForge 1.21.1.

Nightfall's Yamato Dodge uses Epic Fight's global jump-count data key in a context where that key can be unavailable or unsuitable.

KTFR registers a dedicated skill-data key named `nightfall_yamato_jump_count` and redirects Yamato Dodge's accesses to it.

- The key stores an integer and starts at `0`.
- It is scoped to Nightfall's Yamato Dodge skill.
- It follows the original jump counter's unsynchronized data behavior.
- Murasama Dodge's separate data key is not changed.

The integration requires both Nightfall and Epic Fight. Nightfall's own required dependencies, including Avalon, remain the responsibility of the modpack.

---

## Weapons of Miracles + Epic Fight

**Supported loader:** NeoForge 1.21.1.

Weapons of Miracles' Satsujin animation can attempt to write the `wom:sheath` skill-data key before that key has been registered in the active Epic Fight skill-data manager.

KTFR checks the target key before the write:

- If the key is already registered, normal behavior continues.
- If the key is declared and available but not yet registered in the current manager, KTFR registers it before the write.
- If the deferred key is not bound yet, KTFR cancels that invalid write instead of allowing it to throw.

The patch is restricted to the `wom:sheath` key; other skill-data writes are left alone. Both Weapons of Miracles and Epic Fight are required for this mixin.

---

## Spore:SRP!

**Supported loader:** NeoForge 1.21.1.

KTFR contains two separate fixes for Spore:SRP!.

### Data-driven enchantments

Spore:SRP! 1.11.1 uses a static built-in registry lookup for enchantments. In Minecraft 1.21.1, enchantments are data-driven, so that old lookup can be unavailable.

KTFR replaces the affected helper with a lookup against the item's `ENCHANTMENTS` data component. It compares the stored enchantment holder keys with the requested resource key and returns the matching level, or zero if the item has no matching enchantment.

### Early config reads

Spore:SRP!'s client environment-effects handlers can receive render or tick calls before the mod's NeoForge config has finished loading.

KTFR guards the affected config reads. If NeoForge throws the specific "Cannot get config value before config is loaded" exception, the patch returns that config value's declared default for the early read. Other exceptions are not swallowed, and normal configured values are used once loading has completed.

---

## Spore

**Supported loader:** NeoForge 1.21.1.

Spore 2.2.0j can initialize its menu music player from a client tick before the `spore:once_here` sound event has been bound in the sound registry.

KTFR delays that music-player initialization and tick until the sound event exists. Other Spore client tick handlers are not suppressed.

---

## Spore:SRP! + Torchmaster

**Supported loader:** NeoForge 1.21.1.

Spore:SRP! can explicitly approve natural spawn-placement checks for its infected-biome mobs. Torchmaster normally respects another mod's prior approval in some configurations, which can allow those mobs to bypass a Mega Torch even when their entity types are in its block list.

KTFR checks Torchmaster's own blocking-light manager before Spore:SRP! approves the spawn. If a configured blocking light covers the mob's entity type and position, the spawn-placement result is changed to fail.

The check is limited to Spore-namespace mobs using these spawn types:

- Natural
- Chunk generation
- Patrol

It reuses Torchmaster's configured entity filters and light radii. It does not intercept direct entity insertion paths used by unrelated custom spawners. Torchmaster is optional; its API is accessed reflectively.

---

## Torchmaster — Feral Flare Lantern connections

**Supported loader:** NeoForge 1.21.1.

Vanilla fences and walls do not normally recognize Torchmaster's directional Feral Flare Lantern as a valid connection.

KTFR extends the vanilla fence and wall neighbor-shape updates. A connection is added only when:

- The adjacent block is Torchmaster's Feral Flare Lantern.
- The lantern is horizontally adjacent.
- Its `FACING` direction points back toward the wall or fence.

Existing vanilla connection behavior for all other neighboring blocks remains unchanged.

---

## Wings

**Supported loader:** NeoForge 1.21.1.

KTFR includes two general Wings fixes.

### Brewing input validation

Wings 2.1.6 assumes a candidate brewing-stand input always contains the `POTION_CONTENTS` data component. Brewing stands can test arbitrary items, including modded items without potion contents.

KTFR rejects empty stacks and stacks without `POTION_CONTENTS` before Wings performs its original recipe check. Valid potion inputs continue through the original logic.

### Wing-layer rendering

Wings' feather geometry uses custom polygons that can be skipped by Sodium's optimized model-part rendering path.

KTFR adjusts the Wings layer's buffer handling so the custom geometry is compiled through the vanilla polygon path when Sodium is installed. It also uses a no-cull render type for the wing layer so thin feather faces remain visible.

This is a targeted rendering compatibility patch; KTFR does not replace the entire Wings renderer and does not require Sodium to be installed.

---

## Wings + Enigmatic Delicacy

**Supported loader:** NeoForge 1.21.1.

The Enigmatic Delicacy **Gluttony Charm** can bypass Wings' food-level requirements for flight and landing.

KTFR checks whether the player has the charm through Enigmatic Legacy's Curios API when available, then falls back to checking the player's inventory, armor, and offhand.

The charm only bypasses Wings' food gates. Wings' flight and landing exhaustion behavior remains unchanged.

Wings is required for the patch. Enigmatic Delicacy is an optional companion mod for this specific behavior.

---

## Wings + Epic Fight

**Supported loader:** NeoForge 1.21.1.

When both mods are installed, KTFR connects Wings' flight state to Epic Fight's player animation system.

- While the player is actively flying with Wings in Epic Fight mode, KTFR selects Epic Fight's native `CREATIVE_FLY` motion while moving and `CREATIVE_IDLE` while hovering.
- Epic Fight's action/inaction animations are not overridden, preserving attacks and other actions.
- Wings' animated layer is attached to Epic Fight's original player model so the wing layer can render while Epic Fight's player renderer is active.
- The Wings layer uses the rendering compatibility described in the [Wings section](#wings).

The experimental forward-lean modifier was removed. KTFR does not add a custom aerodynamic pitch to Epic Fight's flight animation.

**In-game test record:** wing rendering and Epic Fight attack animations have been confirmed on the tested 1.21.1 setup. The flight pose uses Epic Fight's existing creative-flight motion; no additional forward lean is included.

---

## Client Tweaks + Balm

**Supported loader:** NeoForge 1.21.1.

Client Tweaks can request its active config through Balm before the config has finished loading. In the affected Balm version, this can cause a null-config access during early initialization.

KTFR guards Client Tweaks' `getActive` config lookup. When the active config is not yet available, it creates and returns Client Tweaks' config data with its declared defaults. Once Balm has an active config, that active config is returned normally.

Both Client Tweaks and Balm must be installed for this patch to apply.

---

## Optional integrations and dependencies

The compatibility mixin plugin checks the installed mod list before applying mod-specific patches. Examples:

| Patch family | Mod IDs checked |
|---|---|
| Simply Bows | `simplybows` |
| Simple Storage Network | `storagenetwork` |
| Curios | `curios` |
| Legendary Tooltips + Relics | `legendarytooltips` + `relics` |
| GTBC SpellLib | `gtbcs_spell_lib` |
| Nightfall + Epic Fight | `efn` + `epicfight` |
| Weapons of Miracles + Epic Fight | `wom` + `epicfight` |
| Avalon compatibility | `epicfight` |
| Spore | `spore` |
| Spore:SRP! | `sporesrp` |
| Torchmaster | `torchmaster` |
| Wings | `wings` |
| Wings + Epic Fight | `wings` + `epicfight` |
| Client Tweaks + Balm | `clienttweaks` + `balm` |

The Gluttony Charm behavior is part of the Wings patch family and discovers Enigmatic Legacy APIs reflectively. The Spore:SRP! + Torchmaster patch also checks for Torchmaster at runtime before querying its optional API.

These integrations do not make every listed target mod a global hard dependency of KTFR. Minecraft and the relevant loader are still required.

## Version and test scope

A patch applies only to the Minecraft version and loader listed for it. Forge 1.20.1 and NeoForge 1.21.1 implementations are separate; a patch on one loader must not be assumed to exist on the other.

For the current test record and compatibility matrix, see [Compatibility](Compatibility). A successful CI build confirms that the code compiles and the universal JAR is assembled; it does not prove in-game behavior.
