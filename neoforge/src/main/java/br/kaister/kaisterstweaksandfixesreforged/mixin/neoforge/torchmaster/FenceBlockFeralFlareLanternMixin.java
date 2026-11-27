package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.torchmaster;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public abstract class FenceBlockFeralFlareLanternMixin {

    @Inject(method = "connectsTo(Lnet/minecraft/world/level/block/state/BlockState;ZLnet/minecraft/core/Direction;)Z", at = @At("RETURN"), cancellable = true, remap = false)
    private void ktfr$connectToFeralFlareLantern(
            BlockState neighborState,
            boolean sideFullSquare,
            Direction direction,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!cir.getReturnValueZ()
                && FeralFlareLanternConnections.isMountedToward(neighborState, direction)) {
            cir.setReturnValue(true);
        }
    }
}
