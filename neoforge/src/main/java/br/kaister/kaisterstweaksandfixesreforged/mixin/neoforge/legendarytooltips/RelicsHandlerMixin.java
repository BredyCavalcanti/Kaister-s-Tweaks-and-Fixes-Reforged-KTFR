package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.legendarytooltips;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

/**
 * Legendary Tooltips 1.5.5 still links against Relics' pre-1.21 IRelicItem
 * package. Replace only its optional Relics tooltip hook with a safe fallback
 * so the stale integration cannot crash client tooltip rendering.
 */
@Pseudo
@Mixin(targets = "com.anthonyhilyard.legendarytooltips.compat.RelicsHandler", remap = false)
public abstract class RelicsHandlerMixin {

    /**
     * @author KTFR
     * @reason Legendary Tooltips references a Relics API package removed in 1.21.
     */
    @Overwrite(remap = false)
    public static boolean hasTooltipDecor(ItemStack stack, LocalPlayer player) {
        return false;
    }
}
