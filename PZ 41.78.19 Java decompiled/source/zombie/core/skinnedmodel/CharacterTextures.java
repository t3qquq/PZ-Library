// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel;

import java.util.ArrayList;
import zombie.iso.IsoDirections;

public final class CharacterTextures {
    final ArrayList<CharacterTextures.CTAnimSet> m_animSets = new ArrayList<>();

    CharacterTextures.CTAnimSet getAnimSet(String string) {
        for (int int0 = 0; int0 < this.m_animSets.size(); int0++) {
            CharacterTextures.CTAnimSet cTAnimSet = this.m_animSets.get(int0);
            if (cTAnimSet.m_name.equals(string)) {
                return cTAnimSet;
            }
        }

        return null;
    }

    DeadBodyAtlas.BodyTexture getTexture(String string0, String string1, IsoDirections directions, int int0) {
        CharacterTextures.CTAnimSet cTAnimSet = this.getAnimSet(string0);
        if (cTAnimSet == null) {
            return null;
        } else {
            CharacterTextures.CTState cTState = cTAnimSet.getState(string1);
            if (cTState == null) {
                return null;
            } else {
                CharacterTextures.CTEntry cTEntry = cTState.getEntry(directions, int0);
                return cTEntry == null ? null : cTEntry.m_texture;
            }
        }
    }

    void addTexture(String string0, String string1, IsoDirections directions, int int0, DeadBodyAtlas.BodyTexture bodyTexture) {
        CharacterTextures.CTAnimSet cTAnimSet = this.getAnimSet(string0);
        if (cTAnimSet == null) {
            cTAnimSet = new CharacterTextures.CTAnimSet();
            cTAnimSet.m_name = string0;
            this.m_animSets.add(cTAnimSet);
        }

        cTAnimSet.addEntry(string1, directions, int0, bodyTexture);
    }

    void clear() {
        this.m_animSets.clear();
    }

    private static final class CTAnimSet {
        String m_name;
        final ArrayList<CharacterTextures.CTState> m_states = new ArrayList<>();

        CharacterTextures.CTState getState(String string) {
            for (int int0 = 0; int0 < this.m_states.size(); int0++) {
                CharacterTextures.CTState cTState = this.m_states.get(int0);
                if (cTState.m_name.equals(string)) {
                    return cTState;
                }
            }

            return null;
        }

        void addEntry(String string, IsoDirections directions, int int0, DeadBodyAtlas.BodyTexture bodyTexture) {
            CharacterTextures.CTState cTState = this.getState(string);
            if (cTState == null) {
                cTState = new CharacterTextures.CTState();
                cTState.m_name = string;
                this.m_states.add(cTState);
            }

            cTState.addEntry(directions, int0, bodyTexture);
        }
    }

    private static final class CTEntry {
        int m_frame;
        DeadBodyAtlas.BodyTexture m_texture;
    }

    private static final class CTEntryList extends ArrayList<CharacterTextures.CTEntry> {
    }

    private static final class CTState {
        String m_name;
        final CharacterTextures.CTEntryList[] m_entries = new CharacterTextures.CTEntryList[IsoDirections.values().length];

        CTState() {
            for (int int0 = 0; int0 < this.m_entries.length; int0++) {
                this.m_entries[int0] = new CharacterTextures.CTEntryList();
            }
        }

        CharacterTextures.CTEntry getEntry(IsoDirections directions, int int1) {
            CharacterTextures.CTEntryList cTEntryList = this.m_entries[directions.index()];

            for (int int0 = 0; int0 < cTEntryList.size(); int0++) {
                CharacterTextures.CTEntry cTEntry = cTEntryList.get(int0);
                if (cTEntry.m_frame == int1) {
                    return cTEntry;
                }
            }

            return null;
        }

        void addEntry(IsoDirections directions, int int0, DeadBodyAtlas.BodyTexture bodyTexture) {
            CharacterTextures.CTEntryList cTEntryList = this.m_entries[directions.index()];
            CharacterTextures.CTEntry cTEntry = new CharacterTextures.CTEntry();
            cTEntry.m_frame = int0;
            cTEntry.m_texture = bodyTexture;
            cTEntryList.add(cTEntry);
        }
    }
}
