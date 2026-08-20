package platform;

import aethereal.api.Compile;
import aethereal.core.HydrogenClient;
import aethereal.core.NativeMethodLookup;
import hydrogen.Hydrogen;
import hydrogen.integration.ClientModuleRepository;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

/** Composition root: the gameplay module core is adapted into the Hydrogen application. */
public class Initializer implements ClientModInitializer {
    @Override
    @Compile
    public void onInitializeClient() {
        HydrogenClient clientCore = new HydrogenClient();
        Hydrogen hydrogen = Hydrogen.get();
        hydrogen.init(new ClientModuleRepository(clientCore));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> hydrogen.close());
    }

    static {
        NativeMethodLookup.lookup(Initializer.class, 1);
    }
}
