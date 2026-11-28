package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.wings;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * Keeps Wings' thin feather geometry visible with Sodium.
 *
 * Sodium's optimized ModelPart.Cube renderer reconstructs cubes from the
 * vanilla Cube constructor arguments. Wings' Model3DTexture subclasses Cube
 * but replaces its polygon list after construction, so Sodium sees an empty
 * cuboid and skips the custom feather geometry. A plain VertexConsumer
 * decorator makes Sodium fall back to vanilla Cube.compile for this layer.
 */
@Pseudo
@Mixin(targets = "me.paulf.wings.client.renderer.LayerWings", remap = false)
public abstract class LayerWingsNoCullMixin {

    @Redirect(
            method = "lambda$render$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderType;entityCutout(Lnet/minecraft/resources/ResourceLocation;)Lnet/minecraft/client/renderer/RenderType;",
                    remap = true
            ),
            remap = false
    )
    private RenderType ktfr$useNoCullForWings(ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }

    @Redirect(
            method = "lambda$render$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource;getBuffer(Lnet/minecraft/client/renderer/RenderType;)Lcom/mojang/blaze3d/vertex/VertexConsumer;",
                    remap = true
            ),
            remap = false
    )
    private VertexConsumer ktfr$preserveCustomWingPolygons(
            MultiBufferSource buffers,
            RenderType renderType
    ) {
        VertexConsumer consumer = buffers.getBuffer(renderType);
        if (!ModList.get().isLoaded("sodium")) {
            return consumer;
        }

        // Deliberately do not implement Sodium's VertexBufferWriter interface:
        // this forces Sodium's CubeMixin to leave Model3DTexture on vanilla
        // polygon compilation while preserving the real RenderType buffer.
        return (VertexConsumer) Proxy.newProxyInstance(
                VertexConsumer.class.getClassLoader(),
                new Class<?>[]{VertexConsumer.class},
                (proxy, method, args) -> {
                    try {
                        Object result = method.invoke(consumer, args);
                        return result == consumer ? proxy : result;
                    } catch (InvocationTargetException exception) {
                        throw exception.getCause();
                    }
                }
        );
    }
}
