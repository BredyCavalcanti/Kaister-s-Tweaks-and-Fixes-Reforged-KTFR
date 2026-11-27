package br.kaister.kaisterstweaksandfixesreforged.neoforge.wings;

import me.paulf.wings.server.flight.Flight;
import me.paulf.wings.server.flight.Flights;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;

/**
 * Connects Wings' live flight state to Epic Fight's client animation and
 * original-player-model validation hooks.
 */
public final class WingsEpicFightClientCompat {
    private WingsEpicFightClientCompat() {
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EpicFightClientEventHooks.Entity.MODIFY_PLAYER_LIVING_MOTION_BASE.registerEvent(
                    motionEvent -> {
                        if (!motionEvent.getPlayerPatch().isEpicFightMode()) {
                            return;
                        }

                        Player player = (Player) motionEvent.getPlayerPatch().getOriginal();
                        if (isFlying(player)) {
                            motionEvent.setMotion(WingsEpicFightAnimation.KtfrLivingMotions.WING);
                        }
                    },
                    "ktfr.wings_epicfight"
            );

            EpicFightClientEventHooks.Render.VALIDATE_PLAYER_MODEL_TO_RENDER.registerEvent(
                    renderEvent -> {
                        Player player = (Player) renderEvent.getPlayerPatch().getOriginal();
                        if (isFlying(player)) {
                            // Wings' own LayerWings is attached to the original
                            // player renderer, so let it render instead of the
                            // Epic Fight replacement model while airborne.
                            renderEvent.setShouldRender(false);
                        }
                    },
                    "ktfr.wings_epicfight"
            );
        });
    }

    private static boolean isFlying(Player player) {
        return Flights.get(player).map(Flight::isFlying).orElse(false);
    }
}
