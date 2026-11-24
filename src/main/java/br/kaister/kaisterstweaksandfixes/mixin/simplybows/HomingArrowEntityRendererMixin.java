package br.kaister.kaisterstweaksandfixesreforged.mixin.simplybows;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        targets = "net.sweenus.simplybows.client.renderer.HomingArrowEntityRenderer",
        remap = false
)
public abstract class HomingArrowEntityRendererMixin {

    private static final ResourceLocation SIMPLYBOWSCOMPAT$ORIGINAL_HOMING_TEXTURE =
            new ResourceLocation("simplybows", "textures/item/ice_bow/ice_bow_arrow.png");

    /**
     * Keep Simply Bows' original homing-arrow texture even when the projectile
     * carries tipped-arrow potion data.
     */
    @Inject(
            method = "getTexture(Lnet/sweenus/simplybows/entity/HomingArrowEntity;)Lnet/minecraft/resources/ResourceLocation;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void simplybowscompat$keepOriginalHomingTexture(
            CallbackInfoReturnable<ResourceLocation> cir
    ) {
        cir.setReturnValue(SIMPLYBOWSCOMPAT$ORIGINAL_HOMING_TEXTURE);
    }
}