// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.chat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import zombie.GameWindow;
import zombie.characters.Faction;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatMessage;
import zombie.chat.ChatTab;
import zombie.chat.ChatUtility;
import zombie.chat.ServerChatMessage;
import zombie.chat.defaultChats.AdminChat;
import zombie.chat.defaultChats.FactionChat;
import zombie.chat.defaultChats.GeneralChat;
import zombie.chat.defaultChats.RadioChat;
import zombie.chat.defaultChats.SafehouseChat;
import zombie.chat.defaultChats.SayChat;
import zombie.chat.defaultChats.ServerChat;
import zombie.chat.defaultChats.ShoutChat;
import zombie.chat.defaultChats.WhisperChat;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.areas.SafeHouse;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;

public class ChatServer {
    private static ChatServer instance = null;
    private static final Stack<Integer> availableChatsID = new Stack<>();
    private static int lastChatId = -1;
    private static final HashMap<ChatType, ChatBase> defaultChats = new HashMap<>();
    private static final ConcurrentHashMap<Integer, ChatBase> chats = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, FactionChat> factionChats = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, SafehouseChat> safehouseChats = new ConcurrentHashMap<>();
    private static AdminChat adminChat = null;
    private static GeneralChat generalChat = null;
    private static ServerChat serverChat = null;
    private static RadioChat radioChat = null;
    private static boolean inited = false;
    private static final HashSet<Short> players = new HashSet<>();
    private static final String logName = "chat";
    private static ZLogger logger;
    private static final HashMap<String, ChatTab> tabs = new HashMap<>();
    private static final String mainTabID = "main";
    private static final String adminTabID = "admin";

    public static ChatServer getInstance() {
        if (instance == null) {
            instance = new ChatServer();
        }

        return instance;
    }

    public static boolean isInited() {
        return inited;
    }

    private ChatServer() {
    }

    public void init() {
        if (!inited) {
            LoggerManager.createLogger("chat", Core.bDebug);
            logger = LoggerManager.getLogger("chat");
            logger.write("Start chat server initialization...", "info");
            ChatTab chatTab0 = new ChatTab((short)0, "UI_chat_main_tab_title_id");
            ChatTab chatTab1 = new ChatTab((short)1, "UI_chat_admin_tab_title_id");
            boolean boolean0 = ServerOptions.getInstance().DiscordEnable.getValue();
            GeneralChat generalChatx = new GeneralChat(this.getNextChatID(), chatTab0, boolean0);
            SayChat sayChat = new SayChat(this.getNextChatID(), chatTab0);
            ShoutChat shoutChat = new ShoutChat(this.getNextChatID(), chatTab0);
            RadioChat radioChatx = new RadioChat(this.getNextChatID(), chatTab0);
            AdminChat adminChatx = new AdminChat(this.getNextChatID(), chatTab1);
            ServerChat serverChatx = new ServerChat(this.getNextChatID(), chatTab0);
            chats.put(generalChatx.getID(), generalChatx);
            chats.put(sayChat.getID(), sayChat);
            chats.put(shoutChat.getID(), shoutChat);
            chats.put(radioChatx.getID(), radioChatx);
            chats.put(adminChatx.getID(), adminChatx);
            chats.put(serverChatx.getID(), serverChatx);
            defaultChats.put(generalChatx.getType(), generalChatx);
            defaultChats.put(sayChat.getType(), sayChat);
            defaultChats.put(shoutChat.getType(), shoutChat);
            defaultChats.put(serverChatx.getType(), serverChatx);
            defaultChats.put(radioChatx.getType(), radioChatx);
            tabs.put("main", chatTab0);
            tabs.put("admin", chatTab1);
            generalChat = generalChatx;
            adminChat = adminChatx;
            serverChat = serverChatx;
            radioChat = radioChatx;
            inited = true;
            logger.write("General chat has id = " + generalChatx.getID(), "info");
            logger.write("Say chat has id = " + sayChat.getID(), "info");
            logger.write("Shout chat has id = " + shoutChat.getID(), "info");
            logger.write("Radio chat has id = " + radioChatx.getID(), "info");
            logger.write("Admin chat has id = " + adminChatx.getID(), "info");
            logger.write("Server chat has id = " + serverChat.getID(), "info");
            logger.write("Chat server successfully initialized", "info");
        }
    }

