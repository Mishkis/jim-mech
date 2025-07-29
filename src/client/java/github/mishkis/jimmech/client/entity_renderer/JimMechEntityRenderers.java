package github.mishkis.jimmech.client.entity_renderer;

import github.mishkis.jimmech.entity.JimMechEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class JimMechEntityRenderers {
    public static void initialize() {
        EntityRendererRegistry.register(JimMechEntities.MECH, MechRenderer::new);
    }
}
