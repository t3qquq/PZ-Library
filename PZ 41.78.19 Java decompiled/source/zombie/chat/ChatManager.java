// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.characters.Faction;
import zombie.characters.IsoPlayer;
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
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.VoiceManagerData;
import zombie.debug.DebugLog;
import zombie.inventory.types.Radio;
import zombie.iso.areas.SafeHouse;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.chat.ChatType;
import zombie.radio.devices.DeviceData;

public class ChatManager {
    private static ChatManager instance = null;
    private UdpConnection serverConnection = null;
    private final HashMap<Integer, ChatBase> mpChats;
    private final HashMap<String, WhisperChat> whisperChats;
    private final HashMap<String, WhisperChatCreation> whisperChatCreation = new HashMap<>();
    private final HashMap<Short, ChatTab> tabs;
    private ChatTab focusTab;
    private IsoPlayer player;
    private String myNickname;
    private boolean singlePlayerMode = false;
    private GeneralChat generalChat = null;
    private SayChat sayChat = null;
    private ShoutChat shoutChat = null;
    private FactionChat factionChat = null;
    private SafehouseChat safehouseChat = null;
    private RadioChat radioChat = null;
    private AdminChat adminChat = null;
    private ServerChat serverChat = null;
    private ChatManager.Stage chatManagerStage = ChatManager.Stage.notStarted;
    private static volatile ZLogger logger;
    private static final String logNamePrefix = "client chat";

    private ChatManager() {
        this.mpChats = new HashMap<>();
        this.tabs = new HashMap<>();
        this.whisperChats = new HashMap<>();
    }

    public static ChatManager getInstance() {
        if (instance == null) {
            instance = new ChatManager();
        }

        return instance;
    }

    public boolean isSinglePlayerMode() {
        return this.singlePlayerMode;
    }

    public boolean isWorking() {
        return this.chatManagerStage == ChatManager.Stage.working;
    }

    public void init(boolean boolean0, IsoPlayer playerx) {
        LoggerManager.init();
        LoggerManager.createLogger("client chat " + playerx.getDisplayName(), Core.bDebug);
        logger = LoggerManager.getLogger("client chat " + playerx.getDisplayName());
        logger.write("Init chat system...", "info");
        logger.write("Mode: " + (boolean0 ? "single player" : "multiplayer"), "info");
        logger.write("Chat owner: " + playerx.getDisplayName(), "info");
        this.chatManagerStage = ChatManager.Stage.starting;
        this.singlePlayerMode = boolean0;
        this.generalChat = null;
        this.sayChat = null;
        this.shoutChat = null;
        this.factionChat = null;
        this.safehouseChat = null;
        this.radioChat = null;
        this.adminChat = null;
        this.serverChat = null;
        this.mpChats.clear();
        this.tabs.clear();
        this.focusTab = null;
        this.whisperChats.clear();
        this.player = playerx;
        this.myNickname = this.player.username;
        if (boolean0) {
            this.serverConnection = null;
            this.sayChat = new SayChat();
            this.sayChat.Init();
            this.generalChat = new GeneralChat();
            this.shoutChat = new ShoutChat();
            this.shoutChat.Init();
            this.radioChat = new RadioChat();
            this.radioChat.Init();
            this.adminChat = new AdminChat();
        } else {
            this.serverConnection = GameClient.connection;
            LuaEventManager.triggerEvent("OnChatWindowInit");
        }
    }

    public void processInitPlayerChatPacket(ByteBuffer byteBuffer) {
        this.init(false, IsoPlayer.getInstance());
        short short0 = byteBuffer.getShort();

        for (int int0 = 0; int0 < short0; int0++) {
            ChatTab chatTab = new ChatTab(byteBuffer.getShort(), GameWindow.ReadString(byteBuffer));
            this.tabs.put(chatTab.getID(), chatTab);
        }

        this.addTab((short)0);
        this.focusOnTab(this.tabs.get((short)0).getID());
        LuaEventManager.triggerEvent("OnSetDefaultTab", this.tabs.get((short)0).getTitle());
    }

    public void setFullyConnected() {
        this.chatManagerStage = ChatManager.Stage.working;
    }

    public void processAddTabPacket(ByteBuffer byteBuffer) {
        this.addTab(byteBuffer.getShort());
    }

