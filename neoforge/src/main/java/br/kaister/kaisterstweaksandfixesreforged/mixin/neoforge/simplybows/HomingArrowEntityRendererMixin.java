package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.simplybows;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.sweenus.simplybows.client.renderer.HomingArrowEntityRenderer", remap = false)
public abstract class HomingArrowEntityRendererMixin {

    private static final ResourceLocation KTFR$ORIGINAL_HOMING_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "simplybows",
                    "textures/item/ice_bow/ice_bow_arrow.png"
            );

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true, remap = false)
    private void ktfr$keepOriginalHomingTexture(CallbackInfoReturnable<ResourceLocation> cir) {
        cir.setReturnValue(KTFR$ORIGINAL_HOMING_TEXTURE);
    }
}
