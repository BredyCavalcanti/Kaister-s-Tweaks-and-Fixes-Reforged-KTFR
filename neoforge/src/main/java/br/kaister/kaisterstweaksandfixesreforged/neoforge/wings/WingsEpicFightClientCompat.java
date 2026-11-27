package br.kaister.kaisterstweaksandfixesreforged.neoforge.wings;

import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Connects Wings' live flight state to Epic Fight's client animation and
 * original-player-model validation hooks without linking Wings at compile time.
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
                            // ValidatePlayerModelEvent controls whether Epic Fight
                            // keeps the original player renderer active. Wings'
                            // LayerWings is attached to that renderer, so flight
                            // must force the original model path to stay enabled.
                            renderEvent.setShouldRender(true);
                        }
                    },
                    "ktfr.wings_epicfight"
            );
        });
    }

    private static boolean isFlying(Player player) {
        try {
            ClassLoader classLoader = WingsEpicFightClientCompat.class.getClassLoader();
            Class<?> flightsClass = Class.forName(
                    "me.paulf.wings.server.flight.Flights", true, classLoader
            );

            Method getFlight = null;
            for (Method method : flightsClass.getMethods()) {
                if (method.getName().equals("get")
                        && method.getParameterCount() == 1
                        && method.getParameterTypes()[0].isAssignableFrom(player.getClass())) {
                    getFlight = method;
                    break;
                }
            }
            if (getFlight == null) {
                return false;
            }

            Object result = getFlight.invoke(null, player);
            Object flight = result instanceof Optional<?> optional
                    ? optional.orElse(null)
                    : result;
            if (flight == null) {
                return false;
            }

            Method isFlying = flight.getClass().getMethod("isFlying");
            return Boolean.TRUE.equals(isFlying.invoke(flight));
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }
}
