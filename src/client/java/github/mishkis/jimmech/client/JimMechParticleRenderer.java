package github.mishkis.jimmech.client;

import github.mishkis.jimmech.JimMechParticles;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.SnowflakeParticle;

public class JimMechParticleRenderer {
    public static void initialize() {
        ParticleFactoryRegistry.getInstance().register(JimMechParticles.FLAME_PARTICLE, SnowflakeParticle.Factory::new);
    }
}
