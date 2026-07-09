// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

public class Userlog {
    private final String username;
    private final String type;
    private final String text;
    private final String issuedBy;
    private final String lastUpdate;
    private int amount;

    public Userlog(String string0, String string1, String string2, String string3, int int0, String string4) {
        this.username = string0;
        this.type = string1;
        this.text = string2;
        this.issuedBy = string3;
        this.amount = int0;
        this.lastUpdate = string4;
    }

    public String getUsername() {
        return this.username;
    }

    public String getType() {
        return this.type;
    }

    public String getText() {
        return this.text;
    }

    public String getIssuedBy() {
        return this.issuedBy;
    }

    public int getAmount() {
        return this.amount;
    }

    public void setAmount(int _amount) {
        this.amount = _amount;
    }

    public String getLastUpdate() {
        return this.lastUpdate;
    }

    public static enum UserlogType {
        AdminLog(0),
        Kicked(1),
        Banned(2),
        DupeItem(3),
        LuaChecksum(4),
        WarningPoint(5),
        UnauthorizedPacket(6),
        SuspiciousActivity(7);

        private final int index;

        private UserlogType(int int1) {
            this.index = int1;
        }

        public int index() {
            return this.index;
        }

        public static Userlog.UserlogType fromIndex(int value) {
            return Userlog.UserlogType.class.getEnumConstants()[value];
        }

        public static Userlog.UserlogType FromString(String str) {
            return valueOf(str);
        }
    }
}
