// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Translator;
import zombie.debug.DebugLog;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.network.chat.ChatServer;
import zombie.network.packets.SyncSafehousePacket;

public class SafeHouse {
    private int x = 0;
    private int y = 0;
    private int w = 0;
    private int h = 0;
    private static int diffError = 2;
    private String owner = null;
    private ArrayList<String> players = new ArrayList<>();
    private long lastVisited = 0L;
    private String title = "Safehouse";
    private int playerConnected = 0;
    private int openTimer = 0;
    private final String id;
    public ArrayList<String> playersRespawn = new ArrayList<>();
    private static final ArrayList<SafeHouse> safehouseList = new ArrayList<>();
    private static final ArrayList<IsoPlayer> tempPlayers = new ArrayList<>();

    public static void init() {
        safehouseList.clear();
    }

    public static SafeHouse addSafeHouse(int _x, int _y, int _w, int _h, String player, boolean remote) {
        SafeHouse safeHouse = new SafeHouse(_x, _y, _w, _h, player);
        safeHouse.setOwner(player);
        safeHouse.setLastVisited(Calendar.getInstance().getTimeInMillis());
        safeHouse.addPlayer(player);
        safehouseList.add(safeHouse);
        if (GameServer.bServer) {
            DebugLog.log("safehouse: added " + _x + "," + _y + "," + _w + "," + _h + " owner=" + player);
        }

        if (GameClient.bClient && !remote) {
            GameClient.sendSafehouse(safeHouse, false);
        }

        updateSafehousePlayersConnected();
        if (GameClient.bClient) {
            LuaEventManager.triggerEvent("OnSafehousesChanged");
        }

        return safeHouse;
    }

    public static SafeHouse addSafeHouse(IsoGridSquare square, IsoPlayer player) {
        String string = canBeSafehouse(square, player);
        return string != null && !"".equals(string)
            ? null
            : addSafeHouse(
                square.getBuilding().def.getX() - diffError,
                square.getBuilding().def.getY() - diffError,
                square.getBuilding().def.getW() + diffError * 2,
                square.getBuilding().def.getH() + diffError * 2,
                player.getUsername(),
                false
            );
    }

    public static SafeHouse hasSafehouse(String username) {
        for (int int0 = 0; int0 < safehouseList.size(); int0++) {
            SafeHouse safeHouse = safehouseList.get(int0);
            if (safeHouse.getPlayers().contains(username) || safeHouse.getOwner().equals(username)) {
                return safeHouse;
            }
        }

        return null;
    }

    public static SafeHouse hasSafehouse(IsoPlayer player) {
        return hasSafehouse(player.getUsername());
    }

    public static void updateSafehousePlayersConnected() {
        SafeHouse safeHouse = null;

        for (int int0 = 0; int0 < safehouseList.size(); int0++) {
            safeHouse = safehouseList.get(int0);
            safeHouse.setPlayerConnected(0);
            if (GameClient.bClient) {
                for (IsoPlayer player0 : GameClient.IDToPlayerMap.values()) {
                    if (safeHouse.getPlayers().contains(player0.getUsername()) || safeHouse.getOwner().equals(player0.getUsername())) {
                        safeHouse.setPlayerConnected(safeHouse.getPlayerConnected() + 1);
                    }
                }
            } else if (GameServer.bServer) {
                for (IsoPlayer player1 : GameServer.IDToPlayerMap.values()) {
                    if (safeHouse.getPlayers().contains(player1.getUsername()) || safeHouse.getOwner().equals(player1.getUsername())) {
                        safeHouse.setPlayerConnected(safeHouse.getPlayerConnected() + 1);
                    }
                }
            }
        }
    }

    public void updatePlayersConnected() {
        this.setPlayerConnected(0);
        if (GameClient.bClient) {
            for (IsoPlayer player0 : GameClient.IDToPlayerMap.values()) {
                if (this.getPlayers().contains(player0.getUsername()) || this.getOwner().equals(player0.getUsername())) {
                    this.setPlayerConnected(this.getPlayerConnected() + 1);
                }
            }
        } else if (GameServer.bServer) {
            for (IsoPlayer player1 : GameServer.IDToPlayerMap.values()) {
                if (this.getPlayers().contains(player1.getUsername()) || this.getOwner().equals(player1.getUsername())) {
                    this.setPlayerConnected(this.getPlayerConnected() + 1);
                }
            }
        }
    }

