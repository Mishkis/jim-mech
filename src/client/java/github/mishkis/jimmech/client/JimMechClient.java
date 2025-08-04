package github.mishkis.jimmech.client;

import github.mishkis.jimmech.client.entity_renderer.JimMechEntityRenderers;
import github.mishkis.jimmech.entity.JimMechEntities;
import github.mishkis.jimmech.entity.Mech;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;

public class JimMechClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(JimMechEntities.MECH_PACKET_ID, ((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
            Entity entity = clientPlayNetworkHandler.getWorld().getEntityById(packetByteBuf.readInt());
            int age = packetByteBuf.readInt();

            float start_shoot_tick = packetByteBuf.readFloat();
            float shoot_ticks = packetByteBuf.readFloat();

            float start_charge_tick = packetByteBuf.readFloat();
            float charge_ticks = packetByteBuf.readFloat();
            BlockState held_block = Block.getStateFromRawId(packetByteBuf.readInt());

            float start_ride_tick = packetByteBuf.readFloat();

            boolean flying = packetByteBuf.readBoolean();
            float grounded_ticks = packetByteBuf.readFloat();

            minecraftClient.execute(() -> {
                if (entity instanceof Mech mech) {
                    mech.updateMechClient(age, start_shoot_tick, shoot_ticks, start_charge_tick, charge_ticks, held_block, start_ride_tick, flying, grounded_ticks);
                }
            });
        }));

        JimMechEntityRenderers.initialize();
        JimMechParticleRenderer.initialize();
    }
}
