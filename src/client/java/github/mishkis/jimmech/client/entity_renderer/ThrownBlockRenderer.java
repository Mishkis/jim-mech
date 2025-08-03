package github.mishkis.jimmech.client.entity_renderer;

import github.mishkis.jimmech.JimMech;
import github.mishkis.jimmech.entity.ThrownBlock;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class ThrownBlockRenderer extends GeoEntityRenderer<ThrownBlock> {
    public ThrownBlockRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(Identifier.of(JimMech.MOD_ID, "thrown_block")));

        addRenderLayer(new MechBlockRenderLayer<>(this, (bone, thrownBlock) -> {
            if (bone.getName().equals("Block") && thrownBlock.getBlock() != null) {
                return thrownBlock.getBlock();
            }
            return null;
        }, new Vec3d(0., -0.1, -0.35)));
    }

    @Override
    public void preRender(MatrixStack poseStack, ThrownBlock animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        RenderUtils.faceRotation(poseStack, animatable, partialTick);

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
