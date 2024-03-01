package com.kAIS.KAIMyEntity.neoforge.register;

import com.kAIS.KAIMyEntity.neoforge.network.KAIMyEntityNetworkPack;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class KAIMyEntityRegisterCommon {
    static String networkVersion = "1";
    public static void Register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar("kaimyentity");
        registrar.versioned(networkVersion).optional().play(KAIMyEntityNetworkPack.id, KAIMyEntityNetworkPack::new, handler->handler.client((packet, ctx)->{packet.DoInClient();}).server((packet, ctx)->{packet.DoInServer();}));
    }
}