package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.simplybows;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
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
}
