package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.sporesrp;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Spore:SRP! can receive client tick and fog events before its common config
 * has finished loading. Every ConfigValue subtype used by these handlers
 * needs the same guarded read; JVM bytecode invokes the concrete subtype
 * owners (BooleanValue, IntValue and DoubleValue), not ConfigValue.
 */
@Pseudo
@Mixin(targets = "com.maha_fish.sporesrp.client.EnvironmentEffectsHandler", remap = false)
public abstract class EnvironmentEffectsHandlerMixin {

    @Redirect(
            method = {"onRenderFog", "onComputeFogColor", "onClientTick"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/ModConfigSpec$BooleanValue;get()Ljava/lang/Object;",
                    remap = false
            ),
            remap = false
    )
    private static Object ktfr$guardBooleanValue(ModConfigSpec.BooleanValue configValue) {
        return ktfr$readConfigValue(configValue);
    }

    @Redirect(
            method = {"onRenderFog", "onComputeFogColor", "onClientTick"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/ModConfigSpec$DoubleValue;get()Ljava/lang/Object;",
                    remap = false
            ),
            remap = false
    )
    private static Object ktfr$guardDoubleValue(ModConfigSpec.DoubleValue configValue) {
        return ktfr$readConfigValue(configValue);
    }

    @Redirect(
            method = {"onRenderFog", "onComputeFogColor"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/ModConfigSpec$ConfigValue;get()Ljava/lang/Object;",
                    remap = false
            ),
            remap = false
    )
    private static Object ktfr$guardConfigValue(ModConfigSpec.ConfigValue<?> configValue) {
        return ktfr$readConfigValue(configValue);
    }

    @Redirect(
            method = "onClientTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/ModConfigSpec$IntValue;get()Ljava/lang/Object;",
                    remap = false
            ),
            remap = false
    )
    private static Object ktfr$guardIntValue(ModConfigSpec.IntValue configValue) {
        return ktfr$readConfigValue(configValue);
    }

    private static Object ktfr$readConfigValue(ModConfigSpec.ConfigValue<?> configValue) {
        try {
            return configValue.get();
        } catch (IllegalStateException exception) {
            if ("Cannot get config value before config is loaded.".equals(exception.getMessage())) {
                return configValue.getDefault();
            }
            throw exception;
        }
    }
}
