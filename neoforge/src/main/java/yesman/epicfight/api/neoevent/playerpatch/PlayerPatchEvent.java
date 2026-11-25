package yesman.epicfight.api.neoevent.playerpatch;

import yesman.epicfight.api.event.EpicFightEventHooks;

/**
 * Legacy Epic Fight player-patch event dispatcher retained for Avalon 21.12.x.
 *
 * Epic Fight 21.17 replaced the NeoEvent dispatcher with EventHook.
 */
public final class PlayerPatchEvent {

    private PlayerPatchEvent() {
    }

    public static void postAndFireSkillListeners(AttackPhaseEndEvent event) {
        EpicFightEventHooks.Animation.ATTACK_PHASE_END.postWithListener(
                event,
                event.getEntityPatch().getEventListener()
        );
    }
}
