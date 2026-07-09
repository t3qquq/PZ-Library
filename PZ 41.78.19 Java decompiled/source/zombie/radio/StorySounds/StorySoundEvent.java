// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.StorySounds;

import java.util.ArrayList;

/**
 * Turbo
 */
public final class StorySoundEvent {
    protected String name;
    protected ArrayList<EventSound> eventSounds = new ArrayList<>();

    public StorySoundEvent() {
        this("Unnamed");
    }

    public StorySoundEvent(String _name) {
        this.name = _name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public ArrayList<EventSound> getEventSounds() {
        return this.eventSounds;
    }

    public void setEventSounds(ArrayList<EventSound> _eventSounds) {
        this.eventSounds = _eventSounds;
    }
}
