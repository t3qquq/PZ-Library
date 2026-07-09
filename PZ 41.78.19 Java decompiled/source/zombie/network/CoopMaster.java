// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.Platform;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.ThreadGroups;
import zombie.core.logger.ZipLogs;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;

public class CoopMaster {
    private Process serverProcess;
    private Thread serverThread;
    private PrintStream serverCommandStream;
    private final List<String> incomingMessages;
    private Pattern serverMessageParser;
    private CoopMaster.TerminationReason serverTerminationReason;
    private Thread timeoutWatchThread;
    private boolean serverResponded;
    public static final CoopMaster instance = new CoopMaster();
    private String adminUsername = null;
    private String adminPassword = null;
    private String serverName = null;
    private Long serverSteamID = null;
    private String serverIP = null;
    private Integer serverPort = null;
    private int autoCookie = 0;
    private static final int autoCookieOffset = 1000000;
    private static final int maxAutoCookie = 1000000;
    private final List<CoopMaster.Pair<ICoopServerMessageListener, CoopMaster.ListenerOptions>> listeners;

    private CoopMaster() {
        this.incomingMessages = new LinkedList<>();
        this.listeners = new LinkedList<>();
        this.serverMessageParser = Pattern.compile("^([\\-\\w]+)(\\[(\\d+)\\])?@(.*)$");
        this.adminPassword = UUID.randomUUID().toString();
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void launchServer(String string0, String string1, int int0) throws IOException {
        this.launchServer(string0, string1, int0, false);
    }

    public void softreset(String string0, String string1, int int0) throws IOException {
        this.launchServer(string0, string1, int0, true);
    }

    private void launchServer(String string3, String string1, int int0, boolean boolean0) throws IOException {
        String string0 = Paths.get(System.getProperty("java.home"), "bin", "java").toAbsolutePath().toString();
        if (SteamUtils.isSteamModeEnabled()) {
            string1 = "admin";
        }

        ArrayList arrayList = new ArrayList();
        arrayList.add(string0);
        arrayList.add("-Xms" + int0 + "m");
        arrayList.add("-Xmx" + int0 + "m");
        arrayList.add("-Djava.library.path=" + System.getProperty("java.library.path"));
        arrayList.add("-Djava.class.path=" + System.getProperty("java.class.path"));
        arrayList.add("-Duser.dir=" + System.getProperty("user.dir"));
        arrayList.add("-Duser.home=" + System.getProperty("user.home"));
        arrayList.add("-Dzomboid.znetlog=2");
        arrayList.add("-Dzomboid.steam=" + (SteamUtils.isSteamModeEnabled() ? "1" : "0"));
        arrayList.add("-Djava.awt.headless=true");
        arrayList.add("-XX:-OmitStackTraceInFastThrow");
        String string2 = this.getGarbageCollector();
        if (string2 != null) {
            arrayList.add(string2);
        }

        if (boolean0) {
            arrayList.add("-Dsoftreset");
        }

        if (Core.bDebug) {
            arrayList.add("-Ddebug");
        }

        arrayList.add("zombie.network.GameServer");
        arrayList.add("-coop");
        arrayList.add("-servername");
        arrayList.add(this.serverName = string3);
        arrayList.add("-adminusername");
        arrayList.add(this.adminUsername = string1);
        arrayList.add("-adminpassword");
        arrayList.add(this.adminPassword);
        arrayList.add("-cachedir=" + ZomboidFileSystem.instance.getCacheDir());
        ProcessBuilder processBuilder = new ProcessBuilder(arrayList);
        ZipLogs.addZipFile(false);
        this.serverTerminationReason = CoopMaster.TerminationReason.NormalTermination;
        this.serverResponded = false;
        this.serverProcess = processBuilder.start();
        this.serverCommandStream = new PrintStream(this.serverProcess.getOutputStream());
        this.serverThread = new Thread(ThreadGroups.Workers, this::readServer);
        this.serverThread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
        this.serverThread.start();
        this.timeoutWatchThread = new Thread(ThreadGroups.Workers, this::watchServer);
        this.timeoutWatchThread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
        this.timeoutWatchThread.start();
    }

    private String getGarbageCollector() {
        try {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            List list = runtimeMXBean.getInputArguments();
            boolean boolean0 = false;
            boolean boolean1 = false;

            for (String string : list) {
                if ("-XX:+UseZGC".equals(string)) {
                    boolean0 = true;
                }

                if ("-XX:-UseZGC".equals(string)) {
                    boolean0 = false;
                }

                if ("-XX:+UseG1GC".equals(string)) {
                    boolean1 = true;
                }

                if ("-XX:-UseG1GC".equals(string)) {
                    boolean1 = false;
                }
            }

            if (boolean0) {
                return "-XX:+UseZGC";
            }

            if (boolean1) {
                return "-XX:+UseG1GC";
            }
        } catch (Throwable throwable) {
        }

        return null;
    }

    private void readServer() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.serverProcess.getInputStream()));

        while (true) {
            try {
                int int0 = this.serverProcess.exitValue();
                break;
            } catch (IllegalThreadStateException illegalThreadStateException) {
                String string = null;

                try {
                    string = bufferedReader.readLine();
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                }

                if (string != null) {
                    this.storeMessage(string);
                    this.serverResponded = true;
                }
            }
        }

        this.storeMessage("process-status@terminated");
        ZipLogs.addZipFile(true);
    }

    public void abortServer() {
        this.serverProcess.destroy();
    }

    private void watchServer() {
        byte byte0 = 20;

        try {
            Thread.sleep(1000 * byte0);
            if (!this.serverResponded) {
                this.serverTerminationReason = CoopMaster.TerminationReason.Timeout;
                this.abortServer();
            }
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public boolean isRunning() {
        return this.serverThread != null && this.serverThread.isAlive();
    }

    public CoopMaster.TerminationReason terminationReason() {
        return this.serverTerminationReason;
    }

    private void storeMessage(String string) {
        synchronized (this.incomingMessages) {
            this.incomingMessages.add(string);
        }
    }

    public synchronized void sendMessage(String string0, String string1, String string2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string0);
        if (string1 == null) {
            stringBuilder.append("@");
        } else {
            stringBuilder.append("[");
            stringBuilder.append(string1);
            stringBuilder.append("]@");
        }

        stringBuilder.append(string2);
        String string3 = stringBuilder.toString();
        if (this.serverCommandStream != null) {
            this.serverCommandStream.println(string3);
            this.serverCommandStream.flush();
        }
    }

    public void sendMessage(String string0, String string1) {
        this.sendMessage(string0, null, string1);
    }

    public synchronized void invokeServer(String string1, String string2, ICoopServerMessageListener iCoopServerMessageListener) {
        this.autoCookie = (this.autoCookie + 1) % 1000000;
        String string0 = Integer.toString(1000000 + this.autoCookie);
        this.addListener(iCoopServerMessageListener, new CoopMaster.ListenerOptions(string1, string0, true));
        this.sendMessage(string1, string0, string2);
    }

    public String getMessage() {
        String string = null;
        synchronized (this.incomingMessages) {
            if (this.incomingMessages.size() != 0) {
                string = this.incomingMessages.get(0);
                this.incomingMessages.remove(0);
                if (!"ping@ping".equals(string)) {
                    System.out.println("SERVER: " + string);
                }
            }

            return string;
        }
    }

    public void update() {
        String string0;
        while ((string0 = this.getMessage()) != null) {
            Matcher matcher = this.serverMessageParser.matcher(string0);
            if (matcher.find()) {
                String string1 = matcher.group(1);
                String string2 = matcher.group(3);
                String string3 = matcher.group(4);
                LuaEventManager.triggerEvent("OnCoopServerMessage", string1, string2, string3);
                this.handleMessage(string1, string2, string3);
            } else {
                DebugLog.log(DebugType.Network, "[CoopMaster] Unknown message incoming from the slave server: " + string0);
            }
        }
    }

    private void handleMessage(String string0, String string1, String string2) {
        if (Objects.equals(string0, "ping")) {
            this.sendMessage("ping", string1, "pong");
        } else if (Objects.equals(string0, "steam-id")) {
            if (Objects.equals(string2, "null")) {
                this.serverSteamID = null;
            } else {
                this.serverSteamID = SteamUtils.convertStringToSteamID(string2);
            }
        } else if (Objects.equals(string0, "server-address")) {
            DebugLog.log("Got server-address: " + string2);
            String string3 = "^(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)$";
            Pattern pattern = Pattern.compile(string3);
            Matcher matcher = pattern.matcher(string2);
            if (matcher.find()) {
                String string4 = matcher.group(1);
                String string5 = matcher.group(2);
                this.serverIP = string4;
                this.serverPort = Integer.valueOf(string5);
                DebugLog.log("Successfully parsed: address = " + this.serverIP + ", port = " + this.serverPort);
            } else {
                DebugLog.log("Failed to parse server address");
            }
        }

        this.invokeListeners(string0, string1, string2);
    }

    public void register(Platform platform, KahluaTable table1) {
        KahluaTable table0 = platform.newTable();
        table0.rawset("launch", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int int0) {
                boolean boolean0 = false;
                if (int0 == 4) {
                    Object object0 = luaCallFrame.get(1);
                    Object object1 = luaCallFrame.get(2);
                    Object object2 = luaCallFrame.get(3);
                    if (!(object0 instanceof String) || !(object1 instanceof String) || !(object2 instanceof Double)) {
                        return 0;
                    }

                    try {
                        CoopMaster.this.launchServer((String)object0, (String)object1, ((Double)object2).intValue());
                        boolean0 = true;
                    } catch (IOException iOException) {
                        iOException.printStackTrace();
                    }
                } else {
                    DebugLog.log(DebugType.Network, "[CoopMaster] wrong number of arguments: " + int0);
                }

                luaCallFrame.push(boolean0);
                return 1;
            }
        });
        table0.rawset("softreset", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int int0) {
                boolean boolean0 = false;
                if (int0 == 4) {
                    Object object0 = luaCallFrame.get(1);
                    Object object1 = luaCallFrame.get(2);
                    Object object2 = luaCallFrame.get(3);
                    if (!(object0 instanceof String) || !(object1 instanceof String) || !(object2 instanceof Double)) {
                        return 0;
                    }

                    try {
                        CoopMaster.this.softreset((String)object0, (String)object1, ((Double)object2).intValue());
                        boolean0 = true;
                    } catch (IOException iOException) {
                        iOException.printStackTrace();
                    }
                } else {
                    DebugLog.log(DebugType.Network, "[CoopMaster] wrong number of arguments: " + int0);
                }

                luaCallFrame.push(boolean0);
                return 1;
            }
        });
        table0.rawset("isRunning", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                luaCallFrame.push(CoopMaster.this.isRunning());
                return 1;
            }
        });
        table0.rawset("sendMessage", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int int0) {
                if (int0 == 4) {
                    Object object0 = luaCallFrame.get(1);
                    Object object1 = luaCallFrame.get(2);
                    Object object2 = luaCallFrame.get(3);
                    if (object0 instanceof String && object1 instanceof String && object2 instanceof String) {
                        CoopMaster.this.sendMessage((String)object0, (String)object1, (String)object2);
                    }
                } else if (int0 == 3) {
                    Object object3 = luaCallFrame.get(1);
                    Object object4 = luaCallFrame.get(2);
                    if (object3 instanceof String && object4 instanceof String) {
                        CoopMaster.this.sendMessage((String)object3, (String)object4);
                    }
                }

                return 0;
            }
        });
        table0.rawset("getAdminPassword", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                luaCallFrame.push(CoopMaster.this.adminPassword);
                return 1;
            }
        });
        table0.rawset("getTerminationReason", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                luaCallFrame.push(CoopMaster.this.serverTerminationReason.toString());
                return 1;
            }
        });
        table0.rawset("getSteamID", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                if (CoopMaster.this.serverSteamID != null) {
                    luaCallFrame.push(SteamUtils.convertSteamIDToString(CoopMaster.this.serverSteamID));
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        table0.rawset("getAddress", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                luaCallFrame.push(CoopMaster.this.serverIP);
                return 1;
            }
        });
        table0.rawset("getPort", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                luaCallFrame.push(CoopMaster.this.serverPort);
                return 1;
            }
        });
        table0.rawset("abort", new JavaFunction() {
            @Override
            public int call(LuaCallFrame var1, int var2) {
                CoopMaster.this.abortServer();
                return 0;
            }
        });
        table0.rawset("getServerSaveFolder", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                Object object = luaCallFrame.get(1);
                luaCallFrame.push(CoopMaster.this.getServerSaveFolder((String)object));
                return 1;
            }
        });
        table0.rawset("getPlayerSaveFolder", new JavaFunction() {
            @Override
            public int call(LuaCallFrame luaCallFrame, int var2) {
                Object object = luaCallFrame.get(1);
                luaCallFrame.push(CoopMaster.this.getPlayerSaveFolder((String)object));
                return 1;
            }
        });
        table1.rawset("CoopServer", table0);
    }

    public void addListener(ICoopServerMessageListener iCoopServerMessageListener, CoopMaster.ListenerOptions listenerOptions) {
        synchronized (this.listeners) {
            this.listeners.add(new CoopMaster.Pair<>(iCoopServerMessageListener, listenerOptions));
        }
    }

    public void addListener(ICoopServerMessageListener iCoopServerMessageListener) {
        this.addListener(iCoopServerMessageListener, null);
    }

    public void removeListener(ICoopServerMessageListener iCoopServerMessageListener) {
        synchronized (this.listeners) {
            int int0 = 0;

            while (int0 < this.listeners.size() && this.listeners.get(int0).first != iCoopServerMessageListener) {
                int0++;
            }

            if (int0 < this.listeners.size()) {
                this.listeners.remove(int0);
            }
        }
    }

    private void invokeListeners(String string0, String string1, String string2) {
        synchronized (this.listeners) {
            Iterator iterator = this.listeners.iterator();

            while (iterator.hasNext()) {
                CoopMaster.Pair pair = (CoopMaster.Pair)iterator.next();
                ICoopServerMessageListener iCoopServerMessageListener = (ICoopServerMessageListener)pair.first;
                CoopMaster.ListenerOptions listenerOptions = (CoopMaster.ListenerOptions)pair.second;
                if (iCoopServerMessageListener != null) {
                    if (listenerOptions == null) {
                        iCoopServerMessageListener.OnCoopServerMessage(string0, string1, string2);
                    } else if ((listenerOptions.tag == null || listenerOptions.tag.equals(string0))
                        && (listenerOptions.cookie == null || listenerOptions.cookie.equals(string1))) {
                        if (listenerOptions.autoRemove) {
                            iterator.remove();
                        }

                        iCoopServerMessageListener.OnCoopServerMessage(string0, string1, string2);
                    }
                }
            }
        }
    }

    public String getServerName() {
        return this.serverName;
    }

    public String getServerSaveFolder(String string) {
        return LuaManager.GlobalObject.sanitizeWorldName(string);
    }

    public String getPlayerSaveFolder(String string) {
        return LuaManager.GlobalObject.sanitizeWorldName(string + "_player");
    }

    public class ListenerOptions {
        public String tag = null;
        public String cookie = null;
        public boolean autoRemove = false;

        public ListenerOptions(String string0, String string1, boolean boolean0) {
            this.tag = string0;
            this.cookie = string1;
            this.autoRemove = boolean0;
        }

        public ListenerOptions(String string0, String string1) {
            this(string0, string1, false);
        }

        public ListenerOptions(String string) {
            this(string, null, false);
        }
    }

    private class Pair<K, V> {
        private final K first;
        private final V second;

        public Pair(K object0, V object1) {
            this.first = (K)object0;
            this.second = (V)object1;
        }

        public K getFirst() {
            return this.first;
        }

        public V getSecond() {
            return this.second;
        }
    }

    public static enum TerminationReason {
        NormalTermination,
        Timeout;
    }
}
