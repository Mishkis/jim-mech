package github.mishkis.jimmech.client.entity_renderer;

import github.mishkis.jimmech.entity.JimMechEntities;
import github.mishkis.jimmech.entity.Mech;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.core.Core;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MechGeoModel<T extends GeoEntity> extends DefaultedEntityGeoModel<T> {
    public MechGeoModel(Identifier assetSubpath) {
        super(assetSubpath);
    }

    @Override
    public void setCustomAnimations(T anim, long instanceId, AnimationState<T> animationState) {
        float pelvis_rotation = animationState.getData(JimMechEntities.MECH_PELVIS_ROTATION_DATA);
        getAnimationProcessor().getBone("Pelvis").setRotY(pelvis_rotation);

        CoreGeoBone torso = getAnimationProcessor().getBone("Torso");
        Mech animatable = (Mech) anim;
        float torso_rotation = -MathHelper.lerpAngleDegrees(animationState.getPartialTick(), animatable.prevYaw, animatable.getYaw()) * MathHelper.RADIANS_PER_DEGREE;
        torso.setRotY((float) torso_rotation);
    }
}
