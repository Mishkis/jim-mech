package github.mishkis.jimmech.client.entity_renderer;

import github.mishkis.jimmech.entity.JimMechEntities;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MechGeoModel<T extends GeoEntity> extends DefaultedEntityGeoModel<T> {
    public MechGeoModel(Identifier assetSubpath) {
        super(assetSubpath);
    }

    @Override
    public void setCustomAnimations(T anim, long instanceId, AnimationState<T> animationState) {
        float rotation = animationState.getData(JimMechEntities.MECH_ROTATION_DATA);
        getAnimationProcessor().getBone("Pelvis").setRotY(rotation);
    }
}
