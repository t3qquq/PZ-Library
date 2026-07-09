// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.chat.ChatSettings;
import zombie.chat.defaultChats.FactionChat;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.textures.ColorInfo;
import zombie.network.GameClient;
import zombie.network.ServerOptions;
import zombie.network.chat.ChatServer;

public final class Faction {
    private String name;
    private String owner;
    private String tag;
    private ColorInfo tagColor;
    private final ArrayList<String> players = new ArrayList<>();
    public static ArrayList<Faction> factions = new ArrayList<>();

    public Faction() {
    }

    public Faction(String _name, String _owner) {
        this.setName(_name);
        this.setOwner(_owner);
        this.tagColor = new ColorInfo(Rand.Next(0.3F, 1.0F), Rand.Next(0.3F, 1.0F), Rand.Next(0.3F, 1.0F), 1.0F);
    }

    public static Faction createFaction(String _name, String _owner) {
        if (!factionExist(_name)) {
            Faction faction = new Faction(_name, _owner);
            factions.add(faction);
            if (GameClient.bClient) {
                GameClient.sendFaction(faction, false);
            }

            return faction;
        } else {
            return null;
        }
    }

    public static ArrayList<Faction> getFactions() {
        return factions;
    }

    public static boolean canCreateFaction(IsoPlayer player) {
        boolean boolean0 = ServerOptions.instance.Faction.getValue();
        if (boolean0
            && ServerOptions.instance.FactionDaySurvivedToCreate.getValue() > 0
            && player.getHoursSurvived() / 24.0 < ServerOptions.instance.FactionDaySurvivedToCreate.getValue()) {
            boolean0 = false;
        }

        return boolean0;
    }

    public boolean canCreateTag() {
        return this.players.size() + 1 >= ServerOptions.instance.FactionPlayersRequiredForTag.getValue();
    }

