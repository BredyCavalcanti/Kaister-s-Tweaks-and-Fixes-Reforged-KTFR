package br.kaister.kaisterstweaksandfixesreforged.neoforge.wings;

import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Reuses Epic Fight's native creative-flight motions while Wings is actively
 * flying. Wings' animated-layer definition attaches its layer to Epic Fight's
 * original player model, so Epic Fight's renderer must remain enabled.
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
                            // Match Epic Fight's own creative-flight selection:
                            // CREATIVE_FLY while moving, CREATIVE_IDLE while hovering.
                            motionEvent.setMotion(isMoving(player)
                                    ? LivingMotions.CREATIVE_FLY
                                    : LivingMotions.CREATIVE_IDLE);
                        }
                    },
                    "ktfr.wings_epicfight"
            );


        });
    }

    private static boolean isMoving(Player player) {
        return Math.abs(player.getX() - player.xo) > 0.01D
                || Math.abs(player.getZ() - player.zo) > 0.01D;
    }

    private static boolean isFlying(Player player) {
        try {
            ClassLoader classLoader = WingsEpicFightClientCompat.class.getClassLoader();
            Class<?> flightsClass = Class.forName(
                    "me.paulf.wings.server.flight.Flights", true, classLoader
            );
            Class<?> flightType = Class.forName(
                    "me.paulf.wings.server.flight.Flight", true, classLoader
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

            // Invoke the public API on the Flight interface rather than the
            // concrete capability implementation, which may not be accessible.
            return Boolean.TRUE.equals(flightType.getMethod("isFlying").invoke(flight));
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }
}
