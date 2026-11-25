package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.epicfight;

import net.neoforged.fml.ModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.event.Event;
import yesman.epicfight.api.event.EventHook;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Bridges Epic Fight 21.17's internal entity-patch registry hook to the
 * legacy NeoForge mod-bus event consumed by Avalon 21.12.x.
 */
@Pseudo
@Mixin(
        targets = "yesman.epicfight.world.capabilities.provider.CommonEntityPatchProvider",
        remap = false
)
public abstract class AvalonEntityPatchRegistryMixin {

    @Redirect(
            method = "registerVanillaEntityPatches",
            at = @At(
                    value = "INVOKE",
                    target = "Lyesman/epicfight/api/event/EventHook;post(Lyesman/epicfight/api/event/Event;)Lyesman/epicfight/api/event/Event;",
                    remap = false
            ),
            remap = false
    )
    private static Event ktfr$bridgeEntityPatchRegistry(EventHook hook, Event event) {
        Event posted = hook.post(event);

        if (event.getClass().getName().equals(
                "yesman.epicfight.api.event.types.registry.EntityPatchRegistryEvent")) {
            try {
                Method getter = event.getClass().getMethod("getTypeEntry");
                Map<?, ?> registry = (Map<?, ?>) getter.invoke(event);
                ModLoader.postEvent(new yesman.epicfight.api.neoevent.EntityPatchRegistryEvent(
                        (Map) registry
                ));
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException(
                        "Could not bridge Epic Fight entity patch registration to Avalon",
                        exception
                );
            }
        }

        return posted;
    }
}
