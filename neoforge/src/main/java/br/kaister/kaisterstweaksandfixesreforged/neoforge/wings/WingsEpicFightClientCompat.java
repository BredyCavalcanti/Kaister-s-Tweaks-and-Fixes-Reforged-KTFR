package br.kaister.kaisterstweaksandfixesreforged.neoforge.wings;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.client.event.EpicFightClientEventHooks;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.gameasset.Animations;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Reuses Epic Fight's native creative-flight motions while Wings is actively
 * flying. Wings' animated-layer definition attaches its layer to Epic Fight's
 * original player model, so Epic Fight's renderer must remain enabled.
 */
public final class WingsEpicFightClientCompat {
    private static final float MAX_EXTRA_FLIGHT_PITCH = 45.0F;
    private static final double FLIGHT_PITCH_START_SPEED = 0.10D;
    private static final double FLIGHT_PITCH_SPEED_SCALE = 50.0D;

    private WingsEpicFightClientCompat() {
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            installFlightPitchModifier();

            EpicFightClientEventHooks.Entity.MODIFY_PLAYER_LIVING_MOTION_BASE.registerEvent(
                    motionEvent -> {
                        // Do not replace an action/inaction animation with the
                        // flight loop. Epic Fight will resume living motion itself.
                        if (motionEvent.inaction()
                                || !motionEvent.getPlayerPatch().isEpicFightMode()) {
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

    /**
     * Extends Epic Fight's own flight pose correction instead of replacing its
     * animation. Faster forward movement adds a small progressive forward lean.
     */
    private static void installFlightPitchModifier() {
        if (Animations.BIPED_CREATIVE_FLYING == null
                || !Animations.BIPED_CREATIVE_FLYING.isPresent()) {
            return;
        }

        Animations.BIPED_CREATIVE_FLYING.get().getSubAnimations().forEach(animationAccessor -> {
            var animation = animationAccessor.get();
            var nativeModifier = animation.getProperty(StaticAnimationProperty.POSE_MODIFIER).orElse(null);
            if (nativeModifier == null) {
                return;
            }

            animation.addProperty(StaticAnimationProperty.POSE_MODIFIER,
                    (self, pose, entityPatch, elapsedTime, partialTicks) -> {
                        nativeModifier.modify(self, pose, entityPatch, elapsedTime, partialTicks);

                        if (!(entityPatch.getOriginal() instanceof Player player) || !isFlying(player)) {
                            return;
                        }

                        Vec3 movement = player.getDeltaMovement();
                        Vec3 view = player.getViewVector(partialTicks);
                        double horizontalSpeed = movement.horizontalDistance();
                        double viewLength = view.horizontalDistance();

                        if (horizontalSpeed <= FLIGHT_PITCH_START_SPEED || viewLength < 1.0E-4D) {
                            return;
                        }

                        double forwardAlignment = (movement.x * view.x + movement.z * view.z)
                                / (horizontalSpeed * viewLength);
                        if (forwardAlignment <= 0.0D) {
                            return;
                        }

                        float extraPitch = Mth.clamp(
                                (float) ((horizontalSpeed - FLIGHT_PITCH_START_SPEED)
                                        * FLIGHT_PITCH_SPEED_SCALE * forwardAlignment),
                                0.0F,
                                MAX_EXTRA_FLIGHT_PITCH
                        );

                        var root = pose.orElseEmpty("Root");
                        MathUtils.mulQuaternion(
                                QuaternionUtils.XP.rotationDegrees(extraPitch),
                                root.rotation(),
                                root.rotation()
                        );
                    });
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
