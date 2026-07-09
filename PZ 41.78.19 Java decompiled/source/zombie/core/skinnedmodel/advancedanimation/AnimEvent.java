// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import javax.xml.bind.annotation.XmlTransient;

/**
 * class AnimEvent   Used to set a game variable from an animation node.     eg. Set a sword's collision box to Active during a swing animation,     then Inactive once swing is done.     Holds a time, name, and value     The time is measured as a fraction of the animation's time.     This means that scaling an animation's speed scales the Events as well.
 */
public final class AnimEvent {
    public String m_EventName;
    public AnimEvent.AnimEventTime m_Time = AnimEvent.AnimEventTime.Percentage;
    public float m_TimePc;
    public String m_ParameterValue;
    @XmlTransient
    public String m_SetVariable1;
    @XmlTransient
    public String m_SetVariable2;

    @Override
    public String toString() {
        return String.format("%s { %s }", this.getClass().getName(), this.toDetailsString());
    }

    public String toDetailsString() {
        return String.format(
            "Details: %s %s, time: %s",
            this.m_EventName,
            this.m_ParameterValue,
            this.m_Time == AnimEvent.AnimEventTime.Percentage ? Float.toString(this.m_TimePc) : this.m_Time.name()
        );
    }

    public static enum AnimEventTime {
        Percentage,
        Start,
        End;
    }
}
