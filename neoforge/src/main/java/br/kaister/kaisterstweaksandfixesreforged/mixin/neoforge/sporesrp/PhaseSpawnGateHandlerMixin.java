package br.kaister.kaisterstweaksandfixesreforged.mixin.neoforge.sporesrp;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Makes Spore:SRP!'s natural spawn-placement approvals respect Torchmaster's
 * configured entity filter and blocking-light radius.
 *
 * Spore:SRP! can explicitly set a spawn-placement check to SUCCEED for its
 * infected-biome spawns. Torchmaster intentionally yields to another mod's
 * ALLOW result unless aggressiveSpawnChecks is enabled, so its normal check
 * can be bypassed. This hook performs the same light-manager query before
 * Spore:SRP!'s handler can approve the spawn.
 *
 * Torchmaster is optional. Its API is accessed reflectively so KTFR does not
 * acquire a hard dependency on it.
 */
@Pseudo
@Mixin(targets = "com.maha_fish.sporesrp.handler.PhaseSpawnGateHandler", remap = false)
public abstract class PhaseSpawnGateHandlerMixin {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AtomicBoolean WARNED_ABOUT_TORCHMASTER_API = new AtomicBoolean();

    @Inject(
            method = "onPositionCheck",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void ktfr$respectTorchmaster(MobSpawnEvent.PositionCheck event, CallbackInfo ci) {
        if (!ModList.get().isLoaded("torchmaster")) {
            return;
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        Mob entity = event.getEntity();
        if (!"spore".equals(EntityType.getKey(entity.getType()).getNamespace())) {
            return;
        }

        MobSpawnType spawnType = event.getSpawnType();
        if (spawnType != MobSpawnType.NATURAL
                && spawnType != MobSpawnType.CHUNK_GENERATION
                && spawnType != MobSpawnType.PATROL) {
            return;
        }

        if (!ktfr$shouldBlock(entity, level, spawnType)) {
            return;
        }

        event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        ci.cancel();
    }

    private static boolean ktfr$shouldBlock(
            Mob entity,
            ServerLevel level,
            MobSpawnType spawnType
    ) {
        try {
            ClassLoader classLoader = Mob.class.getClassLoader();
            Class<?> torchmasterClass = Class.forName(
                    "net.xalcon.torchmaster.Torchmaster",
                    false,
                    classLoader
            );
            Method getRegistryForLevel = torchmasterClass.getMethod(
                    "getRegistryForLevel",
                    Level.class
            );

            Object registryResult = getRegistryForLevel.invoke(null, level);
            if (!(registryResult instanceof Optional<?> registry) || registry.isEmpty()) {
                return false;
            }

            Class<?> managerClass = Class.forName(
                    "net.xalcon.torchmaster.logic.entityblocking.IBlockingLightManager",
                    false,
                    classLoader
            );
            Method shouldBlockEntityType = managerClass.getMethod(
                    "shouldBlockEntityType",
                    EntityType.class,
                    Level.class,
                    Vec3.class,
                    MobSpawnType.class
            );

            return Boolean.TRUE.equals(shouldBlockEntityType.invoke(
                    registry.get(),
                    entity.getType(),
                    level,
                    entity.position(),
                    spawnType
            ));
        } catch (ReflectiveOperationException | LinkageError exception) {
            if (WARNED_ABOUT_TORCHMASTER_API.compareAndSet(false, true)) {
                LOGGER.warn(
                        "KTFR could not query Torchmaster's blocking-light registry; "
                                + "Spore:SRP! spawn checks will continue without the compatibility filter.",
                        exception
                );
            }
            return false;
        }
    }
}
