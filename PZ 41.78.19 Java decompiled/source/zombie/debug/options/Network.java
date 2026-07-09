// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug.options;

import zombie.debug.BooleanDebugOption;

public final class Network extends OptionGroup {
    public final Network.Client Client = new Network.Client(this.Group);
    public final Network.Server Server = new Network.Server(this.Group);
    public final Network.PublicServerUtil PublicServerUtil = new Network.PublicServerUtil(this.Group);

    public Network() {
        super("Network");
    }

    public final class Client extends OptionGroup {
        public final BooleanDebugOption MainLoop = newDebugOnlyOption(this.Group, "MainLoop", true);
        public final BooleanDebugOption UpdateZombiesFromPacket = newDebugOnlyOption(this.Group, "UpdateZombiesFromPacket", true);
        public final BooleanDebugOption SyncIsoObject = newDebugOnlyOption(this.Group, "SyncIsoObject", true);

        public Client(IDebugOptionGroup iDebugOptionGroup) {
            super(iDebugOptionGroup, "Client");
        }
    }

    public final class PublicServerUtil extends OptionGroup {
        public final BooleanDebugOption Enabled = newDebugOnlyOption(this.Group, "Enabled", true);

        public PublicServerUtil(IDebugOptionGroup iDebugOptionGroup) {
            super(iDebugOptionGroup, "PublicServerUtil");
        }
    }

    public final class Server extends OptionGroup {
        public final BooleanDebugOption SyncIsoObject = newDebugOnlyOption(this.Group, "SyncIsoObject", true);

        public Server(IDebugOptionGroup iDebugOptionGroup) {
            super(iDebugOptionGroup, "Server");
        }
    }
}
