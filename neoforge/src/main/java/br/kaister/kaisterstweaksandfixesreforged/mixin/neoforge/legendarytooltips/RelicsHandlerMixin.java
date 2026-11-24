package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.legendarytooltips;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Legendary Tooltips 1.5.5 still links against Relics' pre-1.21 IRelicItem
 * package. Skip only its optional Relics tooltip decoration hook rather than
 * allowing that stale linkage to crash client tooltip rendering.
 */
@Pseudo
@Mixin(targets = "com.anthonyhilyard.legendarytooltips.compat.RelicsHandler", remap = false)
public abstract class RelicsHandlerMixin {

    @Inject(method = "hasTooltipDecor", at = @At("HEAD"), cancellable = true, remap = false)
    private static void ktfr$skipOutdatedRelicsIntegration(
            ItemStack stack,
            LocalPlayer player,
            CallbackInfoReturnable<Boolean> cir
    ) {
        cir.setReturnValue(false);
    }
}
