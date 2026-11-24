package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.simplybows;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Handles arrow pickup for Simply Bows without relying on an internal
 * Simply Bows shooting method name that can differ between mappings.
 */
@Mixin(Projectile.class)
public abstract class ProjectileOwnerMixin {

    private static final ResourceKey<Enchantment> INFINITY = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath("minecraft", "infinity")
    );
    private static final ResourceKey<Enchantment> ENDLESS_QUIVER = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath("apothic_enchanting", "endless_quiver")
    );

    @Inject(method = "setOwner", at = @At("TAIL"))
    private void ktfr$preventInfiniteArrowPickup(Entity owner, CallbackInfo ci) {
        if (!((Object) this instanceof AbstractArrow arrow) || !(owner instanceof LivingEntity shooter)) {
            return;
        }

        if (hasInfiniteBow(shooter, shooter.getMainHandItem())
                || hasInfiniteBow(shooter, shooter.getOffhandItem())) {
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
    }

    private static boolean hasInfiniteBow(LivingEntity shooter, ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof BowItem)) {
            return false;
        }

        var enchantments = shooter.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return hasEnchantment(stack, enchantments.get(INFINITY).orElse(null))
                || hasEnchantment(stack, enchantments.get(ENDLESS_QUIVER).orElse(null));
    }

    private static boolean hasEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment != null && stack.getEnchantments().getLevel(enchantment) > 0;
    }
}
