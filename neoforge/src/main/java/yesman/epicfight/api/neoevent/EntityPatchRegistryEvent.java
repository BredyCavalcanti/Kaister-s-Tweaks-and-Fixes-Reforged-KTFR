package yesman.epicfight.api.neoevent;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import yesman.epicfight.api.event.Event;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

import java.util.Map;
import java.util.function.Function;

/**
 * Legacy Epic Fight NeoForge event retained for Avalon 21.12.x.
 *
 * Epic Fight 21.17 moved this event from the NeoForge mod bus to its own
 * EventHook API. KTFR posts this compatibility event with the same mutable
 * registry map so Avalon can continue registering its entity patches.
 */
public final class EntityPatchRegistryEvent extends Event {
    private final Map<EntityType<?>, Function<Entity, EntityPatch<?>>> typeEntry;

    public EntityPatchRegistryEvent(
            Map<EntityType<?>, Function<Entity, EntityPatch<?>>> typeEntry
    ) {
        this.typeEntry = typeEntry;
    }

    public Map<EntityType<?>, Function<Entity, EntityPatch<?>>> getTypeEntry() {
        return this.typeEntry;
    }
}
