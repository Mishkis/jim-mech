package github.mishkis.jimmech.item;

import github.mishkis.jimmech.JimMech;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class JimMechItems {
    public static final MechItem MECH = (MechItem) register(new MechItem(), "mech");

    public static Item register(Item item, String id) {
        Identifier itemID = new Identifier(JimMech.MOD_ID, id);
        return  Registry.register(Registries.ITEM, itemID, item);
    }

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(fabricItemGroupEntries -> fabricItemGroupEntries.addAfter(Items.SHIELD, MECH) );
    }
}