    public void processRemoveTabPacket(ByteBuffer byteBuffer) {
        this.removeTab(byteBuffer.getShort());
    }

    public void processJoinChatPacket(ByteBuffer byteBuffer) {
        ChatType chatType = ChatType.valueOf(byteBuffer.getInt());
        ChatTab chatTab = this.tabs.get(byteBuffer.getShort());
        Object object = null;
        switch (chatType) {
            case general:
                this.generalChat = new GeneralChat(byteBuffer, chatTab, this.player);
                object = this.generalChat;
                break;
            case say:
                this.sayChat = new SayChat(byteBuffer, chatTab, this.player);
                this.sayChat.Init();
                object = this.sayChat;
                break;
            case shout:
                this.shoutChat = new ShoutChat(byteBuffer, chatTab, this.player);
                this.shoutChat.Init();
                object = this.shoutChat;
                break;
            case whisper:
                WhisperChat whisperChat = new WhisperChat(byteBuffer, chatTab, this.player);
                whisperChat.init();
                this.whisperChats.put(whisperChat.getCompanionName(), whisperChat);
                object = whisperChat;
                break;
            case faction:
                this.factionChat = new FactionChat(byteBuffer, chatTab, this.player);
                object = this.factionChat;
                break;
            case safehouse:
                this.safehouseChat = new SafehouseChat(byteBuffer, chatTab, this.player);
                object = this.safehouseChat;
                break;
            case radio:
                this.radioChat = new RadioChat(byteBuffer, chatTab, this.player);
                this.radioChat.Init();
                object = this.radioChat;
                break;
            case admin:
                this.adminChat = new AdminChat(byteBuffer, chatTab, this.player);
                object = this.adminChat;
                break;
            case server:
                this.serverChat = new ServerChat(byteBuffer, chatTab, this.player);
                object = this.serverChat;
                break;
            default:
                DebugLog.log("Chat of type '" + chatType.toString() + "' is not supported to join to");
                return;
        }

        this.mpChats.put(((ChatBase)object).getID(), (ChatBase)object);
        ((ChatBase)object).setFontSize(Core.getInstance().getOptionChatFontSize());
        ((ChatBase)object).setShowTimestamp(Core.getInstance().isOptionShowChatTimestamp());
        ((ChatBase)object).setShowTitle(Core.getInstance().isOptionShowChatTitle());
    }

    public void processLeaveChatPacket(ByteBuffer byteBuffer) {
        Integer integer = byteBuffer.getInt();
        ChatType chatType = ChatType.valueOf(byteBuffer.getInt());
        switch (chatType) {
            case general:
            case say:
            case shout:
            case radio:
            case server:
                DebugLog.log("Chat type is '" + chatType.toString() + "'. Can't leave it. Ignored.");
                break;
            case whisper:
                this.whisperChats.remove(((WhisperChat)this.mpChats.get(integer)).getCompanionName());
                this.mpChats.remove(integer);
                break;
            case faction:
                this.mpChats.remove(integer);
                this.factionChat = null;
                DebugLog.log("You leaved faction chat");
                break;
            case safehouse:
                this.mpChats.remove(integer);
                this.safehouseChat = null;
                DebugLog.log("You leaved safehouse chat");
                break;
            case admin:
                this.mpChats.remove(integer);
                this.removeTab(this.adminChat.getTabID());
                this.adminChat = null;
                DebugLog.log("You leaved admin chat");
                break;
            default:
                DebugLog.log("Chat of type '" + chatType.toString() + "' is not supported to leave to");
        }
    }

    public void processPlayerNotFound(String string) {
        logger.write("Got player not found packet", "info");
        WhisperChatCreation whisperChatCreationx = this.whisperChatCreation.get(string);
        if (whisperChatCreationx != null) {
            whisperChatCreationx.status = WhisperChat.ChatStatus.PlayerNotFound;
        }
    }

    public ChatMessage unpackMessage(ByteBuffer byteBuffer) {
        int int0 = byteBuffer.getInt();
        ChatBase chatBase = this.mpChats.get(int0);
        return chatBase.unpackMessage(byteBuffer);
    }

