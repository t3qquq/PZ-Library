// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.modding;

import java.util.ArrayList;
import java.util.Objects;
import zombie.GameWindow;
import zombie.MapGroups;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.debug.DebugOptions;
import zombie.gameStates.ChooseGameInfo;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.util.StringUtils;

public final class ActiveMods {
    private static final ArrayList<ActiveMods> s_activeMods = new ArrayList<>();
    private static final ActiveMods s_loaded = new ActiveMods("loaded");
    private final String id;
    private final ArrayList<String> mods = new ArrayList<>();
    private final ArrayList<String> mapOrder = new ArrayList<>();

    private static int count() {
        return s_activeMods.size();
    }

    public static ActiveMods getByIndex(int index) {
        return s_activeMods.get(index);
    }

    public static ActiveMods getById(String _id) {
        int int0 = indexOf(_id);
        return int0 == -1 ? create(_id) : s_activeMods.get(int0);
    }

    public static int indexOf(String _id) {
        _id = _id.trim();
        requireValidId(_id);

        for (int int0 = 0; int0 < s_activeMods.size(); int0++) {
            ActiveMods activeMods = s_activeMods.get(int0);
            if (activeMods.id.equalsIgnoreCase(_id)) {
                return int0;
            }
        }

        return -1;
    }

    private static ActiveMods create(String string) {
        requireValidId(string);
        if (indexOf(string) != -1) {
            throw new IllegalStateException("id \"" + string + "\" exists");
        } else {
            ActiveMods activeMods = new ActiveMods(string);
            s_activeMods.add(activeMods);
            return activeMods;
        }
    }

    private static void requireValidId(String string) {
        if (StringUtils.isNullOrWhitespace(string)) {
            throw new IllegalArgumentException("id is null or whitespace");
        }
    }

    public static void setLoadedMods(ActiveMods activeMods) {
        if (activeMods != null) {
            s_loaded.copyFrom(activeMods);
        }
    }

    public static boolean requiresResetLua(ActiveMods activeMods) {
        Objects.requireNonNull(activeMods);
        return !s_loaded.mods.equals(activeMods.mods);
    }

    public static void renderUI() {
        if (DebugOptions.instance.ModRenderLoaded.getValue()) {
            if (!GameWindow.DrawReloadingLua) {
                UIFont uIFont = UIFont.DebugConsole;
                int int0 = TextManager.instance.getFontHeight(uIFont);
                String string0 = "Active Mods:";
                int int1 = TextManager.instance.MeasureStringX(uIFont, string0);

                for (int int2 = 0; int2 < s_loaded.mods.size(); int2++) {
                    String string1 = s_loaded.mods.get(int2);
                    int int3 = TextManager.instance.MeasureStringX(uIFont, string1);
                    int1 = Math.max(int1, int3);
                }

                byte byte0 = 10;
                int1 += byte0 * 2;
                int int4 = Core.width - 20 - int1;
                byte byte1 = 20;
                int int5 = (1 + s_loaded.mods.size()) * int0 + byte0 * 2;
                SpriteRenderer.instance.renderi(null, int4, byte1, int1, int5, 0.0F, 0.5F, 0.75F, 1.0F, null);
                int int6;
                TextManager.instance.DrawString(uIFont, int4 + byte0, int6 = byte1 + byte0, string0, 1.0, 1.0, 1.0, 1.0);

                for (int int7 = 0; int7 < s_loaded.mods.size(); int7++) {
                    String string2 = s_loaded.mods.get(int7);
                    TextManager.instance.DrawString(uIFont, int4 + byte0, int6 += int0, string2, 1.0, 1.0, 1.0, 1.0);
                }
            }
        }
    }

    public static void Reset() {
        s_loaded.clear();
    }

    public ActiveMods(String _id) {
        requireValidId(_id);
        this.id = _id;
    }

    public void clear() {
        this.mods.clear();
        this.mapOrder.clear();
    }

    public ArrayList<String> getMods() {
        return this.mods;
    }

    public ArrayList<String> getMapOrder() {
        return this.mapOrder;
    }

    public void copyFrom(ActiveMods other) {
        this.mods.clear();
        this.mapOrder.clear();
        this.mods.addAll(other.mods);
        this.mapOrder.addAll(other.mapOrder);
    }

    public void setModActive(String modID, boolean active) {
        modID = modID.trim();
        if (!StringUtils.isNullOrWhitespace(modID)) {
            if (active) {
                if (!this.mods.contains(modID)) {
                    this.mods.add(modID);
                }
            } else {
                this.mods.remove(modID);
            }
        }
    }

    public boolean isModActive(String modID) {
        modID = modID.trim();
        return StringUtils.isNullOrWhitespace(modID) ? false : this.mods.contains(modID);
    }

    public void removeMod(String modID) {
        modID = modID.trim();
        this.mods.remove(modID);
    }

    public void removeMapOrder(String folder) {
        this.mapOrder.remove(folder);
    }

    public void checkMissingMods() {
        if (!this.mods.isEmpty()) {
            for (int int0 = this.mods.size() - 1; int0 >= 0; int0--) {
                String string = this.mods.get(int0);
                if (ChooseGameInfo.getAvailableModDetails(string) == null) {
                    this.mods.remove(int0);
                }
            }
        }
    }

    public void checkMissingMaps() {
        if (!this.mapOrder.isEmpty()) {
            MapGroups mapGroups = new MapGroups();
            mapGroups.createGroups(this, false);
            if (mapGroups.checkMapConflicts()) {
                ArrayList arrayList = mapGroups.getAllMapsInOrder();

                for (int int0 = this.mapOrder.size() - 1; int0 >= 0; int0--) {
                    String string = this.mapOrder.get(int0);
                    if (!arrayList.contains(string)) {
                        this.mapOrder.remove(int0);
                    }
                }
            } else {
                this.mapOrder.clear();
            }
        }
    }
}
