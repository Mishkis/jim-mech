package github.mishkis.jimmech.entity;

import github.mishkis.jimmech.JimMech;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Map;

public class Mech extends Entity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public Mech(EntityType<?> type, World world) {
        super(type, world);
    }

    private final Map<Vector2i, Float> ROT_MAP = Map.of(
            new Vector2i(1, 0), 0F,
            new Vector2i(1, 1), MathHelper.PI * 1.75F,
            new Vector2i(0, 1), MathHelper.PI * 1.5F,
            new Vector2i(-1, 1),MathHelper.PI *  1.25F,
            new Vector2i(-1, 0), MathHelper.PI,
            new Vector2i(1, -1),MathHelper.PI *  0.25F,
            new Vector2i(0, -1),MathHelper.PI *  0.5F,
            new Vector2i(-1, -1),MathHelper.PI *  0.75F,
            new Vector2i(0, 0), 0F
    );

    // Used for rotating model
    private float pelvis_rotation = 0;

    // Used to bob model up and down, can't be done as keyframes due to needing to also move player on server side
    private float ticks_walking = 0;

    public float getBobOffset(float partial_tick) {
        return 0.25F * MathHelper.abs(MathHelper.sin((ticks_walking + partial_tick) * MathHelper.PI / 20));
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().isEmpty();
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public double getMountedHeightOffset() {
        return 1.8 + getBobOffset(0);
    }


    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient) {
            player.startRiding(this);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        super.move(movementType, movement);
    }

    public boolean shouldMove(PlayerEntity player) {
        return (player.forwardSpeed != 0 || player.sidewaysSpeed != 0);
    }

    @Override
    public void tick() {
        super.tick();

        this.setVelocity(this.getVelocity().multiply(0.4));

        if (this.getControllingPassenger() instanceof PlayerEntity player) {
            if (shouldMove(player)) {
                float rot = player.headYaw * MathHelper.PI / 180F;
                rot += ROT_MAP.get(new Vector2i(MathHelper.sign(player.forwardSpeed), MathHelper.sign(player.sidewaysSpeed)));
                Vec3d rotVec = new Vec3d(MathHelper.sin(-rot), 0., MathHelper.cos(rot));

                this.setVelocity(rotVec.multiply(0.4));

                ticks_walking++;
            } else {
                ticks_walking = 0;
            }

            this.setYaw(MathHelper.lerpAngleDegrees(0.2F, this.getYaw(), player.getHeadYaw()));
            player.setBodyYaw(this.getYaw());
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return (LivingEntity)this.getFirstPassenger();
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "main", (animationState) -> {
            // I should probably refactor this to work like torso rotation, but can't be bothered right now
            if (MathHelper.abs((float) this.getVelocity().x) + MathHelper.abs((float) this.getVelocity().y) >= 0.01) {
                // Converting from radians to degrees and back to radians. Thanks minecraft.
                pelvis_rotation = MathHelper.lerpAngleDegrees(0.1F, pelvis_rotation * MathHelper.DEGREES_PER_RADIAN, ((float) MathHelper.atan2(this.getVelocity().x, this.getVelocity().z)) * MathHelper.DEGREES_PER_RADIAN) * MathHelper.RADIANS_PER_DEGREE;
            }
            animationState.setData(JimMechEntities.MECH_PELVIS_ROTATION_DATA, pelvis_rotation);

            if (this.getControllingPassenger() instanceof PlayerEntity player && shouldMove(player)) {
                return animationState.setAndContinue(DefaultAnimations.WALK);
            }

            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
