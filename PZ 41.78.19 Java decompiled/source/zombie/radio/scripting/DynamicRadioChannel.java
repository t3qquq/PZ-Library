// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.scripting;

import zombie.radio.ChannelCategory;

/**
 * TurboTuTone.
 */
public final class DynamicRadioChannel extends RadioChannel {
    public DynamicRadioChannel(String n, int freq, ChannelCategory c) {
        super(n, freq, c);
    }

    public DynamicRadioChannel(String n, int freq, ChannelCategory c, String guid) {
        super(n, freq, c, guid);
    }

    @Override
    public void LoadAiringBroadcast(String guid, int line) {
    }
}
