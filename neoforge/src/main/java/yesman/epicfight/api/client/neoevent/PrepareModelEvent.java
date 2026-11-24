package yesman.epicfight.api.client.neoevent;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

/**
 * Compatibility shim for addons compiled against Epic Fight's pre-21.17
 * NeoForge event package. Avalon 21.12.6.2 still references this class.
 *
 * The event is forwarded to Epic Fight's current client event hook, while
 * remaining a NeoForge event so Avalon can keep posting it on NeoForge's bus.
 */
public class PrepareModelEvent extends Event implements ICancellableEvent {
    private final SkinnedMesh mesh;
    private final LivingEntityPatch<?> entityPatch;
    private final MultiBufferSource buffer;
    private final PoseStack poseStack;
    private final int packedLight;
    private final float partialTicks;
    private final PatchedEntityRenderer<?, ?, ?, ?> renderer;

    public PrepareModelEvent(
            PatchedEntityRenderer<?, ?, ?, ?> renderer,
            SkinnedMesh mesh,
            LivingEntityPatch<?> entityPatch,
            MultiBufferSource buffer,
            PoseStack poseStack,
            int packedLight,
            float partialTicks
    ) {
        this.renderer = renderer;
        this.mesh = mesh;
        this.entityPatch = entityPatch;
        this.buffer = buffer;
        this.poseStack = poseStack;
        this.packedLight = packedLight;
        this.partialTicks = partialTicks;

        yesman.epicfight.api.client.event.types.render.PrepareModelEvent currentEvent =
                new yesman.epicfight.api.client.event.types.render.PrepareModelEvent(
                        renderer, mesh, entityPatch, buffer, poseStack, packedLight, partialTicks
                );
        EpicFightClientEventHooks.Render.PREPARE_MODEL_TO_RENDER.post(currentEvent);
        if (currentEvent.isCanceled()) {
            this.setCanceled(true);
        }
    }

    public SkinnedMesh getMesh() {
        return this.mesh;
    }

    public LivingEntityPatch<?> getEntityPatch() {
        return this.entityPatch;
    }

    public MultiBufferSource getBuffer() {
        return this.buffer;
    }

    public PoseStack getPoseStack() {
        return this.poseStack;
    }

    public int getPackedLight() {
        return this.packedLight;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public PatchedEntityRenderer<?, ?, ?, ?> getRenderer() {
        return this.renderer;
    }
}
