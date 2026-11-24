package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.simplybows;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.sweenus.simplybows.entity.HomingArrowEntity", remap = false)
public abstract class HomingArrowEffectMixin {

    @Inject(
            method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("RETURN"),
            remap = false
    )
    private void ktfr$copyArrowEffects(
            Level level,
            LivingEntity shooter,
            ItemStack projectileStack,
            ItemStack weaponStack,
            CallbackInfo ci
    ) {
        PotionContents contents = projectileStack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            ((ArrowAccessor) this).ktfr$setPotionContents(contents);
        }
    }
}
