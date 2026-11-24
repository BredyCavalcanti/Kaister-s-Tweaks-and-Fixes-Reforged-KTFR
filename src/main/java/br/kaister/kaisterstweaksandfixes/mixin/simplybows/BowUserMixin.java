package br.kaister.kaisterstweaksandfixesreforged.mixin.simplybows;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.sweenus.simplybows.util.BowUser", remap = false)
public abstract class BowUserMixin {
    private static final ResourceLocation ENDLESS_QUIVER = new ResourceLocation("apotheosis", "endless_quiver");

    @Inject(method = "hasInfiniteAmmo", at = @At("RETURN"), cancellable = true)
    private static void simplybowscompat$apotheosisEndlessQuiver(
            LivingEntity user,
            ItemStack weapon,
            ItemStack projectile,
            CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (projectile == null || projectile.isEmpty() || !(projectile.getItem() instanceof ArrowItem)) return;

        Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.getOptional(ENDLESS_QUIVER).orElse(null);
        if (enchantment != null && weapon.getEnchantmentLevel(enchantment) > 0) {
            cir.setReturnValue(true);
        }
    }
}
