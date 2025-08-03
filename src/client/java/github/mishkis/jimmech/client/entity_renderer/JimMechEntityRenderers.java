package github.mishkis.jimmech.client.entity_renderer;

import github.mishkis.jimmech.entity.JimMechEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class JimMechEntityRenderers {
    public static void initialize() {
        EntityRendererRegistry.register(JimMechEntities.MECH, MechRenderer::new);
        EntityRendererRegistry.register(JimMechEntities.BULLET, BulletRenderer::new);
        EntityRendererRegistry.register(JimMechEntities.THROWN_BLOCK, ThrownBlockRenderer::new);
    }
}