    public void initPlayer(short short0) {
        logger.write("Player with id = '" + short0 + "' tries to connect", "info");
        synchronized (players) {
            if (players.contains(short0)) {
                logger.write("Player already connected!", "warning");
                return;
            }
        }

        logger.write("Adding player '" + short0 + "' to chat server", "info");
        IsoPlayer player = ChatUtility.findPlayer(short0);
        UdpConnection udpConnection = ChatUtility.findConnection(short0);
        if (udpConnection != null && player != null) {
            this.sendInitPlayerChatPacket(udpConnection);
            this.addDefaultChats(short0);
            logger.write("Player joined to default chats", "info");
            if (udpConnection.accessLevel == 32) {
                this.joinAdminChat(short0);
            }

            Faction faction = Faction.getPlayerFaction(player);
            if (faction != null) {
                this.addMemberToFactionChat(faction.getName(), short0);
            }

            SafeHouse safeHouse = SafeHouse.hasSafehouse(player);
            if (safeHouse != null) {
                this.addMemberToSafehouseChat(safeHouse.getId(), short0);
            }

            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType.PlayerConnectedToChat.doPacket(byteBufferWriter);
            PacketTypes.PacketType.PlayerConnectedToChat.send(udpConnection);
            synchronized (players) {
                players.add(short0);
            }

            logger.write("Player " + player.getUsername() + "(" + short0 + ") joined to chat server successfully", "info");
        } else {
            logger.write("Player or connection is not found on server!", "error");
            logger.write((udpConnection == null ? "connection = null " : "") + (player == null ? "player = null" : ""), "error");
        }
    }

    public void processMessageFromPlayerPacket(ByteBuffer byteBuffer) {
        int int0 = byteBuffer.getInt();
        synchronized (chats) {
            ChatBase chatBase = chats.get(int0);
            ChatMessage chatMessage = chatBase.unpackMessage(byteBuffer);
            logger.write("Got message:" + chatMessage, "info");
            if (!ChatUtility.chatStreamEnabled(chatBase.getType())) {
                logger.write("Message ignored by server because the chat disabled by server settings", "warning");
            } else {
                this.sendMessage(chatMessage);
                logger.write("Message " + chatMessage + " sent to chat (id = " + chatBase.getID() + ") members", "info");
            }
        }
    }

    public void processPlayerStartWhisperChatPacket(ByteBuffer byteBuffer) {
        logger.write("Whisper chat starting...", "info");
        if (!ChatUtility.chatStreamEnabled(ChatType.whisper)) {
            logger.write("Message for whisper chat is ignored because whisper chat is disabled by server settings", "info");
        } else {
            String string0 = GameWindow.ReadString(byteBuffer);
            String string1 = GameWindow.ReadString(byteBuffer);
            logger.write("Player '" + string0 + "' attempt to start whispering with '" + string1 + "'", "info");
            IsoPlayer player0 = ChatUtility.findPlayer(string0);
            IsoPlayer player1 = ChatUtility.findPlayer(string1);
            if (player0 == null) {
                logger.write("Player '" + string0 + "' is not found!", "error");
                throw new RuntimeException("Player not found");
            } else if (player1 == null) {
                logger.write("Player '" + string0 + "' attempt to start whisper dialog with '" + string1 + "' but this player not found!", "info");
                UdpConnection udpConnection = ChatUtility.findConnection(player0.getOnlineID());
                this.sendPlayerNotFoundMessage(udpConnection, string1);
            } else {
                logger.write("Both players found", "info");
                WhisperChat whisperChat = new WhisperChat(this.getNextChatID(), tabs.get("main"), string0, string1);
                whisperChat.addMember(player0.getOnlineID());
                whisperChat.addMember(player1.getOnlineID());
                chats.put(whisperChat.getID(), whisperChat);
                logger.write(
                    "Whisper chat (id = " + whisperChat.getID() + ") between '" + player0.getUsername() + "' and '" + player1.getUsername() + "' started",
                    "info"
                );
            }
        }
    }

