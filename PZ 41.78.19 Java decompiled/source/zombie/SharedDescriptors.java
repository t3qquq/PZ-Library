// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.util.Type;

public final class SharedDescriptors {
    private static final int DESCRIPTOR_COUNT = 500;
    private static final int DESCRIPTOR_ID_START = 500;
    private static final byte[] DESCRIPTOR_MAGIC = new byte[]{68, 69, 83, 67};
    private static final int VERSION_1 = 1;
    private static final int VERSION_2 = 2;
    private static final int VERSION = 2;
    private static SharedDescriptors.Descriptor[] PlayerZombieDescriptors = new SharedDescriptors.Descriptor[10];
    private static final int FIRST_PLAYER_ZOMBIE_DESCRIPTOR_ID = 1000;

    public static void initSharedDescriptors() {
        if (GameServer.bServer) {
            ;
        }
    }

    private static void noise(String string) {
        DebugLog.log("shared-descriptor: " + string);
    }

    public static void createPlayerZombieDescriptor(IsoZombie zombie0) {
        if (GameServer.bServer) {
            if (zombie0.isReanimatedPlayer()) {
                if (zombie0.getDescriptor().getID() == 0) {
                    int int0 = -1;

                    for (int int1 = 0; int1 < PlayerZombieDescriptors.length; int1++) {
                        if (PlayerZombieDescriptors[int1] == null) {
                            int0 = int1;
                            break;
                        }
                    }

                    if (int0 == -1) {
                        SharedDescriptors.Descriptor[] descriptors = new SharedDescriptors.Descriptor[PlayerZombieDescriptors.length + 10];
                        System.arraycopy(PlayerZombieDescriptors, 0, descriptors, 0, PlayerZombieDescriptors.length);
                        int0 = PlayerZombieDescriptors.length;
                        PlayerZombieDescriptors = descriptors;
                        noise("resized PlayerZombieDescriptors array size=" + PlayerZombieDescriptors.length);
                    }

                    zombie0.getDescriptor().setID(1000 + int0);
                    int int2 = PersistentOutfits.instance.pickOutfit("ReanimatedPlayer", zombie0.isFemale());
                    int2 = int2 & -65536 | int0 + 1;
                    zombie0.setPersistentOutfitID(int2);
                    SharedDescriptors.Descriptor descriptor = new SharedDescriptors.Descriptor();
                    descriptor.bFemale = zombie0.isFemale();
                    descriptor.bZombie = false;
                    descriptor.ID = 1000 + int0;
                    descriptor.persistentOutfitID = int2;
                    descriptor.getHumanVisual().copyFrom(zombie0.getHumanVisual());
                    ItemVisuals itemVisuals = new ItemVisuals();
                    zombie0.getItemVisuals(itemVisuals);

                    for (int int3 = 0; int3 < itemVisuals.size(); int3++) {
                        ItemVisual itemVisual = new ItemVisual(itemVisuals.get(int3));
                        descriptor.itemVisuals.add(itemVisual);
                    }

                    PlayerZombieDescriptors[int0] = descriptor;
                    noise("added id=" + descriptor.getID());

                    for (int int4 = 0; int4 < GameServer.udpEngine.connections.size(); int4++) {
                        UdpConnection udpConnection = GameServer.udpEngine.connections.get(int4);
                        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();

                        try {
                            PacketTypes.PacketType.ZombieDescriptors.doPacket(byteBufferWriter);
                            descriptor.save(byteBufferWriter.bb);
                            PacketTypes.PacketType.ZombieDescriptors.send(udpConnection);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            udpConnection.cancelPacket();
                        }
                    }
                }
            }
        }
    }

    public static void releasePlayerZombieDescriptor(IsoZombie zombie0) {
        if (GameServer.bServer) {
            if (zombie0.isReanimatedPlayer()) {
                int int0 = zombie0.getDescriptor().getID() - 1000;
                if (int0 >= 0 && int0 < PlayerZombieDescriptors.length) {
                    noise("released id=" + zombie0.getDescriptor().getID());
                    zombie0.getDescriptor().setID(0);
                    PlayerZombieDescriptors[int0] = null;
                }
            }
        }
    }

