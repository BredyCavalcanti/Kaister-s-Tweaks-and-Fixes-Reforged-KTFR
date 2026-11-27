package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.wings;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Keeps both sides of Wings' thin feather geometry visible with
 * Sodium-family renderers by disabling face culling for the wing layer.
 */
@Mixin(targets = "me.paulf.wings.client.renderer.LayerWings", remap = false)
public abstract class LayerWingsNoCullMixin {

    @Redirect(
            method = "lambda$render$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderType;entityCutout(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;"
            ),
            remap = false
    )
    private RenderType ktfr$useNoCullForWings(ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }
}