    public static boolean isAlreadyInFaction(String username) {
        for (int int0 = 0; int0 < factions.size(); int0++) {
            Faction faction = factions.get(int0);
            if (faction.getOwner().equals(username)) {
                return true;
            }

            for (int int1 = 0; int1 < faction.getPlayers().size(); int1++) {
                if (faction.getPlayers().get(int1).equals(username)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isAlreadyInFaction(IsoPlayer player) {
        return isAlreadyInFaction(player.getUsername());
    }

    public void removePlayer(String player) {
        this.getPlayers().remove(player);
        if (GameClient.bClient) {
            GameClient.sendFaction(this, false);
        }
    }

    public static boolean factionExist(String _name) {
        for (int int0 = 0; int0 < factions.size(); int0++) {
            if (factions.get(int0).getName().equals(_name)) {
                return true;
            }
        }

        return false;
    }

    public static boolean tagExist(String _name) {
        for (int int0 = 0; int0 < factions.size(); int0++) {
            if (factions.get(int0).getTag() != null && factions.get(int0).getTag().equals(_name)) {
                return true;
            }
        }

        return false;
    }

    public static Faction getPlayerFaction(IsoPlayer player) {
        for (int int0 = 0; int0 < factions.size(); int0++) {
            Faction faction = factions.get(int0);
            if (faction.getOwner().equals(player.getUsername())) {
                return faction;
            }

            for (int int1 = 0; int1 < faction.getPlayers().size(); int1++) {
                if (faction.getPlayers().get(int1).equals(player.getUsername())) {
                    return faction;
                }
            }
        }

        return null;
    }

    public static Faction getPlayerFaction(String username) {
        for (int int0 = 0; int0 < factions.size(); int0++) {
            Faction faction = factions.get(int0);
            if (faction.getOwner().equals(username)) {
                return faction;
            }

            for (int int1 = 0; int1 < faction.getPlayers().size(); int1++) {
                if (faction.getPlayers().get(int1).equals(username)) {
                    return faction;
                }
            }
        }

        return null;
    }

    public static Faction getFaction(String _name) {
        for (int int0 = 0; int0 < factions.size(); int0++) {
            if (factions.get(int0).getName().equals(_name)) {
                return factions.get(int0);
            }
        }

        return null;
    }

    public void removeFaction() {
        getFactions().remove(this);
        if (GameClient.bClient) {
            GameClient.sendFaction(this, true);
        }
    }

    public void syncFaction() {
        if (GameClient.bClient) {
            GameClient.sendFaction(this, false);
        }
    }

    public boolean isOwner(String _name) {
        return this.getOwner().equals(_name);
    }

    public boolean isPlayerMember(IsoPlayer player) {
        return this.isMember(player.getUsername());
    }

    public boolean isMember(String _name) {
        for (int int0 = 0; int0 < this.getPlayers().size(); int0++) {
            if (this.getPlayers().get(int0).equals(_name)) {
                return true;
            }
        }

        return false;
    }

    public void writeToBuffer(ByteBufferWriter bb, boolean remove) {
        bb.putUTF(this.getName());
        bb.putUTF(this.getOwner());
        bb.putInt(this.getPlayers().size());
        if (this.getTag() != null) {
            bb.putByte((byte)1);
            bb.putUTF(this.getTag());
            bb.putFloat(this.getTagColor().r);
            bb.putFloat(this.getTagColor().g);
            bb.putFloat(this.getTagColor().b);
        } else {
            bb.putByte((byte)0);
        }

        for (String string : this.getPlayers()) {
            bb.putUTF(string);
        }

        bb.putBoolean(remove);
    }

    public void save(ByteBuffer output) {
        GameWindow.WriteString(output, this.getName());
        GameWindow.WriteString(output, this.getOwner());
        output.putInt(this.getPlayers().size());
        if (this.getTag() != null) {
            output.put((byte)1);
            GameWindow.WriteString(output, this.getTag());
            output.putFloat(this.getTagColor().r);
            output.putFloat(this.getTagColor().g);
            output.putFloat(this.getTagColor().b);
        } else {
            output.put((byte)0);
        }

        for (String string : this.getPlayers()) {
            GameWindow.WriteString(output, string);
        }
    }

    public void load(ByteBuffer input, int WorldVersion) {
        this.setName(GameWindow.ReadString(input));
        this.setOwner(GameWindow.ReadString(input));
        int int0 = input.getInt();
        if (input.get() == 1) {
            this.setTag(GameWindow.ReadString(input));
            this.setTagColor(new ColorInfo(input.getFloat(), input.getFloat(), input.getFloat(), 1.0F));
        } else {
            this.setTagColor(new ColorInfo(Rand.Next(0.3F, 1.0F), Rand.Next(0.3F, 1.0F), Rand.Next(0.3F, 1.0F), 1.0F));
        }

        for (int int1 = 0; int1 < int0; int1++) {
            this.getPlayers().add(GameWindow.ReadString(input));
        }

        if (ChatServer.isInited()) {
            FactionChat factionChat = ChatServer.getInstance().createFactionChat(this.getName());
            ChatSettings chatSettings = FactionChat.getDefaultSettings();
            chatSettings.setFontColor(this.tagColor.r, this.tagColor.g, this.tagColor.b, this.tagColor.a);
            factionChat.setSettings(chatSettings);
        }
    }

    public void addPlayer(String pName) {
        for (int int0 = 0; int0 < factions.size(); int0++) {
            Faction faction = factions.get(int0);
            if (faction.getOwner().equals(pName)) {
                return;
            }

            for (int int1 = 0; int1 < faction.getPlayers().size(); int1++) {
                if (faction.getPlayers().get(int1).equals(pName)) {
                    return;
                }
            }
        }

        this.players.add(pName);
        if (GameClient.bClient) {
            GameClient.sendFaction(this, false);
        }
    }

    public ArrayList<String> getPlayers() {
        return this.players;
    }

    public ColorInfo getTagColor() {
        return this.tagColor;
    }

    public void setTagColor(ColorInfo _tagColor) {
        if (_tagColor.r < 0.19F) {
            _tagColor.r = 0.19F;
        }

        if (_tagColor.g < 0.19F) {
            _tagColor.g = 0.19F;
        }

        if (_tagColor.b < 0.19F) {
            _tagColor.b = 0.19F;
        }

        this.tagColor = _tagColor;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String _tag) {
        this.tag = _tag;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String _owner) {
        if (this.owner == null) {
            this.owner = _owner;
        } else {
            if (!this.isMember(this.owner)) {
                this.getPlayers().add(this.owner);
                this.getPlayers().remove(_owner);
            }

            this.owner = _owner;
        }
    }
}
