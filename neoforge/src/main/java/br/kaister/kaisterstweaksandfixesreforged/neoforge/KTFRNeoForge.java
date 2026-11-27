package br.kaister.kaisterstweaksandfixesreforged.neoforge;

import br.kaister.kaisterstweaksandfixesreforged.neoforge.wings.WingsEpicFightAnimation;
import br.kaister.kaisterstweaksandfixesreforged.neoforge.wings.WingsEpicFightClientCompat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

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

        if (ModList.get().isLoaded("wings") && ModList.get().isLoaded("epicfight")) {
            WingsEpicFightAnimation.register(modEventBus);
            if (FMLEnvironment.dist == Dist.CLIENT) {
                modEventBus.addListener(WingsEpicFightClientCompat::onClientSetup);
            }
        }
    }
}
