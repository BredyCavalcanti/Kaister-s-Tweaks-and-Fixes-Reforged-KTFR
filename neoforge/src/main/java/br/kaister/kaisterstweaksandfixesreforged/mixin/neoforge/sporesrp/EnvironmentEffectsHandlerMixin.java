package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.sporesrp;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Spore:SRP! reads a client config value from its tick handler before
 * NeoForge has finished loading that config. Use the value's own declared
 * default for that early tick rather than crashing the client.
 */
@Pseudo
@Mixin(targets = "com.maha_fish.sporesrp.client.EnvironmentEffectsHandler", remap = false)
public abstract class EnvironmentEffectsHandlerMixin {

    @Redirect(
            method = "onClientTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/common/ModConfigSpec$ConfigValue;get()Ljava/lang/Object;"
            ),
            remap = false
    )
    private Object ktfr$useDefaultUntilConfigLoads(ModConfigSpec.ConfigValue<?> configValue) {
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
