package br.kaister.kaisterstweaksandfixesreforged.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * Loader-neutral compatibility gate shared by the Forge and NeoForge payloads.
 *
 * Do not statically link either loader API here: this class is loaded by Mixin
 * before the loader-specific integrations are considered.
 */
public final class CompatibilityMixinPlugin implements IMixinConfigPlugin {

    private static final boolean NEOFORGE = isClassPresent("net.neoforged.fml.ModList");

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // The original source-set mixins target Forge 1.20.1. They must never
        // be applied to the 1.21.1 runtime; its own port lives in .neoforge.
        boolean neoforgeMixin = mixinClassName.contains(".neoforge.");
        if (NEOFORGE != neoforgeMixin) {
            return false;
        }

        if (mixinClassName.contains(".simplybows.")) {
            return isModLoaded("simplybows");
        }

        if (mixinClassName.contains(".storagenetwork.")) {
            return isModLoaded("storagenetwork");
        }

        return true;
    }

    private static boolean isClassPresent(String name) {
        try {
            Class.forName(name, false, CompatibilityMixinPlugin.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError ignored) {
            return false;
        }
    }

    private static boolean isModLoaded(String modId) {
        try {
            if (NEOFORGE) {
                Class<?> modListClass = Class.forName("net.neoforged.fml.ModList");
                Object modList = modListClass.getMethod("get").invoke(null);
                Method isLoaded = modListClass.getMethod("isLoaded", String.class);
                return Boolean.TRUE.equals(isLoaded.invoke(modList, modId));
            }

            Class<?> loadingModListClass = Class.forName("net.minecraftforge.fml.loading.LoadingModList");
            Object loadingModList = loadingModListClass.getMethod("get").invoke(null);
            Method getModFileById = loadingModListClass.getMethod("getModFileById", String.class);
            return getModFileById.invoke(loadingModList, modId) != null;
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
