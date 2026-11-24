package br.kaister.kaisterstweaksandfixesreforged.mixin.simplybows;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.sweenus.simplybows.item.unique.SimplyBowItem", remap = false)
public abstract class SimplyBowItemMixin {
    private static final ResourceLocation ENDLESS_QUIVER = new ResourceLocation("apotheosis", "endless_quiver");

    @Inject(method = "createArrowEntity", at = @At("RETURN"))
    private void simplybowscompat$preventInfiniteArrowPickup(
            Level level,
            LivingEntity shooter,
            ItemStack weapon,
            ItemStack projectileStack,
            boolean critical,
            CallbackInfoReturnable<Projectile> cir) {
        Projectile projectile = cir.getReturnValue();
        if (!(projectile instanceof AbstractArrow arrow)) return;
        if (!(projectileStack.getItem() instanceof ArrowItem)) return;

        boolean infinite = weapon.getEnchantmentLevel(Enchantments.INFINITY_ARROWS) > 0;
        Enchantment endless = BuiltInRegistries.ENCHANTMENT.getOptional(ENDLESS_QUIVER).orElse(null);
        if (endless != null && weapon.getEnchantmentLevel(endless) > 0) infinite = true;

        if (infinite) {
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
    }
}
