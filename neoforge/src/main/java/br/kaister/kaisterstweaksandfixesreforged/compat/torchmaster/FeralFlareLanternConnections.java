package br.kaister.kaisterstweaksandfixesreforged.compat.torchmaster;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Shared connection check for vanilla walls and fences next to Torchmaster's
 * wall-mounted Feral Flare Lantern.
 */
public final class FeralFlareLanternConnections {

    private static final ResourceLocation FERAL_FLARE_LANTERN =
            ResourceLocation.fromNamespaceAndPath("torchmaster", "feral_flare_lantern");

    private FeralFlareLanternConnections() {
    }

    public static boolean isMountedToward(BlockState neighborState, Direction directionToNeighbor) {
        return BuiltInRegistries.BLOCK.getOptional(FERAL_FLARE_LANTERN)
                .filter(neighborState::is)
                .isPresent()
                && neighborState.hasProperty(DirectionalBlock.FACING)
                && neighborState.getValue(DirectionalBlock.FACING) == directionToNeighbor.getOpposite();
    }
}
