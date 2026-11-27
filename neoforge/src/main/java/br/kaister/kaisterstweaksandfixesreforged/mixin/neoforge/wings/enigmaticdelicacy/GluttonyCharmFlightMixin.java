package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.wings.enigmaticdelicacy;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Lets the Enigmatic Delicacy Gluttony Charm bypass Wings' food-level
 * requirement while the charm is in the player's inventory or equipped
 * in Curios. Flight exhaustion and landing requirements remain unchanged.
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

    private static boolean ktfr$hasGluttonyCharm(Player player) {
        Item charm = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("enigmaticdelicacy", "gluttony_charm")
        );

        if (charm == Items.AIR) {
            return false;
        }

        if (player.getInventory().items.stream().anyMatch(stack -> stack.is(charm))
                || player.getInventory().armor.stream().anyMatch(stack -> stack.is(charm))
                || player.getInventory().offhand.stream().anyMatch(stack -> stack.is(charm))) {
            return true;
        }

        return ktfr$isEquippedInCurios(player, charm);
    }

    /**
     * Curios is an optional integration. Resolve its API reflectively so this
     * mixin has no hard class-linkage dependency on Curios.
     */
    private static boolean ktfr$isEquippedInCurios(Player player, Item charm) {
        try {
            Class<?> curiosApi = Class.forName(
                    "top.theillusivec4.curios.api.CuriosApi",
                    true,
                    GluttonyCharmFlightMixin.class.getClassLoader()
            );
            Method getCuriosInventory = curiosApi.getMethod(
                    "getCuriosInventory",
                    LivingEntity.class
            );
            Object result = getCuriosInventory.invoke(null, player);
            if (result instanceof Optional<?> optional && optional.isPresent()) {
                Object handler = optional.get();
                Class<?> curiosHandler = Class.forName("top.theillusivec4.curios.api.type.capability.ICuriosItemHandler");
                Method isEquipped = curiosHandler.getMethod("isEquipped", Item.class);
                return Boolean.TRUE.equals(isEquipped.invoke(handler, charm));
            }
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // The charm can still work from the regular player inventory.
        }
        return false;
    }
}
