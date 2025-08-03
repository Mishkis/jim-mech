package github.mishkis.jimmech.entity;

import github.mishkis.jimmech.JimMech;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class ThrownBlock extends ProjectileEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private BlockState block;

    public ThrownBlock(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setBlock(BlockState block) {
        this.block = block;
    }

    @Nullable
    public BlockState getBlock() {
        return block;
    }

    @Override
    public void tick() {
        super.tick();

        if (age >= 200) {
            this.discard();
        }

        this.setPosition(this.getPos().add(this.getVelocity()));

        HitResult hitResult = ProjectileUtil.getCollision(this, (hit) -> {
            if (this.getOwner() != null) {
                return !(hit.equals(this.getOwner()) || hit.equals(this.getOwner().getVehicle()));
            }

            return true;
        });
        if (hitResult.getType() != HitResult.Type.MISS) {
            onCollision(hitResult);
        }

        this.setVelocity(this.getVelocity().subtract(0, 0.04, 0));
        this.updateRotation();
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!this.getWorld().isClient() && this.getBlock() != null) {
            this.getWorld().setBlockState(this.getBlockPos(), this.getBlock());
        }

        super.remove(reason);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        BlockPos pos = this.getBlockPos();
        if  (hitResult instanceof BlockHitResult blockHitResult && blockHitResult.isInsideBlock()) {
            pos = pos.offset(blockHitResult.getSide());
        }

        if (this.getWorld().getBlockState(pos).isAir() && getBlock() != null) {
            this.getWorld().setBlockState(pos, getBlock());
        }

        super.onCollision(hitResult);

        this.discard();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (this.getOwner() instanceof PlayerEntity player) {
            entityHitResult.getEntity().damage(this.getDamageSources().playerAttack(player), 20);
        }
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(this.getBlock()));
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.setBlock(Block.getStateFromRawId(packet.getEntityData()));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
