package github.mishkis.jimmech.client.entity_renderer;

import github.mishkis.jimmech.JimMech;
import github.mishkis.jimmech.JimMechParticles;
import github.mishkis.jimmech.entity.Mech;
import github.mishkis.jimmech.mixin.LivingEntityAccessor;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
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

    @Override
    public void renderFinal(MatrixStack poseStack, Mech animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (animatable.getControllingPassenger() instanceof PlayerEntity player && ((LivingEntityAccessor) player).getJumping()) {
            model.getBone("Thruster1").ifPresent(thruster -> {
                Random rand = player.getRandom();
                Vector3d thrusterPos = thruster.getWorldPosition();

                player.getEntityWorld().addParticle(JimMechParticles.FLAME_PARTICLE, thrusterPos.x + rand.nextFloat() - 0.5, thrusterPos.y + rand.nextFloat() - 0.5, thrusterPos.z + rand.nextFloat() - 0.5, rand.nextFloat() - 0.5, -rand.nextFloat() * 5, rand.nextFloat() - 0.5);
            });
            model.getBone("Thruster2").ifPresent(thruster -> {
                Random rand = player.getRandom();
                Vector3d thrusterPos = thruster.getWorldPosition();

                player.getEntityWorld().addParticle(JimMechParticles.FLAME_PARTICLE, thrusterPos.x + rand.nextFloat() - 0.5, thrusterPos.y + rand.nextFloat() - 0.5, thrusterPos.z + rand.nextFloat() - 0.5, rand.nextFloat() - 0.5, -rand.nextFloat() * 5, rand.nextFloat() - 0.5);
            });
        }

        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
