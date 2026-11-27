package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.wings.epicfight;

import br.kaister.kaisterstweaksandfixesreforged.neoforge.wings.WingsEpicFightAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.Animator;

/**
 * Adds KTFR's Wings flight animation to Epic Fight's player animator.
 */
@Pseudo
@Mixin(
        targets = "yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch",
        remap = false
)
public abstract class PlayerPatchMixin {
    @Inject(method = "initAnimator", at = @At("TAIL"), remap = false)
    private void ktfr$addWingsFlightAnimation(Animator animator, CallbackInfo ci) {
        if (WingsEpicFightAnimation.WING_FLIGHT != null) {
            animator.addLivingAnimation(
                    WingsEpicFightAnimation.KtfrLivingMotions.WING,
                    WingsEpicFightAnimation.WING_FLIGHT
            );
        }
    }
}