    public static SafeHouse getSafeHouse(IsoGridSquare square) {
        return isSafeHouse(square, null, false);
    }

    public static SafeHouse getSafeHouse(int _x, int _y, int _w, int _h) {
        SafeHouse safeHouse = null;

        for (int int0 = 0; int0 < safehouseList.size(); int0++) {
            safeHouse = safehouseList.get(int0);
            if (_x == safeHouse.getX() && _w == safeHouse.getW() && _y == safeHouse.getY() && _h == safeHouse.getH()) {
                return safeHouse;
            }
        }

        return null;
    }

    /**
     * Return if the square is a safehouse non allowed for the player You need to be  on a safehouse AND not be allowed to return the safe If you're allowed,  you'll have null in return If username is null, you basically just return if  there's a safehouse here
     */
    public static SafeHouse isSafeHouse(IsoGridSquare square, String username, boolean doDisableSafehouse) {
        if (square == null) {
            return null;
        } else {
            if (GameClient.bClient) {
                IsoPlayer player = GameClient.instance.getPlayerFromUsername(username);
                if (player != null && !player.accessLevel.equals("")) {
                    return null;
                }
            }

            SafeHouse safeHouse = null;
            boolean boolean0 = false;

            for (int int0 = 0; int0 < safehouseList.size(); int0++) {
                safeHouse = safehouseList.get(int0);
                if (square.getX() >= safeHouse.getX()
                    && square.getX() < safeHouse.getX2()
                    && square.getY() >= safeHouse.getY()
                    && square.getY() < safeHouse.getY2()) {
                    boolean0 = true;
                    break;
                }
            }

            if (!boolean0
                || !doDisableSafehouse
                || !ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue()
                || safeHouse.getPlayerConnected() <= 0 && safeHouse.getOpenTimer() <= 0) {
                return !boolean0
                        || (username == null || safeHouse == null || safeHouse.getPlayers().contains(username) || safeHouse.getOwner().equals(username))
                            && username != null
                    ? null
                    : safeHouse;
            } else {
                return null;
            }
        }
    }

    public static void clearSafehouseList() {
        safehouseList.clear();
    }

    public boolean playerAllowed(IsoPlayer player) {
        return this.players.contains(player.getUsername()) || this.owner.equals(player.getUsername()) || !player.accessLevel.equals("");
    }

    public boolean playerAllowed(String name) {
        return this.players.contains(name) || this.owner.equals(name);
    }

    public void addPlayer(String player) {
        if (!this.players.contains(player)) {
            this.players.add(player);
            updateSafehousePlayersConnected();
        }
    }

    public void removePlayer(String player) {
        if (this.players.contains(player)) {
            this.players.remove(player);
            this.playersRespawn.remove(player);
            if (GameClient.bClient) {
                GameClient.sendSafehouse(this, false);
            }
        }
    }

    public void syncSafehouse() {
        if (GameClient.bClient) {
            GameClient.sendSafehouse(this, false);
        }
    }

    public void removeSafeHouse(IsoPlayer player) {
        this.removeSafeHouse(player, false);
    }

    public void removeSafeHouse(IsoPlayer player, boolean force) {
        if (player == null
            || player.getUsername().equals(this.getOwner())
            || !player.accessLevel.equals("admin") && !player.accessLevel.equals("moderator")
            || force) {
            if (GameClient.bClient) {
                GameClient.sendSafehouse(this, true);
            }

            if (GameServer.bServer) {
                SyncSafehousePacket syncSafehousePacket = new SyncSafehousePacket();
                syncSafehousePacket.set(this, true);
                GameServer.sendSafehouse(syncSafehousePacket, null);
            }

            getSafehouseList().remove(this);
            DebugLog.log("safehouse: removed " + this.x + "," + this.y + "," + this.w + "," + this.h + " owner=" + this.getOwner());
            if (GameClient.bClient) {
                LuaEventManager.triggerEvent("OnSafehousesChanged");
            }
        }
    }