    private void sendPlayerNotFoundMessage(UdpConnection udpConnection, String string) {
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.PlayerNotFound.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(string);
        PacketTypes.PacketType.PlayerNotFound.send(udpConnection);
        logger.write("'Player not found' packet was sent", "info");
    }

    public ChatMessage unpackChatMessage(ByteBuffer byteBuffer) {
        int int0 = byteBuffer.getInt();
        return chats.get(int0).unpackMessage(byteBuffer);
    }

    public void disconnectPlayer(short short0) {
        logger.write("Player " + short0 + " disconnecting...", "info");
        synchronized (chats) {
            for (ChatBase chatBase : chats.values()) {
                chatBase.removeMember(short0);
                if (chatBase.getType() == ChatType.whisper) {
                    this.closeChat(chatBase.getID());
                }
            }
        }

        synchronized (players) {
            players.remove(short0);
        }

        logger.write("Disconnecting player " + short0 + " finished", "info");
    }

    private void closeChat(int int0) {
        synchronized (chats) {
            if (!chats.containsKey(int0)) {
                throw new RuntimeException("Chat '" + int0 + "' requested to close but it's not exists.");
            }

            ChatBase chatBase = chats.get(int0);
            chatBase.close();
            chats.remove(int0);
        }

        synchronized (availableChatsID) {
            availableChatsID.push(int0);
        }
    }

    public void joinAdminChat(short short0) {
        if (adminChat == null) {
            logger.write("Admin chat is null! Can't add player to it", "warning");
        } else {
            adminChat.addMember(short0);
            logger.write("Player joined admin chat", "info");
        }
    }

    public void leaveAdminChat(short short0) {
        logger.write("Player " + short0 + " are leaving admin chat...", "info");
        UdpConnection udpConnection = ChatUtility.findConnection(short0);
        if (adminChat == null) {
            logger.write("Admin chat is null. Can't leave it! ChatServer", "warning");
        } else if (udpConnection == null) {
            logger.write("Connection to player is null. Can't leave admin chat! ChatServer.leaveAdminChat", "warning");
        } else {
            adminChat.leaveMember(short0);
            tabs.get("admin").sendRemoveTabPacket(udpConnection);
            logger.write("Player " + short0 + " leaved admin chat", "info");
        }
    }

    public FactionChat createFactionChat(String string) {
        logger.write("Creating faction chat '" + string + "'", "info");
        if (factionChats.containsKey(string)) {
            logger.write("Faction chat '" + string + "' already exists!", "warning");
            return factionChats.get(string);
        } else {
            FactionChat factionChat = new FactionChat(this.getNextChatID(), tabs.get("main"));
            chats.put(factionChat.getID(), factionChat);
            factionChats.put(string, factionChat);
            logger.write("Faction chat '" + string + "' created", "info");
            return factionChat;
        }
    }

    public SafehouseChat createSafehouseChat(String string) {
        logger.write("Creating safehouse chat '" + string + "'", "info");
        if (safehouseChats.containsKey(string)) {
            logger.write("Safehouse chat already has chat with name '" + string + "'", "warning");
            return safehouseChats.get(string);
        } else {
            SafehouseChat safehouseChat = new SafehouseChat(this.getNextChatID(), tabs.get("main"));
            chats.put(safehouseChat.getID(), safehouseChat);
            safehouseChats.put(string, safehouseChat);
            logger.write("Safehouse chat '" + string + "' created", "info");
            return safehouseChat;
        }
    }

