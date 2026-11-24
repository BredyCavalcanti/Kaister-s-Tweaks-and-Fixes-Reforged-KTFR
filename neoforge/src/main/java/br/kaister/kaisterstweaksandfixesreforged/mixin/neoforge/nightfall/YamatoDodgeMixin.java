package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.nightfall;

import br.kaister.kaisterstweaksandfixesreforged.neoforge.NightfallSkillData;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Gives Nightfall's Yamato Dodge its own registered jump counter instead of
 * relying on Epic Fight's global JUMP_COUNT holder.
 */
@Pseudo
@Mixin(targets = "com.hm.efn.skill.dodge.YamatoDodge", remap = false)
public abstract class YamatoDodgeMixin {

    @Redirect(
            method = "*",
            at = @At(
                    value = "FIELD",
                    target = "Lyesman/epicfight/registry/entries/EpicFightSkillDataKeys;JUMP_COUNT:Lnet/neoforged/neoforge/registries/DeferredHolder;",
                    opcode = Opcodes.GETSTATIC
            ),
            remap = false
    )
    private static DeferredHolder<?, ?> ktfr$useRegisteredJumpCount() {
        return NightfallSkillData.yamatoJumpCount();
    }
}
