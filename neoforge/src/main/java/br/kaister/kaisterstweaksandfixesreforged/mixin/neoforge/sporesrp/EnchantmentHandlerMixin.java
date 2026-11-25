package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.sporesrp;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.core.component.DataComponents;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Spore:SRP 1.11.1 looks up the dynamic enchantment registry through
 * BuiltInRegistries.REGISTRY during class initialization. Enchantments are
 * data-driven in 1.21.1, so that lookup is null. The stack's ENCHANTMENTS
 * component already contains the holder keys and levels needed by this helper.
 */
@Pseudo
@Mixin(targets = "com.maha_fish.sporesrp.handler.EnchantmentHandler", remap = false)
public abstract class EnchantmentHandlerMixin {

    /**
     * @author KTFR
     * @reason Read the data component directly instead of relying on the removed
     *         static BuiltInRegistries enchantment registry.
     */
    @Overwrite(remap = false)
    private static int getEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> enchantmentKey) {
        if (stack.isEmpty()) {
            return 0;
        }

        ItemEnchantments enchantments = stack.getOrDefault(
                DataComponents.ENCHANTMENTS,
                ItemEnchantments.EMPTY
        );

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            if (entry.getKey().unwrapKey().filter(enchantmentKey::equals).isPresent()) {
                return entry.getIntValue();
            }
        }

        return 0;
    }

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;)V",
                    remap = false
            ),
            remap = false
    )
    private static void ktfr$suppressObsoleteRegistryWarning(Logger logger, String message) {
        if (!"Enchantment registry not found!".equals(message)) {
            logger.error(message);
        }
    }
}
