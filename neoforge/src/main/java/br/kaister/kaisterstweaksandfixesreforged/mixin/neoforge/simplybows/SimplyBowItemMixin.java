package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.simplybows;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.sweenus.simplybows.item.unique.SimplyBowItem", remap = false)
public abstract class SimplyBowItemMixin {

    private static final ResourceKey<Enchantment> INFINITY = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath("minecraft", "infinity")
    );
    private static final ResourceKey<Enchantment> ENDLESS_QUIVER = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath("apotheosis", "endless_quiver")
    );

    @Inject(method = "shootProjectile", at = @At("HEAD"), remap = false)
    private void ktfr$preventInfiniteArrowPickup(
            LivingEntity shooter,
            Projectile projectile,
            int index,
            float speed,
            float divergence,
            float yaw,
            LivingEntity target,
            CallbackInfo ci
    ) {
        if (!(projectile instanceof AbstractArrow arrow)) {
            return;
        }

        Object thisItem = this;
        ItemStack weapon = shooter.getMainHandItem().getItem() == thisItem
                ? shooter.getMainHandItem()
                : shooter.getOffhandItem().getItem() == thisItem
                    ? shooter.getOffhandItem()
                    : ItemStack.EMPTY;

        if (weapon.isEmpty()) {
            return;
        }

        if (hasEnchantment(shooter, weapon, INFINITY) || hasEnchantment(shooter, weapon, ENDLESS_QUIVER)) {
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
    }

    private static boolean hasEnchantment(
            LivingEntity shooter,
            ItemStack stack,
            ResourceKey<Enchantment> key
    ) {
        Holder<Enchantment> enchantment = shooter.level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(key)
                .orElse(null);
        return enchantment != null && stack.getEnchantments().getLevel(enchantment) > 0;
    }
}
