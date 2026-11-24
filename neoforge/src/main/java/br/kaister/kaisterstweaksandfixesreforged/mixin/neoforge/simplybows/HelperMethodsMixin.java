package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.simplybows;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Pseudo
@Mixin(targets = "net.sweenus.simplybows.util.HelperMethods", remap = false)
public abstract class HelperMethodsMixin {

    @Inject(method = "findArrowStacks", at = @At("RETURN"), remap = false)
    @SuppressWarnings("unchecked")
    private static void ktfr$includeSupplementariesQuiver(
            Player player,
            CallbackInfoReturnable<Map<ItemStack, Integer>> cir
    ) {
        try {
            Class<?> quiverClass = Class.forName(
                    "net.mehvahdjukaar.supplementaries.common.items.QuiverItem"
            );
            Method findActiveQuiver = quiverClass.getMethod("findActiveQuiver", LivingEntity.class);
            ItemStack quiver = (ItemStack) findActiveQuiver.invoke(null, player);
            if (quiver == null || quiver.isEmpty()) {
                return;
            }

            Object quiverItem = quiver.getItem();
            Object componentType = quiverClass.getMethod("getComponentType").invoke(quiverItem);
            Class<?> componentTypeClass = Class.forName("net.minecraft.core.component.DataComponentType");
            Object content = ItemStack.class.getMethod("get", componentTypeClass).invoke(quiver, componentType);
            if (content == null) {
                return;
            }

            // QuiverContent.getSelected(Predicate) returns a copy in 1.21.1.
            // Use the live component list so Simply Bows' normal stack decrement
            // also consumes the arrow in the actual quiver.
            List<ItemStack> contents = (List<ItemStack>) content.getClass()
                    .getMethod("getContentUnsafe").invoke(content);
            int selectedSlot = (int) content.getClass().getMethod("getSelectedSlot").invoke(content);
            if (contents.isEmpty()) {
                return;
            }

            for (int offset = 0; offset < contents.size(); offset++) {
                ItemStack selected = contents.get((selectedSlot + offset) % contents.size());
                if (!selected.isEmpty() && selected.getItem() instanceof ArrowItem) {
                    cir.getReturnValue().put(selected, selected.getCount());
                    return;
                }
            }
        } catch (ClassNotFoundException ignored) {
            // Supplementaries is optional.
        } catch (ReflectiveOperationException | LinkageError ignored) {
            // Preserve Simply Bows behavior if Supplementaries changes its internals.
        }
    }
}
