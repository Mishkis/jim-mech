package github.mishkis.jimmech.client.entity_renderer;

import github.mishkis.jimmech.JimMech;
import github.mishkis.jimmech.entity.JimMechEntities;
import github.mishkis.jimmech.entity.Mech;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MechGeoModel<T extends GeoEntity> extends DefaultedEntityGeoModel<T> {
    public MechGeoModel(Identifier assetSubpath) {
        super(assetSubpath);
    }

    @Override
    public void setCustomAnimations(T anim, long instanceId, AnimationState<T> animationState) {
        CoreGeoBone torso = getAnimationProcessor().getBone("Torso");
        Mech animatable = (Mech) anim;
        float torso_rotation = -MathHelper.lerpAngleDegrees(animationState.getPartialTick(), animatable.prevYaw, animatable.getYaw()) * MathHelper.RADIANS_PER_DEGREE;
        torso.setRotY((float) torso_rotation);

        CoreGeoBone pelvis = getAnimationProcessor().getBone("Pelvis");
        Float pelvis_rotation = animationState.getData(JimMechEntities.MECH_PELVIS_ROTATION_DATA);

        if (pelvis_rotation != null) {
            pelvis.setRotY(pelvis_rotation);
        } else {
            pelvis.setRotY(torso_rotation);
        }

        RawAnimation current_animation = animatable.getAnimatableInstanceCache().getManagerForId(instanceId).getAnimationControllers().get("main").getCurrentRawAnimation();
        CoreGeoBone mech = getAnimationProcessor().getBone("Mech");
        if (current_animation == DefaultAnimations.WALK || current_animation == Mech.STAND_ANIMATION) {
            // Multiplied by 16 to convert from block to local space
            mech.setPosY(16 * animatable.getBobOffset(-(1 - animationState.getPartialTick())));
        } else if (!(current_animation == DefaultAnimations.SIT)) {
            mech.setPosY(0);
        }

        if (animationState.isCurrentAnimation(Mech.MACHINE_GUN_SHOOT_ANIMATION) && animatable.getControllingPassenger() instanceof PlayerEntity player) {
            CoreGeoBone right_arm = getAnimationProcessor().getBone("RightArm");
            float right_arm_rot = -(player.getPitch() - 90) * MathHelper.RADIANS_PER_DEGREE;
            right_arm.setRotX(right_arm_rot);

            Vec3d hit = player.raycast(20, animationState.getPartialTick(), false).getPos();

            CoreGeoBone machine_gun = getAnimationProcessor().getBone("Forearm2");

            float dist = 2;
            Vec3d rot_vec = new Vec3d(-MathHelper.cos(torso_rotation), 0., MathHelper.sin(torso_rotation)).multiply(dist).add(animatable.getPos());
            rot_vec = hit.subtract(rot_vec);

            hit = hit.multiply(1., 0., 1.);
            rot_vec = rot_vec.multiply(1., 0., 1.);
            Vec3d mech_pos = animatable.getPos().multiply(1., 0., 1.);

            // Law of cosines
            machine_gun.setRotZ((float) -(MathHelper.PI/2-Math.acos((mech_pos.squaredDistanceTo(hit) - dist * dist - rot_vec.lengthSquared())/(-2 * dist * dist * rot_vec.length()))));

            machine_gun.setPosX(MathHelper.cos(torso_rotation));
            machine_gun.setPosZ(MathHelper.sin(torso_rotation));
        }
    }
}
