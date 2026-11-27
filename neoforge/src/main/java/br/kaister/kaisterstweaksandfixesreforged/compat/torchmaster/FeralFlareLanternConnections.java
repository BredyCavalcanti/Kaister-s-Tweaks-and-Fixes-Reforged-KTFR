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
        if (!directionToNeighbor.getAxis().isHorizontal()
                || !neighborState.is(BuiltInRegistries.BLOCK.get(FERAL_FLARE_LANTERN))
                || !neighborState.hasProperty(DirectionalBlock.FACING)) {
            return false;
        }

        // FeralFlareLanternBlock stores the face normal (the direction it faces),
        // which points from its supporting block toward the lantern's open side.
        // From the wall/fence, the adjacent lantern must face back toward us.
        return neighborState.getValue(DirectionalBlock.FACING) == directionToNeighbor.getOpposite();
    }
}