    public void processChatMessagePacket(ByteBuffer byteBuffer) {
        ChatMessage chatMessage = this.unpackMessage(byteBuffer);
        ChatBase chatBase = chatMessage.getChat();
        if (ChatUtility.chatStreamEnabled(chatBase.getType())) {
            chatBase.showMessage(chatMessage);
            logger.write("Got message from server: " + chatMessage, "info");
        } else {
            DebugLog.log("Can't process message '" + chatMessage.getText() + "' because '" + chatBase.getType() + "' chat is disabled");
            logger.write("Can't process message '" + chatMessage.getText() + "' because '" + chatBase.getType() + "' chat is disabled", "warning");
        }
    }

    public void updateChatSettings(String string, boolean boolean0, boolean boolean1) {
        Core.getInstance().setOptionChatFontSize(string);
        Core.getInstance().setOptionShowChatTimestamp(boolean0);
        Core.getInstance().setOptionShowChatTitle(boolean1);

        for (ChatBase chatBase : this.mpChats.values()) {
            chatBase.setFontSize(string);
            chatBase.setShowTimestamp(boolean0);
            chatBase.setShowTitle(boolean1);
        }
    }

    public void showInfoMessage(String string) {
        ChatMessage chatMessage = this.sayChat.createInfoMessage(string);
        this.sayChat.showMessage(chatMessage);
    }

    public void showInfoMessage(String string1, String string0) {
        if (this.sayChat != null) {
            ChatMessage chatMessage = this.sayChat.createInfoMessage(string0);
            chatMessage.setAuthor(string1);
            this.sayChat.showMessage(chatMessage);
        }
    }

    public void sendMessageToChat(String string1, ChatType chatType, String string0) {
        string0 = string0.trim();
        if (!string0.isEmpty()) {
            ChatBase chatBase = this.getChat(chatType);
            if (chatBase == null) {
                if (Core.bDebug) {
                    throw new IllegalArgumentException("Chat '" + chatType + "' is null. Chat should be init before use!");
                } else {
                    this.showChatDisabledMessage(chatType);
                }
            } else {
                ChatMessage chatMessage = chatBase.createMessage(string0);
                chatMessage.setAuthor(string1);
                this.sendMessageToChat(chatBase, chatMessage);
            }
        }
    }

    public void sendMessageToChat(ChatType chatType, String string) {
        this.sendMessageToChat(this.player.getUsername(), chatType, string);
    }

    public synchronized void sendWhisperMessage(String string0, String string1) {
        logger.write("Send message '" + string1 + "' for player '" + string0 + "' in whisper chat", "info");
        if (ChatUtility.chatStreamEnabled(ChatType.whisper)) {
            if (string0 == null || string0.equalsIgnoreCase(this.myNickname)) {
                logger.write("Message can't be send to yourself");
                this.showServerChatMessage(Translator.getText("UI_chat_whisper_message_to_yourself_error"));
                return;
            }

            if (this.whisperChats.containsKey(string0)) {
                WhisperChat whisperChat = this.whisperChats.get(string0);
                this.sendMessageToChat(whisperChat, whisperChat.createMessage(string1));
                return;
            }

            if (this.whisperChatCreation.containsKey(string0)) {
                WhisperChatCreation whisperChatCreation0 = this.whisperChatCreation.get(string0);
                whisperChatCreation0.messages.add(string1);
                return;
            }

            WhisperChatCreation whisperChatCreation1 = this.createWhisperChat(string0);
            whisperChatCreation1.messages.add(string1);
        } else {
            logger.write("Whisper chat is disabled", "info");
            this.showChatDisabledMessage(ChatType.whisper);
        }
    }

    public Boolean isPlayerCanUseChat(ChatType chatType) {
        if (!ChatUtility.chatStreamEnabled(chatType)) {
            return false;
        } else {
            switch (chatType) {
                case faction:
                    return Faction.isAlreadyInFaction(this.player);
                case safehouse:
                    return SafeHouse.hasSafehouse(this.player) != null;
                case radio:
                    return this.isPlayerCanUseRadioChat();
                case admin:
                    return this.player.isAccessLevel("admin");
                default:
                    return true;
            }
        }
    }

    public void focusOnTab(Short short0) {
        for (ChatTab chatTab : this.tabs.values()) {
            if (chatTab.getID() == short0) {
                this.focusTab = chatTab;
                return;
            }
        }

        throw new RuntimeException("Tab with id = '" + short0 + "' not found");
    }

