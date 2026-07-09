// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat;

/**
 * Messages which sent by server to any chat stream. This applied stream setting but author always Server
 */
public class ServerChatMessage extends ChatMessage {
    public ServerChatMessage(ChatBase chat, String text) {
        super(chat, text);
        super.setAuthor("Server");
        this.setServerAuthor(true);
    }

    @Override
    public String getAuthor() {
        return super.getAuthor();
    }

    @Override
    public void setAuthor(String author) {
        throw new UnsupportedOperationException();
    }
}
