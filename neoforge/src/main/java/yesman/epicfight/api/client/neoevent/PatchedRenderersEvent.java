package yesman.epicfight.api.client.neoevent;

import com.google.gson.JsonElement;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;

import java.util.Map;
import java.util.function.Function;

/**
 * Legacy NeoForge renderer events used by Avalon 21.12.x.
 * Epic Fight 21.17 replaced these events with RegisterPatchedRenderersEvent.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class PatchedRenderersEvent extends Event implements IModBusEvent {

    public static final class Add extends PatchedRenderersEvent {
        private final Map<EntityType<?>, Function<EntityType<?>, PatchedEntityRenderer>> typeEntry;
        private final EntityRendererProvider.Context context;

        public Add(
                Map<EntityType<?>, Function<EntityType<?>, PatchedEntityRenderer>> typeEntry,
                EntityRendererProvider.Context context
        ) {
            this.typeEntry = typeEntry;
            this.context = context;
        }

        public void addPatchedEntityRenderer(
                EntityType<?> type,
                Function<EntityType<?>, PatchedEntityRenderer> rendererFactory
        ) {
            this.typeEntry.put(type, rendererFactory);
        }

        public EntityRendererProvider.Context getContext() {
            return this.context;
        }
    }

    public static class RegisterItemRenderer extends PatchedRenderersEvent {
        private final Map<ResourceLocation, Function<JsonElement, RenderItemBase>> itemRenderers;

        public RegisterItemRenderer(
                Map<ResourceLocation, Function<JsonElement, RenderItemBase>> itemRenderers
        ) {
            this.itemRenderers = itemRenderers;
        }

        public void addItemRenderer(
                ResourceLocation id,
                Function<JsonElement, RenderItemBase> provider
        ) {
            if (this.itemRenderers.containsKey(id)) {
                throw new IllegalArgumentException("Item renderer " + id + " already registered.");
            }
            this.itemRenderers.put(id, provider);
        }
    }
}
