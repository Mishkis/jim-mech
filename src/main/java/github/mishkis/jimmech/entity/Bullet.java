package github.mishkis.jimmech.entity;

import github.mishkis.jimmech.JimMech;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Bullet extends ProjectileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation animation = DefaultAnimations.SPAWN.thenLoop("misc.idle");

    private float hit_ticks = 0;
    private float scale = 1;

    public Bullet(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public float getScale() {
        return scale;
    }

    // Weird things were happening with it disappearing while rendering, so this is just a quick fix
    // It shouldn't matter much since they despawn after 200 ticks anyways
    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public void tick() {
        super.tick();

        if (age >= 200) {
            this.discard();
        }

        if (hit_ticks == 0) {
            this.setPosition(this.getPos().add(this.getVelocity()));

            HitResult hitResult = ProjectileUtil.getCollision(this, (hit) -> {
                if (this.getOwner() != null) {
                    return !(hit.equals(this.getOwner()) || hit.equals(this.getOwner().getVehicle()) || hit instanceof Bullet);
                }

                return true;
            });
            if (hitResult.getType() != HitResult.Type.MISS) {
                onCollision(hitResult);
            }
        } else {
            hit_ticks += 1;

            if (hit_ticks >= 65) {
                this.discard();
            }
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hit_ticks == 0) {
            hit_ticks = 1;
        }
        super.onCollision(hitResult);
    }

    @Override
    public boolean shouldRender(double distance) {
        return super.shouldRender(distance);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        entityHitResult.getEntity().damage(this.getDamageSources().playerAttack((PlayerEntity) this.getOwner()), 5);

        this.discard();
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);

        scale = random.nextFloat();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, "main", (state) -> {
            if (this.hit_ticks == 0) {
                return state.setAndContinue(animation);
            }

            return state.setAndContinue(DefaultAnimations.DIE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
