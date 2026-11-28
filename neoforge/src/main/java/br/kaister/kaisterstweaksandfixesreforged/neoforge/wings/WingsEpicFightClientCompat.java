package br.kaister.kaisterstweaksandfixesreforged.neoforge.wings;

import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Reuses Epic Fight's native creative-flight motion while Wings is actively
 * flying, and lets the vanilla player renderer draw Wings' render layer.
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
                            // This is the same LivingMotion Epic Fight uses for
                            // creative flight, including its native directional
                            // forward/backward animation handling.
                            motionEvent.setMotion(LivingMotions.CREATIVE_FLY);
                        }
                    },
                    "ktfr.wings_epicfight"
            );

            EpicFightClientEventHooks.Render.VALIDATE_PLAYER_MODEL_TO_RENDER.registerEvent(
                    renderEvent -> {
                        Player player = (Player) renderEvent.getPlayerPatch().getOriginal();
                        if (isFlying(player)) {
                            // Epic Fight's custom player renderer suppresses vanilla
                            // player layers. Disable it while Wings is active so the
                            // original renderer can draw LayerWings.
                            renderEvent.setShouldRender(false);
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
