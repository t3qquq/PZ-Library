// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.chat.defaultChats;

import java.nio.ByteBuffer;
import java.util.HashMap;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatBase;
import zombie.chat.ChatElement;
import zombie.chat.ChatMessage;
import zombie.chat.ChatMode;
import zombie.chat.ChatTab;
import zombie.chat.ChatUtility;
import zombie.core.Color;
import zombie.core.fonts.AngelCodeFont;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.chat.ChatType;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public abstract class RangeBasedChat extends ChatBase {
    private static ChatElement overHeadChat = null;
    private static HashMap<String, IsoPlayer> players = null;
    private static String currentPlayerName = null;
    String customTag = "default";

    RangeBasedChat(ByteBuffer byteBuffer, ChatType chatType, ChatTab chatTab, IsoPlayer player) {
        super(byteBuffer, chatType, chatTab, player);
    }

    RangeBasedChat(ChatType chatType) {
        super(chatType);
    }

    RangeBasedChat(int int0, ChatType chatType, ChatTab chatTab) {
        super(int0, chatType, chatTab);
    }

    public void Init() {
        currentPlayerName = this.getChatOwnerName();
        if (players != null) {
            players.clear();
        }

        overHeadChat = this.getChatOwner().getChatElement();
    }

    @Override
    public boolean isSendingToRadio() {
        return true;
    }

    @Override
    public ChatMessage createMessage(String string) {
        ChatMessage chatMessage = super.createMessage(string);
        if (this.getMode() == ChatMode.SinglePlayer) {
            chatMessage.setShowInChat(false);
        }

        chatMessage.setOverHeadSpeech(true);
        chatMessage.setShouldAttractZombies(true);
        return chatMessage;
    }

    public ChatMessage createBubbleMessage(String string) {
        ChatMessage chatMessage = super.createMessage(string);
        chatMessage.setOverHeadSpeech(true);
        chatMessage.setShowInChat(false);
        return chatMessage;
    }

    @Override
    public void sendMessageToChatMembers(ChatMessage chatMessage) {
        IsoPlayer player0 = ChatUtility.findPlayer(chatMessage.getAuthor());
        if (this.getRange() == -1.0F) {
            DebugLog.log("Range not set for '" + this.getTitle() + "' chat. Message '" + chatMessage.getText() + "' ignored");
        } else {
            for (short short0 : this.members) {
                IsoPlayer player1 = ChatUtility.findPlayer(short0);
                if (player1 != null && player0.getOnlineID() != short0 && ChatUtility.getDistance(player0, player1) < this.getRange()) {
                    this.sendMessageToPlayer(short0, chatMessage);
                }
            }
        }
    }

    @Override
    public void showMessage(ChatMessage chatMessage) {
        super.showMessage(chatMessage);
        if (chatMessage.isOverHeadSpeech()) {
            this.showInSpeechBubble(chatMessage);
        }
    }

    protected ChatElement getSpeechBubble() {
        return overHeadChat;
    }

    protected UIFont selectFont(String string) {
        char[] chars = string.toCharArray();
        UIFont uIFont = UIFont.Dialogue;
        AngelCodeFont angelCodeFont = TextManager.instance.getFontFromEnum(uIFont);

        for (int int0 = 0; int0 < chars.length; int0++) {
            if (chars[int0] > angelCodeFont.chars.length) {
                uIFont = UIFont.Medium;
                break;
            }
        }

        return uIFont;
    }

    protected void showInSpeechBubble(ChatMessage chatMessage) {
        Color color = this.getColor();
        String string0 = chatMessage.getAuthor();
        IsoPlayer player0 = this.getPlayer(string0);
        float float0 = color.r;
        float float1 = color.g;
        float float2 = color.b;
        if (player0 != null) {
            float0 = player0.getSpeakColour().r;
            float1 = player0.getSpeakColour().g;
            float2 = player0.getSpeakColour().b;
        }

        String string1 = ChatUtility.parseStringForChatBubble(chatMessage.getText());
        if (string0 != null && !"".equalsIgnoreCase(string0) && !string0.equalsIgnoreCase(currentPlayerName)) {
            if (!players.containsKey(string0)) {
                players.put(string0, this.getPlayer(string0));
            }

            IsoPlayer player1 = players.get(string0);
            if (player1 != null) {
                if (player1.isDead()) {
                    player1 = this.getPlayer(string0);
                    players.replace(string0, player1);
                }

                player1.getChatElement()
                    .addChatLine(
                        string1,
                        float0,
                        float1,
                        float2,
                        this.selectFont(string1),
                        this.getRange(),
                        this.customTag,
                        this.isAllowBBcode(),
                        this.isAllowImages(),
                        this.isAllowChatIcons(),
                        this.isAllowColors(),
                        this.isAllowFonts(),
                        this.isEqualizeLineHeights()
                    );
            }
        } else {
            overHeadChat.addChatLine(
                string1,
                float0,
                float1,
                float2,
                this.selectFont(string1),
                this.getRange(),
                this.customTag,
                this.isAllowBBcode(),
                this.isAllowImages(),
                this.isAllowChatIcons(),
                this.isAllowColors(),
                this.isAllowFonts(),
                this.isEqualizeLineHeights()
            );
        }
    }

    private IsoPlayer getPlayer(String string) {
        IsoPlayer player = GameClient.bClient ? GameClient.instance.getPlayerFromUsername(string) : null;
        if (player != null) {
            return player;
        } else {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                player = IsoPlayer.players[int0];
                if (player != null && player.getUsername().equals(string)) {
                    return player;
                }
            }

            return null;
        }
    }

    static {
        players = new HashMap<>();
    }
}
