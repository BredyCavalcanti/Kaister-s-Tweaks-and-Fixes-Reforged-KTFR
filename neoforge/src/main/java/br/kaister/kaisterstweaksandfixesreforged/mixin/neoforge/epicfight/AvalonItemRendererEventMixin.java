package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.epicfight;

import net.neoforged.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.client.neoevent.PatchedRenderersEvent;
import yesman.epicfight.api.event.Event;
import yesman.epicfight.api.event.EventHook;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Bridges Epic Fight 21.17's renderer registration events into the legacy
 * mod-bus events expected by Avalon 21.12.x.
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
        String eventName = event.getClass().getName();

        try {
            if (eventName.equals(
                    "yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent$Item")) {
                Map renderers = (Map) ktfr$getField(event, "itemRenderers");
                ModLoader.postEvent(new PatchedRenderersEvent.RegisterItemRenderer(renderers));
            } else if (eventName.equals(
                    "yesman.epicfight.api.client.event.types.registry.RegisterPatchedRenderersEvent$AddEntity")) {
                Map renderers = (Map) ktfr$getField(event, "entityRendererProvider");
                Object context = ktfr$getField(event, "context");
                ModLoader.postEvent(new PatchedRenderersEvent.Add(renderers,
                        (net.minecraft.client.renderer.entity.EntityRendererProvider.Context) context));
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                    "Could not bridge Epic Fight renderer registration events to Avalon", exception);
        }

        return posted;
    }

    private static Object ktfr$getField(Object instance, String name) throws ReflectiveOperationException {
        Field field = instance.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(instance);
    }
}
