package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.storagenetwork;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * NeoForge 1.21.1 port of the stacked-container fix for Simple Storage Network.
 *
 * When a recipe consumes one item from a stack and returns a crafting remainder,
 * SSN's shift-crafting code can replace the whole ingredient stack with the
 * remainder. Preserve the unused ingredients and return only the container.
 */
@Pseudo
@Mixin(targets = "com.lothrazar.storagenetwork.gui.ContainerNetwork", remap = false)
public abstract class ContainerNetworkMixin {

    @Redirect(
            method = "craftShift",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/lothrazar/storagenetwork/gui/NetworkCraftingInventory;setItem(ILnet/minecraft/world/item/ItemStack;)V",
                    ordinal = 1,
                    remap = false
            ),
            remap = false
    )
    private void ktfr$preserveStackedContainerIngredient(
            @Coerce Container matrix,
            int slot,
            ItemStack remainder,
            Player player,
            @Coerce Object tileMain
    ) {
        ItemStack ingredient = matrix.getItem(slot);

        if (ingredient.isEmpty()) {
            matrix.setItem(slot, remainder);
            return;
        }

        ingredient.shrink(1);

        if (ingredient.isEmpty()) {
            matrix.setItem(slot, remainder);
            return;
        }

        matrix.setItem(slot, ingredient);

        ItemStack returned = remainder.copy();
        if (!player.getInventory().add(returned)) {
            player.drop(returned, false);
        }
    }
}
