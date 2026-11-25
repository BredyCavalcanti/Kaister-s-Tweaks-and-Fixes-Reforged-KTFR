package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.epicfight;

import net.neoforged.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.event.Event;
import yesman.epicfight.api.event.EventHook;
import yesman.epicfight.api.client.neoevent.PatchedRenderersEvent;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Bridges the legacy Avalon item-renderer registration event into Epic Fight
 * 21.17's replacement EventHook so Avalon renderers are actually registered.
 */
@Pseudo
@Mixin(targets = "yesman.epicfight.client.events.engine.RenderEngine", remap = false)
public abstract class AvalonItemRendererEventMixin {

    @Redirect(
            method = "reloadItemRenderers",
            at = @At(
                    value = "INVOKE",
                    target = "Lyesman/epicfight/api/event/EventHook;post(Lyesman/epicfight/api/event/Event;)Lyesman/epicfight/api/event/Event;",
                    remap = false
            ),
            remap = false
    )
    private static Event ktfr$bridgeLegacyRendererRegistration(EventHook hook, Event event) {
        Event posted = hook.post(event);

        if (event.getClass().getName().equals(
                "yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent$Item")) {
            try {
                Field renderersField = event.getClass().getDeclaredField("itemRenderers");
                renderersField.setAccessible(true);
                @SuppressWarnings("unchecked")
                Map renderers = (Map) renderersField.get(event);
                ModLoader.postEvent(new PatchedRenderersEvent.RegisterItemRenderer(renderers));
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException(
                        "Could not bridge Epic Fight's item renderer registry to Avalon", exception);
            }
        }

        return posted;
    }
}