    public void removeFactionChat(String string0) {
        logger.write("Removing faction chat '" + string0 + "'...", "info");
        int int0;
        synchronized (factionChats) {
            if (!factionChats.containsKey(string0)) {
                String string1 = "Faction chat '" + string0 + "' tried to delete but it's not exists.";
                logger.write(string1, "error");
                RuntimeException runtimeException = new RuntimeException(string1);
                logger.write(runtimeException);
                throw runtimeException;
            }

            FactionChat factionChat = factionChats.get(string0);
            int0 = factionChat.getID();
            factionChats.remove(string0);
        }

        this.closeChat(int0);
        logger.write("Faction chat '" + string0 + "' removed", "info");
    }

    public void removeSafehouseChat(String string0) {
        logger.write("Removing safehouse chat '" + string0 + "'...", "info");
        int int0;
        synchronized (safehouseChats) {
            if (!safehouseChats.containsKey(string0)) {
                String string1 = "Safehouse chat '" + string0 + "' tried to delete but it's not exists.";
                logger.write(string1, "error");
                RuntimeException runtimeException = new RuntimeException(string1);
                logger.write(runtimeException);
                throw runtimeException;
            }

            SafehouseChat safehouseChat = safehouseChats.get(string0);
            int0 = safehouseChat.getID();
            safehouseChats.remove(string0);
        }

        this.closeChat(int0);
        logger.write("Safehouse chat '" + string0 + "' removed", "info");
    }

    public void syncFactionChatMembers(String string0, String string1, ArrayList<String> arrayList0) {
        logger.write("Start syncing faction chat '" + string0 + "'...", "info");
        if (string0 != null && string1 != null && arrayList0 != null) {
            synchronized (factionChats) {
                if (!factionChats.containsKey(string0)) {
                    logger.write("Faction chat '" + string0 + "' is not exist", "warning");
                    return;
                }

                ArrayList arrayList1 = new ArrayList(arrayList0);
                arrayList1.add(string1);
                FactionChat factionChat = factionChats.get(string0);
                factionChat.syncMembersByUsernames(arrayList1);
                StringBuilder stringBuilder = new StringBuilder("These members were added: ");

                for (short short0 : factionChat.getJustAddedMembers()) {
                    stringBuilder.append("'").append(ChatUtility.findPlayerName(short0)).append("', ");
                }

                stringBuilder.append(". These members were removed: ");

                for (short short1 : factionChat.getJustRemovedMembers()) {
                    stringBuilder.append("'").append(ChatUtility.findPlayerName(short1)).append("', ");
                }

                logger.write(stringBuilder.toString(), "info");
            }

            logger.write("Syncing faction chat '" + string0 + "' finished", "info");
        } else {
            logger.write("Faction name or faction owner or players is null", "warning");
        }
    }

    public void syncSafehouseChatMembers(String string0, String string1, ArrayList<String> arrayList0) {
        logger.write("Start syncing safehouse chat '" + string0 + "'...", "info");
        if (string0 != null && string1 != null && arrayList0 != null) {
            synchronized (safehouseChats) {
                if (!safehouseChats.containsKey(string0)) {
                    logger.write("Safehouse chat '" + string0 + "' is not exist", "warning");
                    return;
                }

                ArrayList arrayList1 = new ArrayList(arrayList0);
                arrayList1.add(string1);
                SafehouseChat safehouseChat = safehouseChats.get(string0);
                safehouseChat.syncMembersByUsernames(arrayList1);
                StringBuilder stringBuilder = new StringBuilder("These members were added: ");

                for (short short0 : safehouseChat.getJustAddedMembers()) {
                    stringBuilder.append("'").append(ChatUtility.findPlayerName(short0)).append("', ");
                }

                stringBuilder.append("These members were removed: ");

                for (short short1 : safehouseChat.getJustRemovedMembers()) {
                    stringBuilder.append("'").append(ChatUtility.findPlayerName(short1)).append("', ");
                }

                logger.write(stringBuilder.toString(), "info");
            }

            logger.write("Syncing safehouse chat '" + string0 + "' finished", "info");
        } else {
            logger.write("Safehouse name or Safehouse owner or players is null", "warning");
        }
    }

