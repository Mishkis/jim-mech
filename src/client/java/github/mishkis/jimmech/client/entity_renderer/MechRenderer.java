package github.mishkis.jimmech.client.entity_renderer;

import github.mishkis.jimmech.JimMech;
import github.mishkis.jimmech.entity.Mech;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MechRenderer extends GeoEntityRenderer<Mech> {
    public MechRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new MechGeoModel<>(Identifier.of(JimMech.MOD_ID, "mech")));

        addRenderLayer(new MechBlockRenderLayer<>(this, (bone, mech) -> {
            if (bone.getName().equals("Block") && mech.getHeldBlock() != null) {
                return mech.getHeldBlock();
            }
            return null;
        }, new Vec3d(-0.17f, -0.35f, -0.20f)));
    }
}
