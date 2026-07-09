// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

public class Server {
    private String name = "My Server";
    private String ip = "127.0.0.1";
    private String localIP = "";
    private String port = "16262";
    private String serverpwd = "";
    private String description = "";
    private String userName = "";
    private String pwd = "";
    private boolean useSteamRelay = false;
    private int lastUpdate = 0;
    private String players = null;
    private String maxPlayers = null;
    private boolean open = false;
    private boolean bPublic = true;
    private String version = null;
    private String mods = null;
    private boolean passwordProtected;
    private String steamId = null;
    private String ping = null;
    private boolean hosted = false;

    public String getPort() {
        return this.port;
    }

    public void setPort(String _port) {
        this.port = _port;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String _ip) {
        this.ip = _ip;
    }

    public String getLocalIP() {
        return this.localIP;
    }

    public void setLocalIP(String _ip) {
        this.localIP = _ip;
    }

    public String getServerPassword() {
        return this.serverpwd;
    }

    public void setServerPassword(String _pwd) {
        this.serverpwd = _pwd == null ? "" : _pwd;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String _description) {
        this.description = _description;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String _userName) {
        this.userName = _userName;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String _pwd) {
        this.pwd = _pwd;
    }

    public boolean getUseSteamRelay() {
        return this.useSteamRelay;
    }

    public void setUseSteamRelay(boolean boolean0) {
        this.useSteamRelay = boolean0;
    }

    public int getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(int _lastUpdate) {
        this.lastUpdate = _lastUpdate;
    }

    public String getPlayers() {
        return this.players;
    }

    public void setPlayers(String _players) {
        this.players = _players;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean _open) {
        this.open = _open;
    }

    public boolean isPublic() {
        return this.bPublic;
    }

    public void setPublic(boolean _bPublic) {
        this.bPublic = _bPublic;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String _version) {
        this.version = _version;
    }

    public String getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(String _maxPlayers) {
        this.maxPlayers = _maxPlayers;
    }

    public String getMods() {
        return this.mods;
    }

    public void setMods(String _mods) {
        this.mods = _mods;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public String getPing() {
        return this.ping;
    }

    public void setPing(String _ping) {
        this.ping = _ping;
    }

    public boolean isPasswordProtected() {
        return this.passwordProtected;
    }

    public void setPasswordProtected(boolean pp) {
        this.passwordProtected = pp;
    }

    public String getSteamId() {
        return this.steamId;
    }

    public void setSteamId(String _steamId) {
        this.steamId = _steamId;
    }

    public boolean isHosted() {
        return this.hosted;
    }

    public void setHosted(boolean _hosted) {
        this.hosted = _hosted;
    }
}
