package github.mishkis.jimmech.client.entity_renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import java.util.function.BiFunction;

public class MechBlockRenderLayer<T extends GeoAnimatable> extends BlockAndItemGeoLayer<T> {
    private Vec3d offset;

    public MechBlockRenderLayer(GeoRenderer renderer) {
        super(renderer);
    }

    public MechBlockRenderLayer(GeoRenderer<T> renderer, BiFunction<GeoBone, T, BlockState> blockForBone, Vec3d offset) {
        super(renderer, (a, b) -> {return null;}, blockForBone);
        this.offset = offset;
    }

    @Override
    protected void renderBlockForBone(MatrixStack poseStack, GeoBone bone, BlockState state, T animatable, VertexConsumerProvider bufferSource, float partialTick, int packedLight, int packedOverlay) {
        poseStack.push();
        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.scale(0.8f, 0.8f, 0.8f);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, poseStack, bufferSource, packedLight, OverlayTexture.DEFAULT_UV);
        poseStack.pop();
    }
}
