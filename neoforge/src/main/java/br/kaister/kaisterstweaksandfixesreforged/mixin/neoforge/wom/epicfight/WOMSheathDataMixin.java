package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.wom.epicfight;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;

/**
 * Weapons of Miracles' Satsujin animation writes wom:sheath even when the
 * active Epic Fight skill container has not registered that data key.
 *
 * Register the already-declared key lazily for the current container. If the
 * DeferredHolder itself is unresolved, skip only this invalid write instead
 * of allowing it to throw during the player's server tick.
 */
@Pseudo
@Mixin(value = SkillDataManager.class, remap = false)
public abstract class WOMSheathDataMixin {
    @Shadow
    public abstract boolean hasData(Holder<SkillDataKey<?>> key);

    @Shadow
    public abstract void registerData(Holder<SkillDataKey<?>> key);

    @Inject(method = "setDataRawtype", at = @At("HEAD"), cancellable = true, remap = false)
    private void ktfr$ensureWomSheathIsRegistered(
            Holder<SkillDataKey<?>> key,
            Object value,
            CallbackInfo callback
    ) {
        ResourceKey<?> resourceKey = key.unwrapKey().orElse(null);
        if (resourceKey == null
                || !resourceKey.location().getNamespace().equals("wom")
                || !resourceKey.location().getPath().equals("sheath")
                || this.hasData(key)) {
            return;
        }

        if (key instanceof DeferredHolder<?, ?> deferredHolder && !deferredHolder.isBound()) {
            callback.cancel();
            return;
        }

        this.registerData(key);
    }
}
