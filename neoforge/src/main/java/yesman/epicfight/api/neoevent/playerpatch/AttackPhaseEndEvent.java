package yesman.epicfight.api.neoevent.playerpatch;

import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * Legacy attack-phase event retained for Avalon 21.12.x.
 *
 * Epic Fight 21.17 moved the event implementation to
 * api.event.types.animation.AttackPhaseEndEvent.
 */
public final class AttackPhaseEndEvent extends PlayerPatchEvent {

    public AttackPhaseEndEvent(
            ServerPlayerPatch playerPatch,
            AnimationAccessor<? extends AttackAnimation> animation,
            AttackAnimation.Phase phase,
            int phaseOrder
    ) {
        super(playerPatch, animation, phase, phaseOrder);
    }
}
