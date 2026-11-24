package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.spelllib;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Makes SpellLib's summon-health helper idempotent. Reapplying the same
 * permanent modifier is ignored, while all other modifier operations remain
 * untouched.
 */
@Pseudo
@Mixin(targets = "com.gametechbc.spelllib.utils.GSLGeneralUtils", remap = false)
public abstract class GSLGeneralUtilsMixin {

    @Redirect(
            method = "applySummonHealth",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;addPermanentModifier(Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;)V"
            )
    )
    private static void ktfr$ignoreDuplicateSummonHealthModifier(
            AttributeInstance attribute,
            AttributeModifier modifier
    ) {
        if (!attribute.hasModifier(modifier.id())) {
            attribute.addPermanentModifier(modifier);
        }
    }
}
