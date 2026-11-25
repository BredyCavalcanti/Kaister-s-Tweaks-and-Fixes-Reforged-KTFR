package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.spore;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Spore 2.2.0j initializes MenuMusicPlayer from the client tick while its
 * sound holder spore:once_here is still unbound. Delay that class initialization
 * until the sound event exists in the registry, without suppressing other
 * Spore client tick handlers.
 */
@Pseudo
@Mixin(targets = "com.Harbinger.Spore.Client.ClientModEvents", remap = false)
public abstract class SporeMenuMusicInitMixin {

    private static final ResourceLocation KTFR$ONCE_HERE =
            ResourceLocation.fromNamespaceAndPath("spore", "once_here");

    @Redirect(
            method = "onClientTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/Harbinger/Spore/Client/MusicManager/MenuMusicPlayer;tick()V",
                    remap = false
            ),
            remap = false
    )
    private static void ktfr$delayMenuMusicUntilSoundRegistryIsReady() {
        if (BuiltInRegistries.SOUND_EVENT.getOptional(KTFR$ONCE_HERE).isEmpty()) {
            return;
        }

        try {
            Class<?> musicPlayer = Class.forName(
                    "com.Harbinger.Spore.Client.MusicManager.MenuMusicPlayer",
                    true,
                    SporeMenuMusicInitMixin.class.getClassLoader()
            );
            Method tick = musicPlayer.getDeclaredMethod("tick");
            tick.invoke(null);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Error error) {
                throw error;
            }
            throw new IllegalStateException("Spore menu music tick failed", cause);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not invoke Spore menu music tick", exception);
        }
    }
}
