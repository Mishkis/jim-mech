package github.mishkis.jimmech.mixin;

import net.fabricmc.fabric.mixin.event.lifecycle.LivingEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public abstract interface LivingEntityAccessor {
    @Accessor
    boolean getJumping();
}
