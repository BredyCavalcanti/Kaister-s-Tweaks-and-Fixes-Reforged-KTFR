package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.clienttweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Balm 21.0.67 can call ConfigReflection with a null LoadedConfig before
 * Client Tweaks' config has finished loading. Return Client Tweaks' declared
 * defaults during that window; once Balm has an active config, use it normally.
 */
@Pseudo
@Mixin(targets = "net.blay09.mods.clienttweaks.ClientTweaksConfig", remap = false)
public abstract class ClientTweaksConfigMixin {

    @Inject(method = "getActive", at = @At("HEAD"), cancellable = true, remap = false)
    private static void ktfr$guardUnloadedBalmConfig(CallbackInfoReturnable<Object> cir) {
        try {
            ClassLoader loader = ClientTweaksConfigMixin.class.getClassLoader();
            Class<?> configDataClass = Class.forName(
                    "net.blay09.mods.clienttweaks.ClientTweaksConfigData", true, loader);
            Class<?> balmClass = Class.forName("net.blay09.mods.balm.api.Balm", true, loader);

            Object balmConfig = balmClass.getMethod("getConfig").invoke(null);
            Method getActive = balmConfig.getClass().getMethod("getActive", Class.class);
            Object activeConfig = getActive.invoke(balmConfig, configDataClass);

            if (activeConfig == null) {
                activeConfig = configDataClass.getConstructor().newInstance();
            }

            cir.setReturnValue(activeConfig);
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();
            if (cause instanceof NullPointerException nullPointer
                    && isBalmConfigNotLoaded(nullPointer)) {
                cir.setReturnValue(createDefaultConfig());
                return;
            }
            throw new IllegalStateException("Client Tweaks config lookup failed", cause);
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException("Could not safely read Client Tweaks config", exception);
        }
    }

    private static boolean isBalmConfigNotLoaded(NullPointerException exception) {
        String message = exception.getMessage();
        return message != null
                && message.contains("LoadedConfig.getRaw")
                && message.contains("because \"config\" is null");
    }

    private static Object createDefaultConfig() {
        try {
            ClassLoader loader = ClientTweaksConfigMixin.class.getClassLoader();
            Class<?> configDataClass = Class.forName(
                    "net.blay09.mods.clienttweaks.ClientTweaksConfigData", true, loader);
            return configDataClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException("Could not create default Client Tweaks config", exception);
        }
    }
}