    public void save(ByteBuffer output) {
        output.putInt(this.getX());
        output.putInt(this.getY());
        output.putInt(this.getW());
        output.putInt(this.getH());
        GameWindow.WriteString(output, this.getOwner());
        output.putInt(this.getPlayers().size());

        for (String string : this.getPlayers()) {
            GameWindow.WriteString(output, string);
        }

        output.putLong(this.getLastVisited());
        GameWindow.WriteString(output, this.getTitle());
        output.putInt(this.playersRespawn.size());

        for (int int0 = 0; int0 < this.playersRespawn.size(); int0++) {
            GameWindow.WriteString(output, this.playersRespawn.get(int0));
        }
    }

    public static SafeHouse load(ByteBuffer bb, int WorldVersion) {
        SafeHouse safeHouse = new SafeHouse(bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt(), GameWindow.ReadString(bb));
        int int0 = bb.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            safeHouse.addPlayer(GameWindow.ReadString(bb));
        }

        safeHouse.setLastVisited(bb.getLong());
        if (WorldVersion >= 101) {
            safeHouse.setTitle(GameWindow.ReadString(bb));
        }

        if (ChatServer.isInited()) {
            ChatServer.getInstance().createSafehouseChat(safeHouse.getId());
        }

        safehouseList.add(safeHouse);
        if (WorldVersion >= 177) {
            int int2 = bb.getInt();

            for (int int3 = 0; int3 < int2; int3++) {
                safeHouse.playersRespawn.add(GameWindow.ReadString(bb));
            }
        }

