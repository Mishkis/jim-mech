package github.mishkis.jimmech;

import github.mishkis.jimmech.entity.JimMechEntities;
import github.mishkis.jimmech.item.JimMechItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JimMech implements ModInitializer {
    public static final String MOD_ID = "jim_mech";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Starting jim mech mod :)");
        JimMechEntities.initialize();
        JimMechParticles.initialize();
        JimMechItems.initialize();
    }
}
