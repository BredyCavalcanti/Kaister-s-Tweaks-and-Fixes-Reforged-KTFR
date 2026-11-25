package yesman.epicfight.api.neoevent.playerpatch;

import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.event.EpicFightEventHooks;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

/**
 * Legacy player-patch event base retained for Avalon 21.12.x.
 *
 * Epic Fight 21.17 replaced the NeoEvent dispatcher with EventHook.
 * Keeping this superclass preserves the binary method descriptor expected
 * by Avalon while forwarding the event to the modern Epic Fight hook.
 */
public class PlayerPatchEvent
        extends yesman.epicfight.api.event.types.animation.AttackPhaseEndEvent {

    protected PlayerPatchEvent(
            ServerPlayerPatch playerPatch,
            AnimationAccessor<? extends AttackAnimation> animation,
            AttackAnimation.Phase phase,
            int phaseOrder
    ) {
        super(playerPatch, animation, phase, phaseOrder, false);
    }

    public static void postAndFireSkillListeners(PlayerPatchEvent event) {
        EpicFightEventHooks.Animation.ATTACK_PHASE_END.postWithListener(
                event,
                event.getEntityPatch().getEventListener()
        );
    }
}
