package github.mishkis.jimmech.item;

import github.mishkis.jimmech.JimMech;
import github.mishkis.jimmech.entity.JimMechEntities;
import github.mishkis.jimmech.entity.Mech;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Direction;

public class MechItem extends Item {
    public MechItem() {
        super(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Mech mech = new Mech(JimMechEntities.MECH, context.getWorld());
        mech.setPosition(context.getBlockPos().offset(Direction.UP).toCenterPos());
        context.getWorld().spawnEntity(mech);
        context.getStack().decrement(1);

        return ActionResult.SUCCESS;
    }
}
