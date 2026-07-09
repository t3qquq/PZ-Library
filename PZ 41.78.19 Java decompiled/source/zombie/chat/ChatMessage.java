// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import zombie.core.Color;
import zombie.core.network.ByteBufferWriter;

public class ChatMessage implements Cloneable {
    private ChatBase chat;
    private LocalDateTime datetime;
    private String author;
    private String text;
    private boolean scramble = false;
    private String customTag;
    private Color textColor;
    private boolean customColor;
    private boolean overHeadSpeech = true;
    private boolean showInChat = true;
    private boolean fromDiscord = false;
    private boolean serverAlert = false;
    private int radioChannel = -1;
    private boolean local = false;
    private boolean shouldAttractZombies = false;
    private boolean serverAuthor = false;

    public ChatMessage(ChatBase _chat, String _text) {
        this(_chat, LocalDateTime.now(), _text);
    }

    public ChatMessage(ChatBase _chat, LocalDateTime _datetime, String _text) {
        this.chat = _chat;
        this.datetime = _datetime;
        this.text = _text;
        this.textColor = _chat.getColor();
        this.customColor = false;
    }

    public boolean isShouldAttractZombies() {
        return this.shouldAttractZombies;
    }

    public void setShouldAttractZombies(boolean _shouldAttractZombies) {
        this.shouldAttractZombies = _shouldAttractZombies;
    }

    public boolean isLocal() {
        return this.local;
    }

    public void setLocal(boolean __local__) {
        this.local = __local__;
    }

    public String getTextWithReplacedParentheses() {
        return this.text != null ? this.text.replaceAll("<", "&lt;").replaceAll(">", "&gt;") : null;
    }

    public void setScrambledText(String _text) {
        this.scramble = true;
        this.text = _text;
    }

    public int getRadioChannel() {
        return this.radioChannel;
    }

    public void setRadioChannel(int _radioChannel) {
        this.radioChannel = _radioChannel;
    }

    public boolean isServerAuthor() {
        return this.serverAuthor;
    }

    public void setServerAuthor(boolean _serverAuthor) {
        this.serverAuthor = _serverAuthor;
    }

    public boolean isFromDiscord() {
        return this.fromDiscord;
    }

    public void makeFromDiscord() {
        this.fromDiscord = true;
    }

    public boolean isOverHeadSpeech() {
        return this.overHeadSpeech;
    }

    public void setOverHeadSpeech(boolean _overHeadSpeech) {
        this.overHeadSpeech = _overHeadSpeech;
    }

    public boolean isShowInChat() {
        return this.showInChat;
    }

    public void setShowInChat(boolean _showInChat) {
        this.showInChat = _showInChat;
    }

    public LocalDateTime getDatetime() {
        return this.datetime;
    }

    public String getDatetimeStr() {
        return this.datetime.format(DateTimeFormatter.ofPattern("h:m"));
    }

    public void setDatetime(LocalDateTime _datetime) {
        this.datetime = _datetime;
    }

    public boolean isShowAuthor() {
        return this.getChat().isShowAuthor();
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String _author) {
        this.author = _author;
    }

    public ChatBase getChat() {
        return this.chat;
    }

    public int getChatID() {
        return this.chat.getID();
    }

    public String getText() {
        return this.text;
    }

    public void setText(String _text) {
        this.text = _text;
    }

    public String getTextWithPrefix() {
        return this.chat.getMessageTextWithPrefix(this);
    }

    public boolean isScramble() {
        return this.scramble;
    }

    public String getCustomTag() {
        return this.customTag;
    }

    public void setCustomTag(String _customTag) {
        this.customTag = _customTag;
    }

    public Color getTextColor() {
        return this.textColor;
    }

    public void setTextColor(Color _textColor) {
        this.customColor = true;
        this.textColor = _textColor;
    }

    public boolean isCustomColor() {
        return this.customColor;
    }

    public void pack(ByteBufferWriter b) {
        this.chat.packMessage(b, this);
    }

    public ChatMessage clone() {
        ChatMessage chatMessage0;
        try {
            chatMessage0 = (ChatMessage)super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new RuntimeException();
        }

        chatMessage0.datetime = this.datetime;
        chatMessage0.chat = this.chat;
        chatMessage0.author = this.author;
        chatMessage0.text = this.text;
        chatMessage0.scramble = this.scramble;
        chatMessage0.customTag = this.customTag;
        chatMessage0.textColor = this.textColor;
        chatMessage0.customColor = this.customColor;
        chatMessage0.overHeadSpeech = this.overHeadSpeech;
        return chatMessage0;
    }

    public boolean isServerAlert() {
        return this.serverAlert;
    }

    public void setServerAlert(boolean _serverAlert) {
        this.serverAlert = _serverAlert;
    }

    @Override
    public String toString() {
        return "ChatMessage{chat=" + this.chat.getTitle() + ", author='" + this.author + "', text='" + this.text + "'}";
    }
}
