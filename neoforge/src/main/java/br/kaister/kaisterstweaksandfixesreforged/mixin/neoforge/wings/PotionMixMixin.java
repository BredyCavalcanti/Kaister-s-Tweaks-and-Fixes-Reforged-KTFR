package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.wings;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents Wings' brewing input check from dereferencing absent potion contents.
 *
 * Wings 2.1.6 assumes every candidate stack has POTION_CONTENTS. Brewing
 * stands also test arbitrary items, including modded items without that
 * component, so those stacks must be rejected before the original check runs.
 */
@Pseudo
@Mixin(targets = "me.paulf.wings.server.potion.PotionMix", remap = false)
public abstract class PotionMixMixin {

    @Inject(
            method = "isInput",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void ktfr$rejectStacksWithoutPotionContents(
            ItemStack stack,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (stack.isEmpty() || !stack.has(DataComponents.POTION_CONTENTS)) {
            cir.setReturnValue(false);
        }
    }
}