        return safeHouse;
    }

    public static String canBeSafehouse(IsoGridSquare clickedSquare, IsoPlayer player) {
        if (!GameClient.bClient && !GameServer.bServer) {
            return null;
        } else if (!ServerOptions.instance.PlayerSafehouse.getValue() && !ServerOptions.instance.AdminSafehouse.getValue()) {
            return null;
        } else {
            String string = "";
            if (ServerOptions.instance.PlayerSafehouse.getValue() && hasSafehouse(player) != null) {
                string = string + Translator.getText("IGUI_Safehouse_AlreadyHaveSafehouse") + System.lineSeparator();
            }

            int int0 = ServerOptions.instance.SafehouseDaySurvivedToClaim.getValue();
            if (!ServerOptions.instance.PlayerSafehouse.getValue() && ServerOptions.instance.AdminSafehouse.getValue() && GameClient.bClient) {
                if (!player.accessLevel.equals("admin") && !player.accessLevel.equals("moderator")) {
                    return null;
                }

                int0 = 0;
            }

            if (int0 > 0 && player.getHoursSurvived() < int0 * 24) {
                string = string + Translator.getText("IGUI_Safehouse_DaysSurvivedToClaim", int0) + System.lineSeparator();
            }

            if (GameClient.bClient) {
                KahluaTableIterator kahluaTableIterator0 = GameClient.instance.getServerSpawnRegions().iterator();
                Object object = null;

                while (kahluaTableIterator0.advance()) {
                    KahluaTable table0 = (KahluaTable)kahluaTableIterator0.getValue();
                    KahluaTableIterator kahluaTableIterator1 = ((KahluaTableImpl)table0.rawget("points")).iterator();

                    while (kahluaTableIterator1.advance()) {
                        KahluaTable table1 = (KahluaTable)kahluaTableIterator1.getValue();
                        KahluaTableIterator kahluaTableIterator2 = table1.iterator();

                        while (kahluaTableIterator2.advance()) {
                            KahluaTable table2 = (KahluaTable)kahluaTableIterator2.getValue();
                            Double double0 = (Double)table2.rawget("worldX");
                            Double double1 = (Double)table2.rawget("worldY");
                            Double double2 = (Double)table2.rawget("posX");
                            Double double3 = (Double)table2.rawget("posY");
                            object = IsoWorld.instance.getCell().getGridSquare(double2 + double0 * 300.0, double3 + double1 * 300.0, 0.0);
                            if (object != null && ((IsoGridSquare)object).getBuilding() != null && ((IsoGridSquare)object).getBuilding().getDef() != null) {
                                BuildingDef buildingDef0 = ((IsoGridSquare)object).getBuilding().getDef();
                                if (clickedSquare.getX() >= buildingDef0.getX()
                                    && clickedSquare.getX() < buildingDef0.getX2()
                                    && clickedSquare.getY() >= buildingDef0.getY()
                                    && clickedSquare.getY() < buildingDef0.getY2()) {
                                    return Translator.getText("IGUI_Safehouse_IsSpawnPoint");
                                }
                            }
                        }
                    }
                }
            }

            boolean boolean0 = true;
            boolean boolean1 = false;
            boolean boolean2 = false;
            boolean boolean3 = false;
            boolean boolean4 = false;
            BuildingDef buildingDef1 = clickedSquare.getBuilding().getDef();
            if (clickedSquare.getBuilding().Rooms != null) {
                for (IsoRoom room : clickedSquare.getBuilding().Rooms) {
                    if (room.getName().equals("kitchen")) {
                        boolean2 = true;
                    }

                    if (room.getName().equals("bedroom") || room.getName().equals("livingroom")) {
                        boolean3 = true;
                    }

                    if (room.getName().equals("bathroom")) {
                        boolean4 = true;
                    }
                }
            }

            IsoCell cell = IsoWorld.instance.getCell();

            for (int int1 = 0; int1 < cell.getObjectList().size(); int1++) {
                IsoMovingObject movingObject = cell.getObjectList().get(int1);
                if (movingObject != player
                    && movingObject instanceof IsoGameCharacter
                    && movingObject.getX() >= buildingDef1.getX() - diffError
                    && movingObject.getX() < buildingDef1.getX2() + diffError
                    && movingObject.getY() >= buildingDef1.getY() - diffError
                    && movingObject.getY() < buildingDef1.getY2() + diffError) {
                    boolean0 = false;
                    break;
                }
            }

            if (player.getX() >= buildingDef1.getX() - diffError
                && player.getX() < buildingDef1.getX2() + diffError
                && player.getY() >= buildingDef1.getY() - diffError
                && player.getY() < buildingDef1.getY2() + diffError
                && player.getCurrentSquare() != null
                && !player.getCurrentSquare().Is(IsoFlagType.exterior)) {
                boolean1 = true;
            }

            if (!boolean0 || !boolean1) {
                string = string + Translator.getText("IGUI_Safehouse_SomeoneInside") + System.lineSeparator();
            }

            if (!boolean3 && !ServerOptions.instance.SafehouseAllowNonResidential.getValue()) {
                string = string + Translator.getText("IGUI_Safehouse_NotHouse") + System.lineSeparator();
            }

            return string;
        }
    }

    public void kickOutOfSafehouse(IsoPlayer player) {
        if (player.isAccessLevel("None")) {
            GameClient.sendKickOutOfSafehouse(player);
        }
    }

    public void checkTrespass(IsoPlayer player) {
        if (GameServer.bServer && !ServerOptions.instance.SafehouseAllowTrepass.getValue() && player.getVehicle() == null && !player.isAccessLevel("admin")) {
            SafeHouse safeHouse = isSafeHouse(player.getCurrentSquare(), player.getUsername(), true);
            if (safeHouse != null) {
                GameServer.sendTeleport(player, this.x - 1, this.y - 1, 0.0F);
                if (player.isAsleep()) {
                    player.setAsleep(false);
                    player.setAsleepTime(0.0F);
                    GameServer.sendWakeUpPlayer(player, null);
                }
            }
        }
    }

    public SafeHouse alreadyHaveSafehouse(String username) {
        return ServerOptions.instance.PlayerSafehouse.getValue() ? hasSafehouse(username) : null;
    }

    public SafeHouse alreadyHaveSafehouse(IsoPlayer player) {
        return ServerOptions.instance.PlayerSafehouse.getValue() ? hasSafehouse(player) : null;
    }

    public static boolean allowSafeHouse(IsoPlayer player) {
        boolean boolean0 = false;
        boolean boolean1 = (GameClient.bClient || GameServer.bServer)
            && (ServerOptions.instance.PlayerSafehouse.getValue() || ServerOptions.instance.AdminSafehouse.getValue());
        if (boolean1) {
            if (ServerOptions.instance.PlayerSafehouse.getValue()) {
                boolean0 = hasSafehouse(player) == null;
            }

            if (boolean0
                && ServerOptions.instance.SafehouseDaySurvivedToClaim.getValue() > 0
                && player.getHoursSurvived() / 24.0 < ServerOptions.instance.SafehouseDaySurvivedToClaim.getValue()) {
                boolean0 = false;
            }

            if (ServerOptions.instance.AdminSafehouse.getValue() && GameClient.bClient) {
                boolean0 = player.accessLevel.equals("admin") || player.accessLevel.equals("moderator");
            }
        }

        return boolean0;
    }

    /**
     * Update the last visited value everytime someone is in this safehouse If it's  not visited for some time (SafehouseRemoval serveroption) it's automatically  removed.
     */
    public void updateSafehouse(IsoPlayer player) {
        this.updatePlayersConnected();
        if (player == null || !this.getPlayers().contains(player.getUsername()) && !this.getOwner().equals(player.getUsername())) {
            if (ServerOptions.instance.SafeHouseRemovalTime.getValue() > 0
                && System.currentTimeMillis() - this.getLastVisited() > 3600000L * ServerOptions.instance.SafeHouseRemovalTime.getValue()) {
                boolean boolean0 = false;
                ArrayList arrayList = GameServer.getPlayers(tempPlayers);

                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    IsoPlayer _player = (IsoPlayer)arrayList.get(int0);
                    if (this.containsLocation(_player.x, _player.y)
                        && (this.getPlayers().contains(_player.getUsername()) || this.getOwner().equals(_player.getUsername()))) {
                        boolean0 = true;
                        break;
                    }
                }

                if (boolean0) {
                    this.setLastVisited(System.currentTimeMillis());
                    return;
                }

                this.removeSafeHouse(player, true);
            }
        } else {
            this.setLastVisited(System.currentTimeMillis());
        }
    }

    public SafeHouse(int _x, int _y, int _w, int _h, String player) {
        this.x = _x;
        this.y = _y;
        this.w = _w;
        this.h = _h;
        this.players.add(player);
        this.owner = player;
        this.id = _x + "," + _y + " at " + Calendar.getInstance().getTimeInMillis();
    }

    public String getId() {
        return this.id;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int _x) {
        this.x = _x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int _y) {
        this.y = _y;
    }

    public int getW() {
        return this.w;
    }

    public void setW(int _w) {
        this.w = _w;
    }

    public int getH() {
        return this.h;
    }

    public void setH(int _h) {
        this.h = _h;
    }

    public int getX2() {
        return this.x + this.w;
    }

    public int getY2() {
        return this.y + this.h;
    }

    public boolean containsLocation(float _x, float _y) {
        return _x >= this.getX() && _x < this.getX2() && _y >= this.getY() && _y < this.getY2();
    }

    public ArrayList<String> getPlayers() {
        return this.players;
    }

    public void setPlayers(ArrayList<String> _players) {
        this.players = _players;
    }

    public static ArrayList<SafeHouse> getSafehouseList() {
        return safehouseList;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String _owner) {
        this.owner = _owner;
        if (this.players.contains(_owner)) {
            this.players.remove(_owner);
        }
    }

    public boolean isOwner(IsoPlayer player) {
        return this.getOwner().equals(player.getUsername());
    }

    public long getLastVisited() {
        return this.lastVisited;
    }

    public void setLastVisited(long _lastVisited) {
        this.lastVisited = _lastVisited;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String _title) {
        this.title = _title;
    }

    public int getPlayerConnected() {
        return this.playerConnected;
    }

    public void setPlayerConnected(int _playerConnected) {
        this.playerConnected = _playerConnected;
    }

    public int getOpenTimer() {
        return this.openTimer;
    }

    public void setOpenTimer(int _openTimer) {
        this.openTimer = _openTimer;
    }

    public void setRespawnInSafehouse(boolean b, String username) {
        if (b) {
            this.playersRespawn.add(username);
        } else {
            this.playersRespawn.remove(username);
        }

        if (GameClient.bClient) {
            GameClient.sendSafehouse(this, false);
        }
    }

    public boolean isRespawnInSafehouse(String username) {
        return this.playersRespawn.contains(username);
    }

    public static boolean isPlayerAllowedOnSquare(IsoPlayer player, IsoGridSquare sq) {
        return !ServerOptions.instance.SafehouseAllowTrepass.getValue() ? isSafeHouse(sq, player.getUsername(), true) == null : true;
    }
}
