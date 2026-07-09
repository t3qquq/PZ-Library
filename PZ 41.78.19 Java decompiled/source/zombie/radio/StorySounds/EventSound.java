// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.StorySounds;

import java.util.ArrayList;
import zombie.core.Color;

/**
 * Turbo
 */
public final class EventSound {
    protected String name;
    protected Color color = new Color(1.0F, 1.0F, 1.0F);
    protected ArrayList<DataPoint> dataPoints = new ArrayList<>();
    protected ArrayList<StorySound> storySounds = new ArrayList<>();

    public EventSound() {
        this("Unnamed");
    }

    public EventSound(String _name) {
        this.name = _name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String _name) {
        this.name = _name;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color _color) {
        this.color = _color;
    }

    public ArrayList<DataPoint> getDataPoints() {
        return this.dataPoints;
    }

    public void setDataPoints(ArrayList<DataPoint> _dataPoints) {
        this.dataPoints = _dataPoints;
    }

    public ArrayList<StorySound> getStorySounds() {
        return this.storySounds;
    }

    public void setStorySounds(ArrayList<StorySound> _storySounds) {
        this.storySounds = _storySounds;
    }
}