    private void addMemberToSafehouseChat(String string, short short0) {
        if (!safehouseChats.containsKey(string)) {
            logger.write("Safehouse chat is not initialized!", "warning");
        } else {
            synchronized (safehouseChats) {
                SafehouseChat safehouseChat = safehouseChats.get(string);
                safehouseChat.addMember(short0);
            }

            logger.write("Player joined to chat of safehouse '" + string + "'", "info");
        }
    }

    private void addMemberToFactionChat(String string, short short0) {
        if (!factionChats.containsKey(string)) {
            logger.write("Faction chat is not initialized!", "warning");
        } else {
            synchronized (factionChats) {
                FactionChat factionChat = factionChats.get(string);
                factionChat.addMember(short0);
            }

            logger.write("Player joined to chat of faction '" + string + "'", "info");
        }
    }

    public void sendServerAlertMessageToServerChat(String string0, String string1) {
        serverChat.sendMessageToChatMembers(serverChat.createMessage(string0, string1, true));
        logger.write("Server alert message: '" + string1 + "' by '" + string0 + "' sent.");
    }

    public void sendServerAlertMessageToServerChat(String string) {
        serverChat.sendMessageToChatMembers(serverChat.createServerMessage(string, true));
        logger.write("Server alert message: '" + string + "' sent.");
    }

    public ChatMessage createRadiostationMessage(String string, int int0) {
        return radioChat.createBroadcastingMessage(string, int0);
    }

    public void sendMessageToServerChat(UdpConnection udpConnection, String string) {
        ServerChatMessage serverChatMessage = serverChat.createServerMessage(string, false);
        serverChat.sendMessageToPlayer(udpConnection, serverChatMessage);
    }

    public void sendMessageToServerChat(String string) {
        ServerChatMessage serverChatMessage = serverChat.createServerMessage(string, false);
        serverChat.sendMessageToChatMembers(serverChatMessage);
    }

    public void sendMessageFromDiscordToGeneralChat(String string1, String string0) {
        if (string1 != null && string0 != null) {
            logger.write("Got message '" + string0 + "' by author '" + string1 + "' from discord");
        }

        ChatMessage chatMessage = generalChat.createMessage(string0);
        chatMessage.makeFromDiscord();
        chatMessage.setAuthor(string1);
        if (ChatUtility.chatStreamEnabled(ChatType.general)) {
            this.sendMessage(chatMessage);
            logger.write("Message '" + string0 + "' send from discord to general chat members");
        } else {
            generalChat.sendToDiscordGeneralChatDisabled();
            logger.write("General chat disabled so error message sent to discord", "warning");
        }
    }

    private int getNextChatID() {
        synchronized (availableChatsID) {
            if (availableChatsID.isEmpty()) {
                lastChatId++;
                availableChatsID.push(lastChatId);
            }

            return availableChatsID.pop();
        }
    }

    private void sendMessage(ChatMessage chatMessage) {
        synchronized (chats) {
            if (chats.containsKey(chatMessage.getChatID())) {
                ChatBase chatBase = chats.get(chatMessage.getChatID());
                chatBase.sendMessageToChatMembers(chatMessage);
            }
        }
    }

    private void sendInitPlayerChatPacket(UdpConnection udpConnection) {
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.InitPlayerChat.doPacket(byteBufferWriter);
        byteBufferWriter.putShort((short)tabs.size());

        for (ChatTab chatTab : tabs.values()) {
            byteBufferWriter.putShort(chatTab.getID());
            byteBufferWriter.putUTF(chatTab.getTitleID());
        }

        PacketTypes.PacketType.InitPlayerChat.send(udpConnection);
    }

    private void addDefaultChats(short short0) {
        for (Entry entry : defaultChats.entrySet()) {
            ChatBase chatBase = (ChatBase)entry.getValue();
            chatBase.addMember(short0);
        }
    }

    public void sendMessageToAdminChat(String string) {
        ServerChatMessage serverChatMessage = adminChat.createServerMessage(string);
        adminChat.sendMessageToChatMembers(serverChatMessage);
    }
}
