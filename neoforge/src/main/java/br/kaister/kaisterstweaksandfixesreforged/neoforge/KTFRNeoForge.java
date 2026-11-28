package br.kaister.kaisterstweaksandfixesreforged.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * NeoForge 1.21.1 entrypoint.
 *
 * Optional-mod implementation classes are loaded reflectively only after
 * their owning mods have been detected. This keeps absent optional APIs out
 * of the entrypoint's eager class-linking path.
 */
@Mod(KTFRNeoForge.MOD_ID)
public final class KTFRNeoForge {
    public static final String MOD_ID = "kaisterstweaksandfixesreforged";

    private static final String NIGHTFALL_DATA =
            "br.kaister.kaisterstweaksandfixesreforged.neoforge.NightfallSkillData";
    private static final String WINGS_EPIC_FIGHT =
            "br.kaister.kaisterstweaksandfixesreforged.neoforge.wings.WingsEpicFightClientCompat";

    public KTFRNeoForge(IEventBus modEventBus) {
        if (ModList.get().isLoaded("efn") && ModList.get().isLoaded("epicfight")) {
            registerNightfallData(modEventBus);
        }

        if (ModList.get().isLoaded("wings") && ModList.get().isLoaded("epicfight")
                && FMLEnvironment.dist == Dist.CLIENT) {
            registerWingsEpicFight(modEventBus);
        }
    }

    private static void registerNightfallData(IEventBus modEventBus) {
        try {
            Class<?> dataClass = Class.forName(NIGHTFALL_DATA, true, KTFRNeoForge.class.getClassLoader());
            Method register = dataClass.getMethod("register", IEventBus.class);
            register.invoke(null, modEventBus);
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException(
                    "Failed to initialize KTFR's Nightfall/Epic Fight compatibility",
                    exception
            );
        }
    }

    private static void registerWingsEpicFight(IEventBus modEventBus) {
        try {
            Class<?> compatClass = Class.forName(WINGS_EPIC_FIGHT, true, KTFRNeoForge.class.getClassLoader());
            Method onClientSetup = compatClass.getMethod("onClientSetup", FMLClientSetupEvent.class);

            modEventBus.addListener((FMLClientSetupEvent event) -> {
                try {
                    onClientSetup.invoke(null, event);
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    throw new IllegalStateException(
                            "Failed to register KTFR's Wings/Epic Fight flight compatibility",
                            exception
                    );
                }
            });
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException(
                    "Failed to load KTFR's Wings/Epic Fight compatibility",
                    exception
            );
        }
    }
}
