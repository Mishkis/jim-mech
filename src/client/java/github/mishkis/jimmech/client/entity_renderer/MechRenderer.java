package github.mishkis.jimmech.client.entity_renderer;

import github.mishkis.jimmech.JimMech;
import github.mishkis.jimmech.entity.Mech;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MechRenderer extends GeoEntityRenderer<Mech> {
    public MechRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(Identifier.of(JimMech.MOD_ID, "mech")));
    }
}