    public String getTabName(short short0) {
        return this.tabs.containsKey(short0) ? this.tabs.get(short0).getTitle() : Short.toString(short0);
    }

    public ChatTab getFocusTab() {
        return this.focusTab;
    }

    public void showRadioMessage(ChatMessage chatMessage) {
        this.radioChat.showMessage(chatMessage);
    }

    public void showRadioMessage(String string, int int0) {
        ChatMessage chatMessage = this.radioChat.createMessage(string);
        if (int0 != 0) {
            chatMessage.setRadioChannel(int0);
        }

        this.radioChat.showMessage(chatMessage);
    }

    public void showStaticRadioSound(String string) {
        this.radioChat.showMessage(this.radioChat.createStaticSoundMessage(string));
    }

    public ChatMessage createRadiostationMessage(String string, int int0) {
        return this.radioChat.createBroadcastingMessage(string, int0);
    }

    public void showServerChatMessage(String string) {
        ServerChatMessage serverChatMessage = this.serverChat.createServerMessage(string);
        this.serverChat.showMessage(serverChatMessage);
    }

    private void addMessage(int int0, String string1, String string0) {
        ChatBase chatBase = this.mpChats.get(int0);
        chatBase.showMessage(string0, string1);
    }

    public void addMessage(String string0, String string1) throws RuntimeException {
        if (this.generalChat == null) {
            throw new RuntimeException();
        } else {
            this.addMessage(this.generalChat.getID(), string0, string1);
        }
    }

    private void sendMessageToChat(ChatBase chatBase, ChatMessage chatMessage0) {
        if (chatBase.getType() == ChatType.radio) {
            if (Core.bDebug) {
                throw new IllegalArgumentException("You can't send message to radio directly. Use radio and send say message");
            } else {
                DebugLog.log("You try to use radio chat directly. It's restricted. Try to use say chat");
            }
        } else {
            chatBase.showMessage(chatMessage0);
            if (chatBase.isEnabled()) {
                if (!this.isSinglePlayerMode() && !chatMessage0.isLocal()) {
                    DeviceData deviceData = this.getTransmittingRadio();
                    chatBase.sendToServer(chatMessage0, deviceData);
                    if (deviceData != null && chatBase.isSendingToRadio()) {
                        ChatMessage chatMessage1 = this.radioChat.createMessage(chatMessage0.getText());
                        chatMessage1.setRadioChannel(deviceData.getChannel());
                        this.radioChat.sendToServer(chatMessage1, deviceData);
                    }
                }
            } else {
                this.showChatDisabledMessage(chatBase.getType());
            }
        }
    }

    private ChatBase getChat(ChatType chatType) {
        if (chatType == ChatType.whisper) {
            throw new IllegalArgumentException("Whisper not unique chat");
        } else {
            switch (chatType) {
                case general:
                    return this.generalChat;
                case say:
                    return this.sayChat;
                case shout:
                    return this.shoutChat;
                case whisper:
                default:
                    throw new IllegalArgumentException("Chat type is undefined");
                case faction:
                    return this.factionChat;
                case safehouse:
                    return this.safehouseChat;
                case radio:
                    return this.radioChat;
                case admin:
                    return this.adminChat;
                case server:
                    return this.serverChat;
            }
        }
    }

    private void addTab(short short0) {
        ChatTab chatTab = this.tabs.get(short0);
        if (!chatTab.isEnabled()) {
            chatTab.setEnabled(true);
            LuaEventManager.triggerEvent("OnTabAdded", chatTab.getTitle(), chatTab.getID());
        }
    }

    private void removeTab(Short short0) {
        ChatTab chatTab = this.tabs.get(short0);
        if (chatTab.isEnabled()) {
            LuaEventManager.triggerEvent("OnTabRemoved", chatTab.getTitle(), chatTab.getID());
            chatTab.setEnabled(false);
        }
    }