    public static SharedDescriptors.Descriptor[] getPlayerZombieDescriptors() {
        return PlayerZombieDescriptors;
    }

    public static void registerPlayerZombieDescriptor(SharedDescriptors.Descriptor descriptor) {
        if (GameClient.bClient) {
            int int0 = descriptor.getID() - 1000;
            if (int0 >= 0 && int0 < 32767) {
                if (PlayerZombieDescriptors.length <= int0) {
                    int int1 = (int0 + 10) / 10 * 10;
                    SharedDescriptors.Descriptor[] descriptors = new SharedDescriptors.Descriptor[int1];
                    System.arraycopy(PlayerZombieDescriptors, 0, descriptors, 0, PlayerZombieDescriptors.length);
                    PlayerZombieDescriptors = descriptors;
                    noise("resized PlayerZombieDescriptors array size=" + PlayerZombieDescriptors.length);
                }

                PlayerZombieDescriptors[int0] = descriptor;
                noise("registered id=" + descriptor.getID());
            }
        }
    }

    public static void ApplyReanimatedPlayerOutfit(int int0, String var1, IsoGameCharacter character) {
        IsoZombie zombie0 = Type.tryCastTo(character, IsoZombie.class);
        if (zombie0 != null) {
            short short0 = (short)(int0 & 65535);
            if (short0 >= 1 && short0 <= PlayerZombieDescriptors.length) {
                SharedDescriptors.Descriptor descriptor = PlayerZombieDescriptors[short0 - 1];
                if (descriptor != null) {
                    zombie0.useDescriptor(descriptor);
                }
            }
        }
    }

    public static final class Descriptor implements IHumanVisual {
        public int ID = 0;
        public int persistentOutfitID = 0;
        public String outfitName;
        public final HumanVisual humanVisual = new HumanVisual(this);
        public final ItemVisuals itemVisuals = new ItemVisuals();
        public boolean bFemale = false;
        public boolean bZombie = false;

        public int getID() {
            return this.ID;
        }

        public int getPersistentOutfitID() {
            return this.persistentOutfitID;
        }

        @Override
        public HumanVisual getHumanVisual() {
            return this.humanVisual;
        }

        @Override
        public void getItemVisuals(ItemVisuals _itemVisuals) {
            _itemVisuals.clear();
            _itemVisuals.addAll(this.itemVisuals);
        }

        @Override
        public boolean isFemale() {
            return this.bFemale;
        }

        @Override
        public boolean isZombie() {
            return this.bZombie;
        }

        @Override
        public boolean isSkeleton() {
            return false;
        }

        public void save(ByteBuffer output) throws IOException {
            byte byte0 = 0;
            if (this.bFemale) {
                byte0 = (byte)(byte0 | 1);
            }

            if (this.bZombie) {
                byte0 = (byte)(byte0 | 2);
            }

            output.put(byte0);
            output.putInt(this.ID);
            output.putInt(this.persistentOutfitID);
            GameWindow.WriteStringUTF(output, this.outfitName);
            this.humanVisual.save(output);
            this.itemVisuals.save(output);
        }

        public void load(ByteBuffer input, int WorldVersion) throws IOException {
            this.humanVisual.clear();
            this.itemVisuals.clear();
            byte byte0 = input.get();
            this.bFemale = (byte0 & 1) != 0;
            this.bZombie = (byte0 & 2) != 0;
            this.ID = input.getInt();
            this.persistentOutfitID = input.getInt();
            this.outfitName = GameWindow.ReadStringUTF(input);
            this.humanVisual.load(input, WorldVersion);
            short short0 = input.getShort();

            for (int int0 = 0; int0 < short0; int0++) {
                ItemVisual itemVisual = new ItemVisual();
                itemVisual.load(input, WorldVersion);
                this.itemVisuals.add(itemVisual);
            }
        }
    }

    private static final class DescriptorList extends ArrayList<SharedDescriptors.Descriptor> {
    }
}
