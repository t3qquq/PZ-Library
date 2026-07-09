// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.chat.ChatType;
import zombie.radio.devices.DeviceData;

public abstract class ChatBase {
    private static final int ID_NOT_SET = -29048394;
    private int id;
    private final String titleID;
    private final ChatType type;
    private ChatSettings settings;
    private boolean customSettings = false;
    private ChatTab chatTab = null;
    private String translatedTitle;
    protected final ArrayList<Short> members;
    private final ArrayList<Short> justAddedMembers = new ArrayList<>();
    private final ArrayList<Short> justRemovedMembers = new ArrayList<>();
    protected final ArrayList<ChatMessage> messages;
    private UdpConnection serverConnection;
    private ChatMode mode;
    private IsoPlayer chatOwner;
    private final Lock memberLock = new ReentrantLock();

    protected ChatBase(ChatType chatType) {
        this.settings = new ChatSettings();
        this.customSettings = false;
        this.messages = new ArrayList<>();
        this.id = -29048394;
        this.titleID = chatType.getTitleID();
        this.type = chatType;
        this.members = new ArrayList<>();
        this.mode = ChatMode.SinglePlayer;
        this.serverConnection = null;
        this.chatOwner = IsoPlayer.getInstance();
    }

    /**
     * Should called only on client side of chat system
     * 
     * @param bb package from server that describe how chat should look and work
     * @param _type meta information about chat. Many parameters depends on that
     * @param tab tab where chat should show their info
     * @param owner actual player instance
     */
    public ChatBase(ByteBuffer bb, ChatType _type, ChatTab tab, IsoPlayer owner) {
        this(_type);
        this.id = bb.getInt();
        this.customSettings = bb.get() == 1;
        if (this.customSettings) {
            this.settings = new ChatSettings(bb);
        }

        this.chatTab = tab;
        this.mode = ChatMode.ClientMultiPlayer;
        this.serverConnection = GameClient.connection;
        this.chatOwner = owner;
    }

    /**
     * Should be called only on server side of chat system
     * 
     * @param _id unique id of chat. It will be used to identify chat in client-server communication
     * @param _type meta information about chat. Many parameters depends on that
     * @param tab this tab will transferred to clients when it will connecting
     */
    public ChatBase(int _id, ChatType _type, ChatTab tab) {
        this(_type);
        this.id = _id;
        this.chatTab = tab;
        this.mode = ChatMode.ServerMultiPlayer;
    }

    public boolean isEnabled() {
        return ChatUtility.chatStreamEnabled(this.type);
    }

    protected String getChatOwnerName() {
        if (this.chatOwner == null) {
            if (this.mode != ChatMode.ServerMultiPlayer) {
                if (Core.bDebug) {
                    throw new NullPointerException("chat owner is null but name quired");
                }

                DebugLog.log("chat owner is null but name quired. Chat: " + this.getType());
            }

            return "";
        } else {
            return this.chatOwner.username;
        }
    }

    protected IsoPlayer getChatOwner() {
        if (this.chatOwner != null || this.mode == ChatMode.ServerMultiPlayer) {
            return this.chatOwner;
        } else if (Core.bDebug) {
            throw new NullPointerException("chat owner is null");
        } else {
            DebugLog.log("chat owner is null. Chat: " + this.getType());
            return null;
        }
    }

    public ChatMode getMode() {
        return this.mode;
    }

    public ChatType getType() {
        return this.type;
    }

    public int getID() {
        return this.id;
    }

    public String getTitleID() {
        return this.titleID;
    }

    public Color getColor() {
        return this.settings.getFontColor();
    }

    public short getTabID() {
        return this.chatTab.getID();
    }

    public float getRange() {
        return this.settings.getRange();
    }

    public boolean isSendingToRadio() {
        return false;
    }

    public float getZombieAttractionRange() {
        return this.settings.getZombieAttractionRange();
    }

