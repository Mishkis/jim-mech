package github.mishkis.jimmech.entity;

import github.mishkis.jimmech.JimMech;
import github.mishkis.jimmech.mixin.LivingEntityAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
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
    public static final RawAnimation MACHINE_GUN_SHOOT_ANIMATION = RawAnimation.begin().thenPlay("attack.start_machine_gun").thenLoop("attack.shoot_machine_gun");
    private static final RawAnimation MACHINE_GUN_STOP_SHOOT_ANIMATION = RawAnimation.begin().thenPlay("attack.stop_machine_gun_hands").thenPlay("attack.stop_machine_gun");
    public static final RawAnimation GRAB_ANIMATION = RawAnimation.begin().thenPlay("block.grab").thenLoop("block.hold");
    private static final RawAnimation CHARGE_ANIMATION = RawAnimation.begin().thenPlay("block.start_charge").thenLoop("block.charge");
    private static final RawAnimation THROW_ANIMATION = RawAnimation.begin().thenPlay("block.throw");
    public static final RawAnimation LAND_ANIMATION = RawAnimation.begin().thenPlay("move.land");
    // Public as it is called in mech geo model to offset
    public static final RawAnimation STAND_ANIMATION = RawAnimation.begin().thenPlay("misc.stand");

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
    private float start_ride_tick = 0;

    private float start_shoot_tick = 0;
    private float shoot_ticks = 0;

    private float start_charge_tick = 0;
    private float charge_ticks = Float.POSITIVE_INFINITY;

    private boolean flying = false;
    private float grounded_ticks = 0;

    private BlockState held_block;

    public BlockState getHeldBlock() {
        return held_block;
    }

    public float getBobOffset(float partial_tick) {
        RawAnimation current = getAnimatableInstanceCache().getManagerForId(getId()).getAnimationControllers().get("main").getCurrentRawAnimation();

        if (current == DefaultAnimations.WALK) {
            return 0.25F * MathHelper.abs(MathHelper.sin((ticks_walking + partial_tick) * MathHelper.PI / 20));
        } else if (current == STAND_ANIMATION) {
            return (float)(-Math.pow(2-(age - start_ride_tick + partial_tick)/20F, 2F) * 3)/16F;
        } else if (current == LAND_ANIMATION) {
            return 5*(MathHelper.cos((float) ((grounded_ticks + partial_tick) * 2 * MathHelper.PI/25))-1)/16;
        }

        return 0;
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

    @Nullable
    @Override
    public Entity getVehicle() {
        return super.getVehicle();
    }

    @Override
    public void onDamaged(DamageSource damageSource) {
        if (this.getControllingPassenger() instanceof PlayerEntity player && player.equals(damageSource.getAttacker()) && this.charge_ticks == Float.POSITIVE_INFINITY && this.shoot_ticks == 0) {
            if (player.raycast(8, 0, false) instanceof BlockHitResult blockHitResult) {
                BlockState block = player.getWorld().getBlockState(blockHitResult.getBlockPos());
                if (block.isAir()) {
                    return;
                }

                if (held_block == null) {
                    held_block = block;
                    if (!this.getWorld().isClient()) {
                        player.getWorld().breakBlock(blockHitResult.getBlockPos(), false);
                    }
                } else {
                    if (!this.getWorld().isClient()) {
                        player.getWorld().setBlockState(blockHitResult.getBlockPos().offset(blockHitResult.getSide()), held_block);
                    }
                    held_block = null;
                }
            }
        } else {
            super.onDamaged(damageSource);
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.getControllingPassenger() == player) {
            if (held_block == null) {
                if (shoot_ticks == 0) {
                    start_shoot_tick = age;
                    shoot_ticks = 4;

                    return ActionResult.SUCCESS;
                }
                shoot_ticks++;
            } else {
                if (charge_ticks == Float.POSITIVE_INFINITY) {
                    start_charge_tick = age;
                    charge_ticks = 1;

                    return ActionResult.SUCCESS;
                }

                charge_ticks++;
            }

            return ActionResult.CONSUME;
        }

        if (player.startRiding(this)) {
            start_ride_tick = age;

            if (!this.getWorld().isClient) {
                triggerAnim("main", "stand");
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

    public boolean shouldMove(PlayerEntity player) {
        return (player.forwardSpeed != 0 || player.sidewaysSpeed != 0);
    }

    @Override
    public void tick() {
        super.tick();

        this.setVelocity(this.getVelocity().multiply(0.4, 1, 0.4));

        grounded_ticks++;
        if (!this.groundCollision) {
            this.setVelocity(this.getVelocity().subtract(0,0.1,0));
        } else {
            this.setVelocity(this.getVelocity().multiply(1, 0, 1));
        }

        if (this.getControllingPassenger() instanceof PlayerEntity player) {
            if (age - start_ride_tick >= 40 && grounded_ticks > 25) {
                if (shouldMove(player)) {
                    float rot = player.headYaw * MathHelper.PI / 180F;
                    rot += ROT_MAP.get(new Vector2i(MathHelper.sign(player.forwardSpeed), MathHelper.sign(player.sidewaysSpeed)));
                    Vec3d rotVec = new Vec3d(MathHelper.sin(-rot), 0., MathHelper.cos(rot));

                    this.setVelocity(rotVec.multiply(0.4).add(0, this.getVelocity().y, 0));

                    ticks_walking++;
                } else {
                    ticks_walking = 0;
                }

                if (((LivingEntityAccessor) player).getJumping()) {
                    flying = true;
                    this.setVelocity(this.getVelocity().multiply(0.6, 0, 0.6).add(0, 0.2, 0));
                }

                this.setYaw(MathHelper.lerpAngleDegrees(0.2F, this.getYaw(), player.getHeadYaw()));
            } else {
                player.setYaw(this.getYaw());
            }

            player.setBodyYaw(this.getYaw());

            float real_shoot_ticks = age - start_shoot_tick;
            if (real_shoot_ticks <= shoot_ticks * 4) {
                if(real_shoot_ticks >= 80 || MathHelper.sin(real_shoot_ticks * real_shoot_ticks * 3) >= 0.95) {
                    // Shooting
                    Vec3d hit = player.raycast(20, 0, false).getPos();

                    float dist = 2;
                    float radian_yaw = -this.getYaw() * MathHelper.RADIANS_PER_DEGREE;
                    Vec3d start_pos = new Vec3d(-MathHelper.cos(radian_yaw), 0., MathHelper.sin(radian_yaw)).multiply(dist).add(this.getPos()).add(0., 3.2, 0);
                    Vec3d rot_vec = hit.subtract(start_pos).normalize();

                    Bullet bullet = new Bullet(JimMechEntities.BULLET, this.getWorld());
                    bullet.setPosition(start_pos.x + (random.nextFloat() - 0.5) / 1.2, start_pos.y + (random.nextFloat() - 0.5) / 1.2, start_pos.z + (random.nextFloat() - 0.5) / 1.2);
                    bullet.setVelocity(rot_vec.multiply(1.5).multiply(0.5 + random.nextFloat()));
                    bullet.setOwner(player);
                    ProjectileUtil.setRotationFromVelocity(bullet, 1);

                    this.getWorld().spawnEntity(bullet);
                }
            } else {
                shoot_ticks = 0;
            }


            float real_charge_ticks = age - start_charge_tick;
            if (real_charge_ticks > charge_ticks * 4) {
                charge_ticks = Float.POSITIVE_INFINITY;
                if (!this.getWorld().isClient()) {
                    triggerAnim("hands", "throw");
                }

                Vec3d hit = player.raycast(20, 0, false).getPos();

                float dist = 2;
                float radian_yaw = -this.getYaw() * MathHelper.RADIANS_PER_DEGREE;
                Vec3d rot_vec = new Vec3d(MathHelper.cos(radian_yaw), 0., -MathHelper.sin(radian_yaw)).multiply(dist).add(this.getPos()).add(0., 5.2, 0);

                ThrownBlock block = new ThrownBlock(JimMechEntities.THROWN_BLOCK, this.getWorld());
                block.setPosition(rot_vec);

                rot_vec = hit.subtract(rot_vec).normalize().multiply(Math.min(2, real_charge_ticks/20));
                block.setOwner(player);
                block.setBlock(this.getHeldBlock());

                block.setVelocity(rot_vec);

                this.getWorld().spawnEntity(block);

                this.held_block = null;
            }
        }

        this.move(MovementType.SELF, this.getVelocity());
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        if (fallDistance >= 3) {
            if (!this.getWorld().isClient() && fallDistance >= 1) {
                triggerAnim("main", "land");
            }
            grounded_ticks = 0;
        }

        flying = false;
        return false;
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

            if (!this.hasPassengers()) {
                return animationState.setAndContinue(DefaultAnimations.SIT);
            }

            animationState.setData(JimMechEntities.MECH_PELVIS_ROTATION_DATA, pelvis_rotation);

            if (flying) {
                return animationState.setAndContinue(DefaultAnimations.FLY);
            }

            if (this.getControllingPassenger() instanceof PlayerEntity player && shouldMove(player)) {
                return animationState.setAndContinue(DefaultAnimations.WALK);
            }

            return animationState.setAndContinue(DefaultAnimations.IDLE);
        }).triggerableAnim("stand", STAND_ANIMATION).triggerableAnim("land", LAND_ANIMATION));

        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "hands", (animationState) -> {
            if (charge_ticks != Float.POSITIVE_INFINITY) {
                return animationState.setAndContinue(CHARGE_ANIMATION);
            }

            if (held_block != null) {
                return animationState.setAndContinue(GRAB_ANIMATION);
            }

            if (shoot_ticks != 0) {
                return animationState.setAndContinue(MACHINE_GUN_SHOOT_ANIMATION);
            }

            return animationState.setAndContinue(MACHINE_GUN_STOP_SHOOT_ANIMATION);
        }).triggerableAnim("throw", THROW_ANIMATION));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
