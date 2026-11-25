package yesman.epicfight.api.client.neoevent;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;

import java.util.Map;
import java.util.function.Function;

/**
 * Compatibility event retained for addons compiled against Epic Fight's
 * pre-21.17 NeoForge renderer-registration API.
 */
@SuppressWarnings("rawtypes")
public abstract class PatchedRenderersEvent extends Event implements IModBusEvent {

    public static class RegisterItemRenderer extends PatchedRenderersEvent {
        private final Map<ResourceLocation, Function<JsonElement, RenderItemBase>> itemRenderers;

        public RegisterItemRenderer(Map<ResourceLocation, Function<JsonElement, RenderItemBase>> itemRenderers) {
            this.itemRenderers = itemRenderers;
        }

        public void addItemRenderer(ResourceLocation id, Function<JsonElement, RenderItemBase> provider) {
            if (this.itemRenderers.containsKey(id)) {
                throw new IllegalArgumentException("Item renderer " + id + " already registered.");
            }
            this.itemRenderers.put(id, provider);
        }
    }
}