    public void setSettings(ChatSettings _settings) {
        this.settings = _settings;
        this.customSettings = true;
    }

    public void setFontSize(String fontSize) {
        this.settings.setFontSize(fontSize.toLowerCase());
    }

    public void setShowTimestamp(boolean showTimestamp) {
        this.settings.setShowTimestamp(showTimestamp);
    }

    public void setShowTitle(boolean showTitle) {
        this.settings.setShowChatTitle(showTitle);
    }

    protected boolean isCustomSettings() {
        return this.customSettings;
    }

    protected boolean isAllowImages() {
        return this.settings.isAllowImages();
    }

    protected boolean isAllowChatIcons() {
        return this.settings.isAllowChatIcons();
    }

    protected boolean isAllowColors() {
        return this.settings.isAllowColors();
    }

    protected boolean isAllowFonts() {
        return this.settings.isAllowFonts();
    }

    protected boolean isAllowBBcode() {
        return this.settings.isAllowBBcode();
    }

    protected boolean isEqualizeLineHeights() {
        return this.settings.isEqualizeLineHeights();
    }

    protected boolean isShowAuthor() {
        return this.settings.isShowAuthor();
    }

    protected boolean isShowTimestamp() {
        return this.settings.isShowTimestamp();
    }

    protected boolean isShowTitle() {
        return this.settings.isShowChatTitle();
    }

    protected String getFontSize() {
        return this.settings.getFontSize().toString();
    }

    protected String getTitle() {
        if (this.translatedTitle == null) {
            this.translatedTitle = Translator.getText(this.titleID);
        }

        return this.translatedTitle;
    }

    public void close() {
        synchronized (this.memberLock) {
            for (Short short0 : new ArrayList<>(this.members)) {
                this.leaveMember(short0);
            }

            this.members.clear();
        }
    }

