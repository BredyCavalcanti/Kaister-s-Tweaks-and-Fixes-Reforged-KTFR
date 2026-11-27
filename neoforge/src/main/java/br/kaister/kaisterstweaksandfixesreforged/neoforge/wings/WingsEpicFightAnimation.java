package br.kaister.kaisterstweaksandfixesreforged.neoforge.wings;

import net.neoforged.bus.api.IEventBus;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Armatures;

/**
 * Registers KTFR's own Epic Fight living motion and flight animation.
 *
 * This is an independent implementation of the Wings/Epic Fight bridge;
 * it does not bundle or depend on the separate EFWingsCompat mod.
 */
public final class WingsEpicFightAnimation {
    public static AnimationManager.AnimationAccessor<StaticAnimation> WING_FLIGHT;

    private WingsEpicFightAnimation() {
    }

    public static void register(IEventBus modEventBus) {
        LivingMotion.ENUM_MANAGER.registerEnumCls("ktfr", KtfrLivingMotions.class);
        modEventBus.addListener(WingsEpicFightAnimation::registerAnimations);
    }

    private static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
        event.newBuilder("ktfr", builder -> WING_FLIGHT = builder.nextAccessor(
                "biped/living/wing",
                accessor -> new StaticAnimation(true, accessor, Armatures.BIPED)
        ));
    }

    public enum KtfrLivingMotions implements LivingMotion {
        WING;

        private final int id;

        KtfrLivingMotions() {
            this.id = LivingMotion.ENUM_MANAGER.assign(this);
        }

        @Override
        public int universalOrdinal() {
            return this.id;
        }
    }
}
