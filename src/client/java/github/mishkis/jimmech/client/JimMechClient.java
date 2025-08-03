package github.mishkis.jimmech.client;

import github.mishkis.jimmech.client.entity_renderer.JimMechEntityRenderers;
import net.fabricmc.api.ClientModInitializer;

public class JimMechClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        JimMechEntityRenderers.initialize();
        JimMechParticleRenderer.initialize();
    }
}
