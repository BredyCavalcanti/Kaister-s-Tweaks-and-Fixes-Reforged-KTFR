package br.kaister.kaisterstweaksandfixesreforged.mixin.curios;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Guards Curios against a slot index that becomes stale while its equipment
 * handlers are being resized during an entity tick.
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