    private WhisperChatCreation createWhisperChat(String string) {
        logger.write("Whisper chat is not created for '" + string + "'", "info");
        WhisperChatCreation whisperChatCreationx = new WhisperChatCreation();
        whisperChatCreationx.destPlayerName = string;
        whisperChatCreationx.status = WhisperChat.ChatStatus.Creating;
        whisperChatCreationx.createTime = System.currentTimeMillis();
        this.whisperChatCreation.put(string, whisperChatCreationx);
        ByteBufferWriter byteBufferWriter = this.serverConnection.startPacket();
        PacketTypes.PacketType.PlayerStartPMChat.doPacket(byteBufferWriter);
        byteBufferWriter.putUTF(this.myNickname);
        byteBufferWriter.putUTF(string);
        PacketTypes.PacketType.PlayerStartPMChat.send(this.serverConnection);
        logger.write("'Start PM chat' package sent. Waiting for a creating whisper chat by server...", "info");
        return whisperChatCreationx;
    }

    public static void UpdateClient() {
        if (instance != null) {
            try {
                instance.updateClient();
            } catch (Throwable throwable) {
                ExceptionLogger.logException(throwable);
            }
        }
    }

    private void updateClient() {
        if (this.isWorking()) {
            this.updateWhisperChat();
        }
    }

    private void updateWhisperChat() {
        if (!this.whisperChatCreation.isEmpty()) {
            long long0 = System.currentTimeMillis();

            for (WhisperChatCreation whisperChatCreationx : new ArrayList<>(this.whisperChatCreation.values())) {
                if (this.whisperChats.containsKey(whisperChatCreationx.destPlayerName)) {
                    WhisperChat whisperChat = this.whisperChats.get(whisperChatCreationx.destPlayerName);
                    logger.write(
                        "Whisper chat created between '"
                            + this.myNickname
                            + "' and '"
                            + whisperChatCreationx.destPlayerName
                            + "' and has id = "
                            + whisperChat.getID(),
                        "info"
                    );
                    this.whisperChatCreation.remove(whisperChatCreationx.destPlayerName);

                    for (String string : whisperChatCreationx.messages) {
                        this.sendMessageToChat(whisperChat, whisperChat.createMessage(string));
                    }
                } else if (whisperChatCreationx.status == WhisperChat.ChatStatus.PlayerNotFound) {
                    logger.write("Player '" + whisperChatCreationx.destPlayerName + "' is not found. Chat is not created", "info");
                    this.whisperChatCreation.remove(whisperChatCreationx.destPlayerName);
                    this.showServerChatMessage(Translator.getText("UI_chat_whisper_player_not_found_error", whisperChatCreationx.destPlayerName));
                } else if (whisperChatCreationx.status == WhisperChat.ChatStatus.Creating && long0 - whisperChatCreationx.createTime >= 10000L) {
                    logger.write("Whisper chat is not created by timeout. See server chat logs", "error");
                    this.whisperChatCreation.remove(whisperChatCreationx.destPlayerName);
                }
            }
        }
    }

    private void showChatDisabledMessage(ChatType chatType0) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Translator.getText("UI_chat_chat_disabled_msg", Translator.getText(chatType0.getTitleID())));

        for (ChatType chatType1 : ChatUtility.getAllowedChatStreams()) {
            if (this.isPlayerCanUseChat(chatType1)) {
                stringBuilder.append("    * ").append(Translator.getText(chatType1.getTitleID())).append(" <LINE> ");
            }
        }

        this.showServerChatMessage(stringBuilder.toString());
    }

    private boolean isPlayerCanUseRadioChat() {
        Radio radio = this.player.getEquipedRadio();
        if (radio != null && radio.getDeviceData() != null) {
            boolean boolean0 = radio.getDeviceData().getIsTurnedOn();
            boolean0 &= radio.getDeviceData().getIsTwoWay();
            boolean0 &= radio.getDeviceData().getIsPortable();
            return boolean0 & !radio.getDeviceData().getMicIsMuted();
        } else {
            return false;
        }
    }

    private DeviceData getTransmittingRadio() {
        if (this.player.getOnlineID() == -1) {
            return null;
        } else {
            VoiceManagerData voiceManagerData = VoiceManagerData.get(this.player.getOnlineID());
            synchronized (voiceManagerData.radioData) {
                return voiceManagerData.radioData
                    .stream()
                    .filter(VoiceManagerData.RadioData::isTransmissionAvailable)
                    .findFirst()
                    .map(VoiceManagerData.RadioData::getDeviceData)
                    .orElse(null);
            }
        }
    }

    private static enum Stage {
        notStarted,
        starting,
        working;
    }
}
