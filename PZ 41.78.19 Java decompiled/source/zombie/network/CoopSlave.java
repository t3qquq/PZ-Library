// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.ZomboidFileSystem;
import zombie.core.znet.PortMapper;
import zombie.core.znet.SteamGameServer;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;

public class CoopSlave {
    private static PrintStream stdout;
    private static PrintStream stderr;
    private Pattern serverMessageParser;
    private long nextPing = -1L;
    private long lastPong = -1L;
    public static CoopSlave instance;
    public String hostUser = null;
    public long hostSteamID = 0L;
    private boolean masterLost = false;
    private HashSet<Long> invites = new HashSet<>();
    private Long serverSteamID = null;

    public static void init() throws FileNotFoundException {
        instance = new CoopSlave();
    }

    public static void initStreams() throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(ZomboidFileSystem.instance.getCacheDir() + File.separator + "coop-console.txt");
        stdout = System.out;
        stderr = System.err;
        System.setOut(new PrintStream(fileOutputStream));
        System.setErr(System.out);
    }

    private CoopSlave() {
        this.serverMessageParser = Pattern.compile("^([\\-\\w]+)(\\[(\\d+)\\])?@(.*)$");
        this.notify("coop mode enabled");
        if (System.getProperty("hostUser") != null) {
            this.hostUser = System.getProperty("zomboid.hostUser").trim();
        }
    }

    public synchronized void notify(String string) {
        this.sendMessage("info", null, string);
    }

    public synchronized void sendStatus(String string) {
        this.sendMessage("status", null, string);
    }

    public static void status(String string) {
        if (instance != null) {
            instance.sendStatus(string);
        }
    }

    public synchronized void sendMessage(String string) {
        this.sendMessage("message", null, string);
    }

    public synchronized void sendMessage(String string2, String string0, String string1) {
        if (string0 != null) {
            stdout.println(string2 + "[" + string0 + "]@" + string1);
        } else {
            stdout.println(string2 + "@" + string1);
        }
    }

    public void sendExternalIPAddress(String string) {
        this.sendMessage("get-parameter", string, PortMapper.getExternalAddress());
    }

    public synchronized void sendSteamID(String string) {
        if (this.serverSteamID == null && SteamUtils.isSteamModeEnabled()) {
            this.serverSteamID = SteamGameServer.GetSteamID();
        }

        this.sendMessage("get-parameter", string, this.serverSteamID.toString());
    }

    public boolean handleCommand(String string0) {
        Matcher matcher = this.serverMessageParser.matcher(string0);
        if (matcher.find()) {
            String string1 = matcher.group(1);
            String string2 = matcher.group(3);
            String string3 = matcher.group(4);
            if (Objects.equals(string1, "set-host-user")) {
                System.out.println("Set host user:" + string3);
                this.hostUser = string3;
            }

            if (Objects.equals(string1, "set-host-steamid")) {
                this.hostSteamID = SteamUtils.convertStringToSteamID(string3);
            }

            if (Objects.equals(string1, "invite-add")) {
                Long long0 = SteamUtils.convertStringToSteamID(string3);
                if (long0 != -1L) {
                    this.invites.add(long0);
                }
            }

            if (Objects.equals(string1, "invite-remove")) {
                Long long1 = SteamUtils.convertStringToSteamID(string3);
                if (long1 != -1L) {
                    this.invites.remove(long1);
                }
            }

            if (Objects.equals(string1, "get-parameter")) {
                DebugLog.log("Got get-parameter command: tag = " + string1 + " payload = " + string3);
                if (Objects.equals(string3, "external-ip")) {
                    this.sendExternalIPAddress(string2);
                } else if (Objects.equals(string3, "steam-id")) {
                    this.sendSteamID(string2);
                }
            }

            if (Objects.equals(string1, "ping")) {
                this.lastPong = System.currentTimeMillis();
            }

            if (Objects.equals(string1, "process-status") && Objects.equals(string3, "eof")) {
                DebugLog.log("Master connection lost: EOF");
                this.masterLost = true;
            }

            return true;
        } else {
            DebugLog.log("Got malformed command: " + string0);
            return false;
        }
    }

    public String getHostUser() {
        return this.hostUser;
    }

    public void update() {
        long long0 = System.currentTimeMillis();
        if (long0 >= this.nextPing) {
            this.sendMessage("ping", null, "ping");
            this.nextPing = long0 + 5000L;
        }

        long long1 = 60000L;
        if (this.lastPong == -1L) {
            this.lastPong = long0;
        }

        this.masterLost = this.masterLost || long0 - this.lastPong > long1;
    }

    public boolean masterLost() {
        return this.masterLost;
    }

    public boolean isHost(long long0) {
        return long0 == this.hostSteamID;
    }

    public boolean isInvited(long long0) {
        return this.invites.contains(long0);
    }
}
