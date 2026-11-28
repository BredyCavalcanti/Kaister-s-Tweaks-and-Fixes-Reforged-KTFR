package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.simplybows;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.sweenus.simplybows.item.unique.SimplyBowItem", remap = false)
public abstract class SimplyBowItemMixin {

    private static final ResourceKey<Enchantment> ENDLESS_QUIVER = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath("apothic_enchanting", "endless_quiver")
    );

    /**
     * IceBowItem has its own ammo check path through SimplyBowItem's
     * simplybows$hasInfiniteAmmo helper. Extend that helper so multi-arrow bows
     * use Endless Quiver like Infinity.
     */
    @Inject(
            method = "simplybows$hasInfiniteAmmo(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private void ktfr$endlessQuiverCountsAsInfinity(
            Player shooter,
            ItemStack weapon,
            ItemStack projectile,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (cir.getReturnValue()) {
            return;
        }
        if (projectile == null || projectile.isEmpty() || !(projectile.getItem() instanceof ArrowItem)) {
            return;
        }

        Holder<Enchantment> enchantment = shooter.level().registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(ENDLESS_QUIVER)
                .orElse(null);
        if (enchantment != null && weapon.getEnchantments().getLevel(enchantment) > 0) {
            cir.setReturnValue(true);
        }
    }
    /**
     * Keep pickup changes scoped to projectiles created by Simply Bows.
     * A global Projectile#setOwner hook cannot identify the firing weapon.
     */
    @Inject(method = "createArrowEntity", at = @At("RETURN"), remap = false)
    private void ktfr$preventInfiniteArrowPickup(
            Level level,
            LivingEntity shooter,
            ItemStack weapon,
            ItemStack projectileStack,
            boolean critical,
            CallbackInfoReturnable<Projectile> cir
    ) {
        Projectile projectile = cir.getReturnValue();
        if (!(projectile instanceof AbstractArrow arrow)
                || projectileStack.isEmpty()
                || !(projectileStack.getItem() instanceof ArrowItem)) {
            return;
        }

        var enchantments = shooter.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> infinity = enchantments.get(ResourceKey.create(
                Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath("minecraft", "infinity")
        )).orElse(null);
        Holder<Enchantment> endlessQuiver = enchantments.get(ENDLESS_QUIVER).orElse(null);

        if ((infinity != null && weapon.getEnchantments().getLevel(infinity) > 0)
                || (endlessQuiver != null && weapon.getEnchantments().getLevel(endlessQuiver) > 0)) {
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
    }

}
