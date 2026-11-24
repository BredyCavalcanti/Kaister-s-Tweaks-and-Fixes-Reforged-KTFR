package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.simplybows;

import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Arrow.class)
public interface ArrowAccessor {

    @Invoker("setPotionContents")
    void ktfr$setPotionContents(PotionContents contents);
}
