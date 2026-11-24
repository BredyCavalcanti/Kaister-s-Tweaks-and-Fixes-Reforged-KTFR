package br.kaister.kaisterstweaksandfixesreforged.mixin.storagenetwork;

import com.lothrazar.storagenetwork.block.main.TileMain;
import com.lothrazar.storagenetwork.gui.ContainerNetwork;
import com.lothrazar.storagenetwork.gui.NetworkCraftingInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ContainerNetwork.class, remap = false)
public abstract class ContainerNetworkMixin {

    /**
     * Simple Storage Network 1.13.2 replaces the entire crafting-grid stack
     * with its crafting remainder during shift-crafting.
     *
     * That is only safe for unstackable container items. For stackable items
     * such as Farmer's Delight Milk Bottles, replacing the slot destroys every
     * unused item in the stack.
     *
     * Consume exactly one ingredient instead. If more ingredients remain,
     * keep them in the grid and return the container to the player. If the
     * consumed item was the last one, the remainder can occupy the grid slot.
     */
    @Redirect(
            method = "craftShift",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/lothrazar/storagenetwork/gui/NetworkCraftingInventory;m_6836_(ILnet/minecraft/world/item/ItemStack;)V",
                    ordinal = 1
            )
    )
    private void ssncraftingfix$preserveStackedContainerIngredient(
            NetworkCraftingInventory matrix,
            int slot,
            ItemStack remainder,
            Player player,
            TileMain tileMain
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