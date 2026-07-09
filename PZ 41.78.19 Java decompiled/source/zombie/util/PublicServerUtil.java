// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import zombie.core.Core;
import zombie.core.ThreadGroups;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.network.GameServer;
import zombie.network.ServerOptions;

public final class PublicServerUtil {
    public static String webSite = "https://www.projectzomboid.com/server_browser/";
    private static long timestampForUpdate = 0L;
    private static long timestampForPlayerUpdate = 0L;
    private static long updateTick = 600000L;
    private static long updatePlayerTick = 300000L;
    private static int sentPlayerCount = 0;
    private static boolean isEnabled = false;

    public static void init() {
        isEnabled = false;
        if (DebugOptions.instance.Network.PublicServerUtil.Enabled.getValue()) {
            try {
                if (GameServer.bServer) {
                    ServerOptions.instance.changeOption("PublicName", checkHacking(ServerOptions.instance.getOption("PublicName")));
                    ServerOptions.instance.changeOption("PublicDescription", checkHacking(ServerOptions.instance.getOption("PublicDescription")));
                }

                if (GameServer.bServer && !isPublic()) {
                    return;
                }

                DebugLog.log("connecting to public server list");
                URL url = new URL(webSite + "serverVar.php");
                URLConnection uRLConnection = url.openConnection();
                uRLConnection.setConnectTimeout(3000);
                uRLConnection.connect();
                InputStreamReader inputStreamReader = new InputStreamReader(uRLConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                Object object = null;
                StringBuilder stringBuilder = new StringBuilder();

                while ((object = bufferedReader.readLine()) != null) {
                    stringBuilder.append((String)object).append('\n');
                }

                bufferedReader.close();
                String[] strings = stringBuilder.toString().split("<br>");

                for (String string : strings) {
                    if (string.contains("allowed") && string.contains("true")) {
                        isEnabled = true;
                    }

                    if (string.contains("updateTick")) {
                        updateTick = Long.parseLong(string.split("=")[1].trim());
                    }

                    if (string.contains("updatePlayerTick")) {
                        updatePlayerTick = Long.parseLong(string.split("=")[1].trim());
                    }

                    if (string.contains("ip")) {
                        GameServer.ip = string.split("=")[1].trim();
                        if (GameServer.ip.contains(":")) {
                            DebugLog.log(
                                "The IP address ("
                                    + GameServer.ip
                                    + ") looks like the IPv6 address. Please make sure IPv4 server address is set to the "
                                    + ServerOptions.getInstance().server_browser_announced_ip.getName()
                                    + " server option."
                            );
                        }
                    }
                }
            } catch (SocketTimeoutException socketTimeoutException) {
                isEnabled = false;
                DebugLog.log("timeout trying to connect to public server list");
            } catch (Exception exception) {
                isEnabled = false;
                exception.printStackTrace();
            }
        }
    }

    private static String checkHacking(String string) {
        return string == null
            ? ""
            : string.replaceAll("--", "")
                .replaceAll("->", "")
                .replaceAll("(?i)select union", "")
                .replaceAll("(?i)select join", "")
                .replaceAll("1=1", "")
                .replaceAll("(?i)delete from", "");
    }

    public static void insertOrUpdate() {
        if (isEnabled) {
            if (isPublic()) {
                try {
                    insertDatas();
                } catch (Exception exception) {
                    System.out.println("Can't reach PZ.com");
                }
            }
        }
    }

    private static boolean isPublic() {
        String string = checkHacking(ServerOptions.instance.PublicName.getValue());
        return ServerOptions.instance.Public.getValue() && !string.isEmpty();
    }

    public static void update() {
        if (System.currentTimeMillis() - timestampForUpdate > updateTick) {
            timestampForUpdate = System.currentTimeMillis();
            init();
            if (!isEnabled) {
                return;
            }

            if (isPublic()) {
                try {
                    insertDatas();
                } catch (Exception exception) {
                    System.out.println("Can't reach PZ.com");
                }
            }
        }
    }

    private static void insertDatas() throws Exception {
        if (isEnabled) {
            String string0 = "";
            if (!ServerOptions.instance.PublicDescription.getValue().isEmpty()) {
                string0 = "&desc=" + ServerOptions.instance.PublicDescription.getValue().replaceAll(" ", "%20");
            }

            String string1 = "";

            for (String string2 : GameServer.ServerMods) {
                string1 = string1 + string2 + ",";
            }

            if (!"".equals(string1)) {
                string1 = string1.substring(0, string1.length() - 1);
                string1 = "&mods=" + string1.replaceAll(" ", "%20");
            }

            String string3 = GameServer.ip;
            if (!ServerOptions.instance.server_browser_announced_ip.getValue().isEmpty()) {
                string3 = ServerOptions.instance.server_browser_announced_ip.getValue();
            }

            timestampForUpdate = System.currentTimeMillis();
            int int0 = GameServer.getPlayerCount();
            callUrl(
                webSite
                    + "write.php?name="
                    + ServerOptions.instance.PublicName.getValue().replaceAll(" ", "%20")
                    + string0
                    + "&port="
                    + ServerOptions.instance.DefaultPort.getValue()
                    + "&UDPPort="
                    + ServerOptions.instance.UDPPort.getValue()
                    + "&players="
                    + int0
                    + "&ip="
                    + string3
                    + "&open="
                    + (ServerOptions.instance.Open.getValue() ? "1" : "0")
                    + "&password="
                    + ("".equals(ServerOptions.instance.Password.getValue()) ? "0" : "1")
                    + "&maxPlayers="
                    + ServerOptions.getInstance().getMaxPlayers()
                    + "&version="
                    + Core.getInstance().getVersion().replaceAll(" ", "%20")
                    + string1
                    + "&mac="
                    + getMacAddress()
            );
            sentPlayerCount = int0;
        }
    }

    public static void updatePlayers() {
        if (System.currentTimeMillis() - timestampForPlayerUpdate > updatePlayerTick) {
            timestampForPlayerUpdate = System.currentTimeMillis();
            if (!isEnabled) {
                return;
            }

            try {
                String string = GameServer.ip;
                if (!ServerOptions.instance.server_browser_announced_ip.getValue().isEmpty()) {
                    string = ServerOptions.instance.server_browser_announced_ip.getValue();
                }

                int int0 = GameServer.getPlayerCount();
                callUrl(webSite + "updatePlayers.php?port=" + ServerOptions.instance.DefaultPort.getValue() + "&players=" + int0 + "&ip=" + string);
                sentPlayerCount = GameServer.getPlayerCount();
            } catch (Exception exception) {
                System.out.println("Can't reach PZ.com");
            }
        }
    }

    public static void updatePlayerCountIfChanged() {
        if (isEnabled && sentPlayerCount != GameServer.getPlayerCount()) {
            updatePlayers();
        }
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    private static String getMacAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
            if (networkInterface != null) {
                byte[] bytes = networkInterface.getHardwareAddress();
                StringBuilder stringBuilder = new StringBuilder();

                for (int int0 = 0; int0 < bytes.length; int0++) {
                    stringBuilder.append(String.format("%02X%s", bytes[int0], int0 < bytes.length - 1 ? "-" : ""));
                }

                return stringBuilder.toString();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return "";
    }

    private static void callUrl(String string) {
        new Thread(ThreadGroups.Workers, Lambda.invoker(string, stringx -> {
            try {
                URL url = new URL(stringx);
                URLConnection uRLConnection = url.openConnection();
                uRLConnection.getInputStream();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }), "openUrl").start();
    }
}
