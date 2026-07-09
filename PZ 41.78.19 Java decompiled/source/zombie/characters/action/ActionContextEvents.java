// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.action;

public final class ActionContextEvents {
    private ActionContextEvents.Event m_firstEvent;
    private ActionContextEvents.Event m_eventPool;

    public void add(String name, int layer) {
        if (!this.contains(name, layer, false)) {
            ActionContextEvents.Event event = this.allocEvent();
            event.name = name;
            event.layer = layer;
            event.next = this.m_firstEvent;
            this.m_firstEvent = event;
        }
    }

    public boolean contains(String name, int layer) {
        return this.contains(name, layer, true);
    }

    public boolean contains(String name, int layer, boolean bAgnosticLayer) {
        for (ActionContextEvents.Event event = this.m_firstEvent; event != null; event = event.next) {
            if (event.name.equalsIgnoreCase(name)) {
                if (layer == -1) {
                    return true;
                }

                if (event.layer == layer) {
                    return true;
                }

                if (bAgnosticLayer && event.layer == -1) {
                    return true;
                }
            }
        }

        return false;
    }

    public void clear() {
        if (this.m_firstEvent != null) {
            ActionContextEvents.Event event = this.m_firstEvent;

            while (event.next != null) {
                event = event.next;
            }

            event.next = this.m_eventPool;
            this.m_eventPool = this.m_firstEvent;
            this.m_firstEvent = null;
        }
    }

    public void clearEvent(String name) {
        ActionContextEvents.Event event0 = null;
        ActionContextEvents.Event event1 = this.m_firstEvent;

        while (event1 != null) {
            ActionContextEvents.Event event2 = event1.next;
            if (event1.name.equalsIgnoreCase(name)) {
                this.releaseEvent(event1, event0);
            } else {
                event0 = event1;
            }

            event1 = event2;
        }
    }

    private ActionContextEvents.Event allocEvent() {
        if (this.m_eventPool == null) {
            return new ActionContextEvents.Event();
        } else {
            ActionContextEvents.Event event = this.m_eventPool;
            this.m_eventPool = event.next;
            return event;
        }
    }

    private void releaseEvent(ActionContextEvents.Event event1, ActionContextEvents.Event event0) {
        if (event0 == null) {
            assert event1 == this.m_firstEvent;

            this.m_firstEvent = event1.next;
        } else {
            assert event1 != this.m_firstEvent;

            assert event0.next == event1;

            event0.next = event1.next;
        }

        event1.next = this.m_eventPool;
        this.m_eventPool = event1;
    }

    private static final class Event {
        int layer;
        String name;
        ActionContextEvents.Event next;
    }
}
