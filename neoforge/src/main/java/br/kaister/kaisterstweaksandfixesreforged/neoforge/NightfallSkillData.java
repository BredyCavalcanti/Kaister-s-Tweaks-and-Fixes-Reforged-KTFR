package br.kaister.kaisterstweaksandfixesreforged.neoforge;

import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.lang.reflect.Method;

/**
 * Nightfall's Yamato Dodge uses Epic Fight's jump-count holder, which can be
 * unresolved in the affected runtime. Register an equivalent KTFR-owned key
 * for YamatoDodge and let the compatibility mixin redirect the skill to it.
 *
 * Reflection keeps Epic Fight and Nightfall optional at compile time.
 */
public final class NightfallSkillData {
    private static final String EPIC_FIGHT_REGISTRIES =
            "yesman.epicfight.registry.EpicFightRegistries";
    private static final String SKILL_DATA_KEY_CLASS =
            "yesman.epicfight.skill.SkillDataKey";
    private static final String YAMATO_DODGE_CLASS =
            "com.hm.efn.skill.dodge.YamatoDodge";

    private static final DeferredRegister<?> DATA_KEYS = createRegistry();
    private static final DeferredHolder<?, ?> YAMATO_JUMP_COUNT =
            registerYamatoJumpCount();

    private NightfallSkillData() {
    }

    public static void register(IEventBus modEventBus) {
        DATA_KEYS.register(modEventBus);
    }

    public static DeferredHolder<?, ?> yamatoJumpCount() {
        return YAMATO_JUMP_COUNT;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static DeferredRegister<?> createRegistry() {
        try {
            Class<?> registries = Class.forName(
                    EPIC_FIGHT_REGISTRIES, false, NightfallSkillData.class.getClassLoader());
            Registry registry = (Registry) registries.getField("SKILL_DATA_KEY").get(null);
            return DeferredRegister.create(registry, "kaisterstweaksandfixesreforged");
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException(
                    "Could not access Epic Fight's skill-data-key registry for Nightfall compatibility",
                    exception);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static DeferredHolder<?, ?> registerYamatoJumpCount() {
        DeferredRegister rawRegister = DATA_KEYS;
        return rawRegister.register("nightfall_yamato_jump_count",
                NightfallSkillData::createYamatoJumpCount);
    }

    private static Object createYamatoJumpCount() {
        try {
            Class<?> dataKeyClass = Class.forName(
                    SKILL_DATA_KEY_CLASS, false, NightfallSkillData.class.getClassLoader());
            Class<?> yamatoDodgeClass = Class.forName(
                    YAMATO_DODGE_CLASS, false, NightfallSkillData.class.getClassLoader());
            Method factory = dataKeyClass.getMethod(
                    "createSkillDataKey",
                    StreamCodec.class,
                    Object.class,
                    boolean.class,
                    Class[].class);

            // Match Epic Fight's original JUMP_COUNT semantics: Integer(0), not synchronized.
            return factory.invoke(null, new Object[]{
                    ByteBufCodecs.INT,
                    Integer.valueOf(0),
                    false,
                    new Class<?>[]{yamatoDodgeClass}
            });
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException(
                    "Could not create the KTFR Yamato Dodge jump-count key",
                    exception);
        }
    }
}
