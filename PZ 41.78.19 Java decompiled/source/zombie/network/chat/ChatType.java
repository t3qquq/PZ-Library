// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.chat;

public enum ChatType {
    notDefined(-1, ""),
    general(0, "UI_chat_general_chat_title_id"),
    whisper(1, "UI_chat_private_chat_title_id"),
    say(2, "UI_chat_local_chat_title_id"),
    shout(3, "UI_chat_local_chat_title_id"),
    faction(4, "UI_chat_faction_chat_title_id"),
    safehouse(5, "UI_chat_safehouse_chat_title_id"),
    radio(6, "UI_chat_radio_chat_title_id"),
    admin(7, "UI_chat_admin_chat_title_id"),
    server(8, "UI_chat_server_chat_title_id");

    private final int value;
    private final String titleID;

    public static ChatType valueOf(Integer integer) {
        if (general.value == integer) {
            return general;
        } else if (whisper.value == integer) {
            return whisper;
        } else if (say.value == integer) {
            return say;
        } else if (shout.value == integer) {
            return shout;
        } else if (faction.value == integer) {
            return faction;
        } else if (safehouse.value == integer) {
            return safehouse;
        } else if (radio.value == integer) {
            return radio;
        } else if (admin.value == integer) {
            return admin;
        } else {
            return server.value == integer ? server : notDefined;
        }
    }

    private ChatType(Integer integer, String string1) {
        this.value = integer;
        this.titleID = string1;
    }

    public int getValue() {
        return this.value;
    }

    public String getTitleID() {
        return this.titleID;
    }
}
