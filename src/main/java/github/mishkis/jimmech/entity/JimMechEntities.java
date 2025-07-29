package github.mishkis.jimmech.entity;

import github.mishkis.jimmech.JimMech;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.network.SerializableDataTicket;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JimMechEntities {
    public static final EntityType<Mech> MECH =
            register(
                    "mech",
                    EntityType.Builder.create(Mech::new, SpawnGroup.MISC)
                            .setDimensions(2F, 4F)
            );

    public static final SerializableDataTicket<Float> MECH_PELVIS_ROTATION_DATA = GeckoLibUtil.addDataTicket(SerializableDataTicket.ofFloat(Identifier.of(JimMech.MOD_ID, "mech_pelvis_rotation_data")));

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, Identifier.of(JimMech.MOD_ID, id), type.build(id));
    }

    public static void initialize() {}
}
