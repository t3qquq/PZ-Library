// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatMessage;
import zombie.chat.ChatSettings;
import zombie.chat.ChatTab;
import zombie.chat.ChatUtility;
import zombie.core.Color;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.network.chat.ChatType;

public class GeneralChat extends ChatBase {
    private boolean discordEnabled = false;
    private final Color discordMessageColor = new Color(114, 137, 218);

    public GeneralChat(ByteBuffer byteBuffer, ChatTab chatTab, IsoPlayer player) {
        super(byteBuffer, ChatType.general, chatTab, player);
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }

    public GeneralChat(int int0, ChatTab chatTab, boolean boolean0) {
        super(int0, ChatType.general, chatTab);
        this.discordEnabled = boolean0;
        if (!this.isCustomSettings()) {
            this.setSettings(getDefaultSettings());
        }
    }

    public GeneralChat() {
        super(ChatType.general);
    }

    public static ChatSettings getDefaultSettings() {
        ChatSettings chatSettings = new ChatSettings();
        chatSettings.setBold(true);
        chatSettings.setFontColor(new Color(255, 165, 0));
        chatSettings.setShowAuthor(true);
        chatSettings.setShowChatTitle(true);
        chatSettings.setShowTimestamp(true);
        chatSettings.setUnique(true);
        chatSettings.setAllowColors(true);
        chatSettings.setAllowFonts(true);
        chatSettings.setAllowBBcode(true);
        return chatSettings;
    }

    @Override
    public void sendMessageToChatMembers(ChatMessage chatMessage) {
        if (this.discordEnabled) {
            IsoPlayer player = ChatUtility.findPlayer(chatMessage.getAuthor());
            if (chatMessage.isFromDiscord()) {
                for (short short0 : this.members) {
                    this.sendMessageToPlayer(short0, chatMessage);
                }
            } else {
                GameServer.discordBot.sendMessage(chatMessage.getAuthor(), chatMessage.getText());

                for (short short1 : this.members) {
                    if (player == null || player.getOnlineID() != short1) {
                        this.sendMessageToPlayer(short1, chatMessage);
                    }
                }
            }
        } else {
            super.sendMessageToChatMembers(chatMessage);
        }

        DebugLog.log("New message '" + chatMessage + "' was sent members of chat '" + this.getID() + "'");
    }

    public void sendToDiscordGeneralChatDisabled() {
        GameServer.discordBot.sendMessage("Server", Translator.getText("UI_chat_general_chat_disabled"));
    }

    @Override
    public String getMessagePrefix(ChatMessage chatMessage) {
        StringBuilder stringBuilder = new StringBuilder();
        if (chatMessage.isFromDiscord()) {
            stringBuilder.append(this.getColorTag(this.discordMessageColor));
        } else {
            stringBuilder.append(this.getColorTag());
        }

        stringBuilder.append(" ").append(this.getFontSizeTag()).append(" ");
        if (this.isShowTimestamp()) {
            stringBuilder.append("[").append(LuaManager.getHourMinuteJava()).append("]");
        }

        if (this.isShowTitle()) {
            stringBuilder.append("[").append(this.getTitle()).append("]");
        }

        if (this.isShowAuthor()) {
            stringBuilder.append("[").append(chatMessage.getAuthor()).append("]");
        }

        stringBuilder.append(": ");
        return stringBuilder.toString();
    }

    @Override
    public void packMessage(ByteBufferWriter byteBufferWriter, ChatMessage chatMessage) {
        super.packMessage(byteBufferWriter, chatMessage);
        byteBufferWriter.putBoolean(chatMessage.isFromDiscord());
    }

    @Override
    public ChatMessage unpackMessage(ByteBuffer byteBuffer) {
        ChatMessage chatMessage = super.unpackMessage(byteBuffer);
        if (byteBuffer.get() == 1) {
            chatMessage.makeFromDiscord();
        }

        return chatMessage;
    }
}
