package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.torchmaster;

import br.kaister.kaisterstweaksandfixesreforged.compat.torchmaster.FeralFlareLanternConnections;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public abstract class WallBlockFeralFlareLanternMixin {

    @Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
    private void ktfr$connectSideToFeralFlareLantern(
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
            case NORTH -> updated = updated.setValue(WallBlock.NORTH_WALL, WallSide.LOW);
            case EAST -> updated = updated.setValue(WallBlock.EAST_WALL, WallSide.LOW);
            case SOUTH -> updated = updated.setValue(WallBlock.SOUTH_WALL, WallSide.LOW);
            case WEST -> updated = updated.setValue(WallBlock.WEST_WALL, WallSide.LOW);
            default -> {
                return;
            }
        }
        cir.setReturnValue(updated);
    }
}
