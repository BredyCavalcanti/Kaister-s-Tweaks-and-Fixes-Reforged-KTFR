package br.kaister.kaisterstweaksandfixesreforged.mixin.simplybows;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Predicate;

@Pseudo
@Mixin(targets = "net.sweenus.simplybows.util.HelperMethods", remap = false)
public abstract class HelperMethodsMixin {
    @Inject(method = "findArrowStacks", at = @At("RETURN"))
    private static void simplybowscompat$includeSupplementariesQuiver(
            Player player,
            CallbackInfoReturnable<Map<ItemStack, Integer>> cir) {
        try {
            Class<?> quiverClass = Class.forName("net.mehvahdjukaar.supplementaries.common.items.QuiverItem");
            Method findActiveQuiver = quiverClass.getMethod("findActiveQuiver", LivingEntityClass());
            ItemStack quiver = (ItemStack) findActiveQuiver.invoke(null, player);
            if (quiver == null || quiver.isEmpty()) return;

            Method getQuiverData = quiverClass.getMethod("getQuiverData", ItemStack.class);
            Object data = getQuiverData.invoke(null, quiver);
            if (data == null) return;

            Method getSelected = data.getClass().getMethod("getSelected", Predicate.class);
            Predicate<ItemStack> arrows = stack -> !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
            ItemStack selected = (ItemStack) getSelected.invoke(data, arrows);
            if (selected != null && !selected.isEmpty()) {
                cir.getReturnValue().put(selected, selected.getCount());
            }
        } catch (ClassNotFoundException ignored) {
            // Supplementaries is optional.
        } catch (ReflectiveOperationException ignored) {
            // Leave Simply Bows behavior untouched if Supplementaries internals change.
        }
    }

    private static Class<?> LivingEntityClass() throws ClassNotFoundException {
        return Class.forName("net.minecraft.world.entity.LivingEntity");
    }
}
