package github.mishkis.jimmech;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class JimMechParticles {
    public static final DefaultParticleType FLAME_PARTICLE = FabricParticleTypes.simple();

    public static void initialize() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(JimMech.MOD_ID, "flame_particle"), FLAME_PARTICLE);
    }
}