    protected void packChat(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.type.getValue());
        byteBufferWriter.putShort(this.getTabID());
        byteBufferWriter.putInt(this.id);
        byteBufferWriter.putBoolean(this.customSettings);
        if (this.customSettings) {
            this.settings.pack(byteBufferWriter);
        }
    }

    public ChatMessage unpackMessage(ByteBuffer bb) {
        String string0 = GameWindow.ReadString(bb);
        String string1 = GameWindow.ReadString(bb);
        ChatMessage chatMessage = this.createMessage(string1);
        chatMessage.setAuthor(string0);
        return chatMessage;
    }

    public void packMessage(ByteBufferWriter b, ChatMessage msg) {
        b.putInt(this.id);
        b.putUTF(msg.getAuthor());
        b.putUTF(msg.getText());
    }

    /**
     * Message creator. Every chat know how to create its own message
     * 
     * @param text text of the message
     * @return corresponding object to message
     */
    public ChatMessage createMessage(String text) {
        return this.createMessage(this.getChatOwnerName(), text);
    }

    private ChatMessage createMessage(String string1, String string0) {
        ChatMessage chatMessage = new ChatMessage(this, string0);
        chatMessage.setAuthor(string1);
        chatMessage.setServerAuthor(false);
        return chatMessage;
    }

    public ServerChatMessage createServerMessage(String text) {
        ServerChatMessage serverChatMessage = new ServerChatMessage(this, text);
        serverChatMessage.setServerAuthor(true);
        return serverChatMessage;
    }

    public void showMessage(String text, String author) {
        ChatMessage chatMessage = new ChatMessage(this, LocalDateTime.now(), text);
        chatMessage.setAuthor(author);
        this.showMessage(chatMessage);
    }

    public void showMessage(ChatMessage msg) {
        this.messages.add(msg);
        if (this.isEnabled() && msg.isShowInChat() && this.chatTab != null) {
            LuaEventManager.triggerEvent("OnAddMessage", msg, this.getTabID());
        }
    }

    public String getMessageTextWithPrefix(ChatMessage msg) {
        return this.getMessagePrefix(msg) + " " + msg.getTextWithReplacedParentheses();
    }

    public void sendMessageToChatMembers(ChatMessage msg) {
        IsoPlayer player0 = ChatUtility.findPlayer(msg.getAuthor());
        if (player0 == null) {
            DebugLog.log("Author '" + msg.getAuthor() + "' not found");
        } else {
            synchronized (this.memberLock) {
                for (short short0 : this.members) {
                    IsoPlayer player1 = ChatUtility.findPlayer(short0);
                    if (player1 != null && player0.getOnlineID() != short0) {
                        this.sendMessageToPlayer(short0, msg);
                    }
                }
            }

            if (Core.bDebug) {
                DebugLog.log("New message '" + msg + "' was sent members of chat '" + this.getID() + "'");
            }
        }
    }

    public void sendMessageToChatMembers(ServerChatMessage msg) {
        synchronized (this.memberLock) {
            for (short short0 : this.members) {
                IsoPlayer player = ChatUtility.findPlayer(short0);
                if (player != null) {
                    this.sendMessageToPlayer(short0, msg);
                }
            }
        }

        if (Core.bDebug) {
            DebugLog.log("New message '" + msg + "' was sent members of chat '" + this.getID() + "'");
        }
    }

    public void sendMessageToPlayer(UdpConnection connection, ChatMessage msg) {
        synchronized (this.memberLock) {
            boolean boolean0 = false;
            short[] shorts = connection.playerIDs;
            int int0 = shorts.length;

            for (int int1 = 0; int1 < int0; int1++) {
                Short short0 = shorts[int1];
                if (boolean0) {
                    break;
                }

                boolean0 = this.members.contains(short0);
            }

            if (!boolean0) {
                throw new RuntimeException("Passed connection didn't contained member of chat");
            } else {
                this.sendChatMessageToPlayer(connection, msg);
            }
        }
    }

    public void sendMessageToPlayer(short playerID, ChatMessage msg) {
        UdpConnection udpConnection = ChatUtility.findConnection(playerID);
        if (udpConnection != null) {
            this.sendChatMessageToPlayer(udpConnection, msg);
            DebugLog.log("Message '" + msg + "' was sent to player with id '" + playerID + "' of chat '" + this.getID() + "'");
        }
    }

    public String getMessagePrefix(ChatMessage msg) {
        StringBuilder stringBuilder = new StringBuilder(this.getChatSettingsTags());
        if (this.isShowTimestamp()) {
            stringBuilder.append("[").append(LuaManager.getHourMinuteJava()).append("]");
        }

        if (this.isShowTitle()) {
            stringBuilder.append("[").append(this.getTitle()).append("]");
        }

        if (this.isShowAuthor()) {
            stringBuilder.append("[").append(msg.getAuthor()).append("]");
        }

        stringBuilder.append(": ");
        return stringBuilder.toString();
    }

    protected String getColorTag() {
        Color color = this.getColor();
        return this.getColorTag(color);
    }

    protected String getColorTag(Color color) {
        return "<RGB:" + color.r + "," + color.g + "," + color.b + ">";
    }

    protected String getFontSizeTag() {
        return "<SIZE:" + this.settings.getFontSize() + ">";
    }

    protected String getChatSettingsTags() {
        return this.getColorTag() + " " + this.getFontSizeTag() + " ";
    }

    public void addMember(short playerID) {
        synchronized (this.memberLock) {
            if (!this.hasMember(playerID)) {
                this.members.add(playerID);
                this.justAddedMembers.add(playerID);
                UdpConnection udpConnection = ChatUtility.findConnection(playerID);
                if (udpConnection != null) {
                    this.sendPlayerJoinChatPacket(udpConnection);
                    this.chatTab.sendAddTabPacket(udpConnection);
                } else if (Core.bDebug) {
                    throw new RuntimeException("Connection should exist!");
                }
            }
        }
    }

    public void leaveMember(Short playerID) {
        synchronized (this.memberLock) {
            if (this.hasMember(playerID)) {
                this.justRemovedMembers.add(playerID);
                UdpConnection udpConnection = ChatUtility.findConnection(playerID);
                if (udpConnection != null) {
                    this.sendPlayerLeaveChatPacket(udpConnection);
                }

                this.members.remove(playerID);
            }
        }
    }

    private boolean hasMember(Short short0) {
        return this.members.contains(short0);
    }

    public void removeMember(Short playerID) {
        synchronized (this.memberLock) {
            if (this.hasMember(playerID)) {
                this.members.remove(playerID);
            }
        }
    }

    public void syncMembersByUsernames(ArrayList<String> players) {
        synchronized (this.memberLock) {
            this.justAddedMembers.clear();
            this.justRemovedMembers.clear();
            ArrayList arrayList = new ArrayList(players.size());
            Object object = null;

            for (String string : players) {
                object = ChatUtility.findPlayer(string);
                if (object != null) {
                    arrayList.add(((IsoPlayer)object).getOnlineID());
                }
            }

            this.syncMembers(arrayList);
        }
    }

    public ArrayList<Short> getJustAddedMembers() {
        synchronized (this.memberLock) {
            return this.justAddedMembers;
        }
    }

    public ArrayList<Short> getJustRemovedMembers() {
        synchronized (this.memberLock) {
            return this.justRemovedMembers;
        }
    }

    private void syncMembers(ArrayList<Short> arrayList0) {
        for (Short short0 : arrayList0) {
            this.addMember(short0);
        }

        ArrayList arrayList1 = new ArrayList();
        synchronized (this.memberLock) {
            for (Short short1 : this.members) {
                if (!arrayList0.contains(short1)) {
                    arrayList1.add(short1);
                }
            }

            for (Short short2 : arrayList1) {
                this.leaveMember(short2);
            }
        }
    }

    public void sendPlayerJoinChatPacket(UdpConnection playerConnection) {
        ByteBufferWriter byteBufferWriter = playerConnection.startPacket();
        PacketTypes.PacketType.PlayerJoinChat.doPacket(byteBufferWriter);
        this.packChat(byteBufferWriter);
        PacketTypes.PacketType.PlayerJoinChat.send(playerConnection);
    }

    public void sendPlayerLeaveChatPacket(short playerID) {
        UdpConnection udpConnection = ChatUtility.findConnection(playerID);
        this.sendPlayerLeaveChatPacket(udpConnection);
    }

    public void sendPlayerLeaveChatPacket(UdpConnection connection) {
        ByteBufferWriter byteBufferWriter = connection.startPacket();
        PacketTypes.PacketType.PlayerLeaveChat.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(this.getID());
        byteBufferWriter.putInt(this.getType().getValue());
        PacketTypes.PacketType.PlayerLeaveChat.send(connection);
    }

    public void sendToServer(ChatMessage msg, DeviceData deviceData) {
        if (this.serverConnection == null) {
            DebugLog.log("Connection to server is null in client chat");
        }

        this.sendChatMessageFromPlayer(this.serverConnection, msg);
    }

    private void sendChatMessageToPlayer(UdpConnection udpConnection, ChatMessage chatMessage) {
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.ChatMessageToPlayer.doPacket(byteBufferWriter);
        this.packMessage(byteBufferWriter, chatMessage);
        PacketTypes.PacketType.ChatMessageToPlayer.send(udpConnection);
    }

    private void sendChatMessageFromPlayer(UdpConnection udpConnection, ChatMessage chatMessage) {
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.ChatMessageFromPlayer.doPacket(byteBufferWriter);
        this.packMessage(byteBufferWriter, chatMessage);
        PacketTypes.PacketType.ChatMessageFromPlayer.send(udpConnection);
    }

    protected boolean hasChatTab() {
        return this.chatTab != null;
    }
}
