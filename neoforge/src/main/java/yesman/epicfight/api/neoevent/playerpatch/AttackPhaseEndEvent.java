package yesman.epicfight.api.neoevent.playerpatch;

import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * Legacy Epic Fight event retained for Avalon 21.12.x.
 *
 * Epic Fight 21.17 moved this event to
 * api.event.types.animation.AttackPhaseEndEvent.
 */
public final class AttackPhaseEndEvent
        extends yesman.epicfight.api.event.types.animation.AttackPhaseEndEvent {

    public AttackPhaseEndEvent(
            ServerPlayerPatch playerPatch,
            AnimationAccessor<? extends AttackAnimation> animation,
            AttackAnimation.Phase phase,
            int phaseOrder
    ) {
        super(playerPatch, animation, phase, phaseOrder, false);
    }
}
