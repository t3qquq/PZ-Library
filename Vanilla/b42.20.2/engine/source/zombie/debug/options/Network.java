// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class Network extends OptionGroup {
    public final Network.Client client = new Network.Client(this.group);
    public final Network.Server server = new Network.Server(this.group);
    public final Network.PublicServerUtil publicServerUtil = new Network.PublicServerUtil(this.group);

    public final class Client extends OptionGroup {
        public final BooleanDebugOption mainLoop = this.newDebugOnlyOption("MainLoop", true);
        public final BooleanDebugOption updateZombiesFromPacket = this.newDebugOnlyOption("UpdateZombiesFromPacket", true);
        public final BooleanDebugOption syncIsoObject = this.newDebugOnlyOption("SyncIsoObject", true);

        public Client(final IDebugOptionGroup parent) {
            super(parent, "Client");
        }
    }

    public final class PublicServerUtil extends OptionGroup {
        public final BooleanDebugOption enabled = this.newDebugOnlyOption("Enabled", true);

        public PublicServerUtil(final IDebugOptionGroup parent) {
            super(parent, "PublicServerUtil");
        }
    }

    public final class Server extends OptionGroup {
        public final BooleanDebugOption syncIsoObject = this.newDebugOnlyOption("SyncIsoObject", true);

        public Server(final IDebugOptionGroup parent) {
            super(parent, "Server");
        }
    }
}
