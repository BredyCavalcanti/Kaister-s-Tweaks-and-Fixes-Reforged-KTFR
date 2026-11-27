package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.torchmaster;

import br.kaister.kaisterstweaksandfixesreforged.compat.torchmaster.FeralFlareLanternConnections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public abstract class FenceBlockFeralFlareLanternMixin {

    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    private void ktfr$connectToFeralFlareLantern(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos,
            CallbackInfoReturnable<BlockState> cir
    ) {
        if (!FeralFlareLanternConnections.isMountedToward(neighborState, direction)) {
            return;
        }

        BlockState updated = cir.getReturnValue();
        switch (direction) {
            case NORTH -> updated = updated.setValue(FenceBlock.NORTH, true);
            case EAST -> updated = updated.setValue(FenceBlock.EAST, true);
            case SOUTH -> updated = updated.setValue(FenceBlock.SOUTH, true);
            case WEST -> updated = updated.setValue(FenceBlock.WEST, true);
            default -> {
                return;
            }
        }
        cir.setReturnValue(updated);
    }
}
