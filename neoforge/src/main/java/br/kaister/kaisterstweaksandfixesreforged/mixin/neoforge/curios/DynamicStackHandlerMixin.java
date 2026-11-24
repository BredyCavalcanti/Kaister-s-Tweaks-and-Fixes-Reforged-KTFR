package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.curios;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevents Curios from throwing when a slot index becomes stale while its
 * dynamic equipment handler is being resized.
 */
@Pseudo
@Mixin(targets = "top.theillusivec4.curios.common.inventory.DynamicStackHandler", remap = false)
public abstract class DynamicStackHandlerMixin {

    @Inject(method = "getPreviousStackInSlot", at = @At("HEAD"), cancellable = true, remap = false)
    private void ktfr$guardPreviousStackIndex(int slot, CallbackInfoReturnable<ItemStack> cir) {
        int slotCount = ((ItemStackHandler) (Object) this).getSlots();
        if (slot < 0 || slot >= slotCount) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "setPreviousStackInSlot", at = @At("HEAD"), cancellable = true, remap = false)
    private void ktfr$guardPreviousStackWrite(int slot, ItemStack stack, CallbackInfo ci) {
        int slotCount = ((ItemStackHandler) (Object) this).getSlots();
        if (slot < 0 || slot >= slotCount) {
            ci.cancel();
        }
    }
}
