package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.wings.enigmaticdelicacy;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;

/**
 * Lets the Enigmatic Delicacy Gluttony Charm bypass Wings' food-level
 * requirements while equipped. Wings' flight and landing exhaustion remain
 * unchanged; the charm only bypasses the food gates.
 */
@Pseudo
@Mixin(targets = "me.paulf.wings.server.apparatus.SimpleFlightApparatus", remap = false)
public abstract class GluttonyCharmFlightMixin {

    @Inject(
            method = "isUsable(Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void ktfr$allowFlightWithGluttonyCharm(
            Player player,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (ktfr$hasGluttonyCharm(player)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "isLandable(Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void ktfr$allowLandingWithGluttonyCharm(
            Player player,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (ktfr$hasGluttonyCharm(player)) {
            cir.setReturnValue(true);
        }
    }

    private static boolean ktfr$hasGluttonyCharm(Player player) {
        Item charm = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("enigmaticdelicacy", "gluttony_charm")
        );

        if (charm == Items.AIR) {
            return false;
        }

        try {
            Class<?> handler = Class.forName(
                    "auviotre.enigmatic.legacy.handlers.EnigmaticHandler",
                    true,
                    GluttonyCharmFlightMixin.class.getClassLoader()
            );
            Method hasCurio = handler.getMethod("hasCurio", LivingEntity.class, ItemLike.class);
            if (Boolean.TRUE.equals(hasCurio.invoke(null, player, charm))) {
                return true;
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // Fall back to ordinary inventory locations if the optional API is absent.
        }

        return player.getInventory().items.stream().anyMatch(stack -> stack.is(charm))
                || player.getInventory().armor.stream().anyMatch(stack -> stack.is(charm))
                || player.getInventory().offhand.stream().anyMatch(stack -> stack.is(charm));
    }
}
