// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.chat.ChatSettings;
import zombie.chat.ChatTab;
import zombie.chat.ServerChatMessage;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.chat.ChatType;

public class ServerChat extends ChatBase {
    public ServerChat(ByteBuffer byteBuffer, ChatTab chatTab, IsoPlayer player) {
        super(byteBuffer, ChatType.server, chatTab, player);
        this.setSettings(getDefaultSettings());
    }

    public ServerChat(int int0, ChatTab chatTab) {
        super(int0, ChatType.server, chatTab);
        this.setSettings(getDefaultSettings());
    }

    public static ChatSettings getDefaultSettings() {
        ChatSettings chatSettings = new ChatSettings();
        chatSettings.setBold(true);
        chatSettings.setFontColor(new Color(0, 128, 255, 255));
        chatSettings.setShowAuthor(false);
        chatSettings.setShowChatTitle(true);
        chatSettings.setShowTimestamp(false);
        chatSettings.setAllowColors(true);
        chatSettings.setAllowFonts(false);
        chatSettings.setAllowBBcode(false);
        return chatSettings;
    }

    public ChatMessage createMessage(String string1, String string0, boolean boolean0) {
        ChatMessage chatMessage = this.createMessage(string0);
        chatMessage.setAuthor(string1);
        if (boolean0) {
            chatMessage.setServerAlert(true);
        }

        return chatMessage;
    }

    public ServerChatMessage createServerMessage(String string, boolean boolean0) {
        ServerChatMessage serverChatMessage = this.createServerMessage(string);
        serverChatMessage.setServerAlert(boolean0);
        return serverChatMessage;
    }

    @Override
    public short getTabID() {
        return !GameClient.bClient ? super.getTabID() : ChatManager.getInstance().getFocusTab().getID();
    }

    @Override
    public ChatMessage unpackMessage(ByteBuffer byteBuffer) {
        ChatMessage chatMessage = super.unpackMessage(byteBuffer);
        chatMessage.setServerAlert(byteBuffer.get() == 1);
        chatMessage.setServerAuthor(byteBuffer.get() == 1);
        return chatMessage;
    }

    @Override
    public void packMessage(ByteBufferWriter byteBufferWriter, ChatMessage chatMessage) {
        super.packMessage(byteBufferWriter, chatMessage);
        byteBufferWriter.putBoolean(chatMessage.isServerAlert());
        byteBufferWriter.putBoolean(chatMessage.isServerAuthor());
    }

    @Override
    public String getMessagePrefix(ChatMessage chatMessage) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getChatSettingsTags());
        boolean boolean0 = false;
        if (this.isShowTitle()) {
            stringBuilder.append("[").append(this.getTitle()).append("]");
            boolean0 = true;
        }

        if (!chatMessage.isServerAuthor() && this.isShowAuthor()) {
            stringBuilder.append("[").append(chatMessage.getAuthor()).append("]");
            boolean0 = true;
        }

        if (boolean0) {
            stringBuilder.append(": ");
        }

        return stringBuilder.toString();
    }

    @Override
    public String getMessageTextWithPrefix(ChatMessage chatMessage) {
        return this.getMessagePrefix(chatMessage) + " " + chatMessage.getText();
    }

    @Override
    public void showMessage(ChatMessage chatMessage) {
        this.messages.add(chatMessage);
        if (this.isEnabled()) {
            LuaEventManager.triggerEvent("OnAddMessage", chatMessage, this.getTabID());
        }
    }

    @Override
    public void sendMessageToChatMembers(ChatMessage chatMessage) {
        for (short short0 : this.members) {
            this.sendMessageToPlayer(short0, chatMessage);
        }

        if (Core.bDebug) {
            DebugLog.log("New message '" + chatMessage + "' was sent members of chat '" + this.getID() + "'");
        }
    }
}
