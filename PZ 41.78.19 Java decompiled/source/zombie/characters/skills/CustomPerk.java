// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.skills;

public final class CustomPerk {
    public String m_id;
    public String m_parent = "None";
    public String m_translation;
    public boolean m_bPassive = false;
    public final int[] m_xp = new int[10];

    public CustomPerk(String string) {
        this.m_id = string;
    }
}
