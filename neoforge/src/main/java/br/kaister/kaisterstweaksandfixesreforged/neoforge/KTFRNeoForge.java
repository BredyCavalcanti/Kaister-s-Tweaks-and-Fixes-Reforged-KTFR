package br.kaister.kaisterstweaksandfixesreforged.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;

/**
 * NeoForge 1.21.1 entrypoint.
 *
 * Loader-specific implementation lives in this module so the final
 * distributable can also contain the Forge 1.20.1 implementation.
 */
@Mod(KTFRNeoForge.MOD_ID)
public final class KTFRNeoForge {
    public static final String MOD_ID = "kaisterstweaksandfixesreforged";

    public KTFRNeoForge(IEventBus modEventBus) {
        if (ModList.get().isLoaded("efn") && ModList.get().isLoaded("epicfight")) {
            NightfallSkillData.register(modEventBus);
        }

    }
}
