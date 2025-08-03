package github.mishkis.jimmech.mixin;

import com.mojang.authlib.GameProfile;
import github.mishkis.jimmech.JimMech;
import github.mishkis.jimmech.entity.Mech;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "attack(Lnet/minecraft/entity/Entity;)V")
    public void attack(Entity target, CallbackInfo info) {
        if (this.getVehicle() instanceof Mech mech) {
            mech.onDamaged(this.getDamageSources().playerAttack((PlayerEntity)(Object)this));
        }
    }
}
