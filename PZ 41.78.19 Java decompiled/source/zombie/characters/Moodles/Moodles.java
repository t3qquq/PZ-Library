// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.Moodles;

import java.util.Stack;
import zombie.characters.IsoGameCharacter;
import zombie.core.Color;

public final class Moodles {
    boolean MoodlesStateChanged = false;
    private Stack<Moodle> MoodleList = new Stack<>();
    private final IsoGameCharacter Parent;

    public Moodles(IsoGameCharacter parent) {
        this.Parent = parent;
        this.MoodleList.add(new Moodle(MoodleType.Endurance, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Tired, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Hungry, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Panic, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Sick, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Bored, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Unhappy, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Bleeding, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Wet, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.HasACold, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Angry, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Stress, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Thirst, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Injured, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Pain, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.HeavyLoad, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Drunk, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Dead, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Zombie, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.FoodEaten, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.Hyperthermia, this.Parent, 3));
        this.MoodleList.add(new Moodle(MoodleType.Hypothermia, this.Parent, 3));
        this.MoodleList.add(new Moodle(MoodleType.Windchill, this.Parent));
        this.MoodleList.add(new Moodle(MoodleType.CantSprint, this.Parent));
    }

    public int getGoodBadNeutral(int MoodleIndex) {
        return MoodleType.GoodBadNeutral(this.MoodleList.get(MoodleIndex).Type);
    }

    public String getMoodleDisplayString(int MoodleIndex) {
        return MoodleType.getDisplayName(this.MoodleList.get(MoodleIndex).Type, this.MoodleList.get(MoodleIndex).getLevel());
    }

    public String getMoodleDescriptionString(int MoodleIndex) {
        return MoodleType.getDescriptionText(this.MoodleList.get(MoodleIndex).Type, this.MoodleList.get(MoodleIndex).getLevel());
    }

    public int getMoodleLevel(int MoodleIndex) {
        return this.MoodleList.get(MoodleIndex).getLevel();
    }

    public int getMoodleLevel(MoodleType MType) {
        return this.MoodleList.get(MoodleType.ToIndex(MType)).getLevel();
    }

    public int getMoodleChevronCount(int moodleIndex) {
        return this.MoodleList.get(moodleIndex).getChevronCount();
    }

    public boolean getMoodleChevronIsUp(int moodleIndex) {
        return this.MoodleList.get(moodleIndex).isChevronIsUp();
    }

    public Color getMoodleChevronColor(int moodleIndex) {
        return this.MoodleList.get(moodleIndex).getChevronColor();
    }

    public MoodleType getMoodleType(int MoodleIndex) {
        return this.MoodleList.get(MoodleIndex).Type;
    }

    public int getNumMoodles() {
        return this.MoodleList.size();
    }

    public void Randomise() {
    }

    public boolean UI_RefreshNeeded() {
        if (this.MoodlesStateChanged) {
            this.MoodlesStateChanged = false;
            return true;
        } else {
            return false;
        }
    }

    public void setMoodlesStateChanged(boolean refresh) {
        this.MoodlesStateChanged = refresh;
    }

    public void Update() {
        for (int int0 = 0; int0 < this.MoodleList.size(); int0++) {
            if (this.MoodleList.get(int0).Update()) {
                this.MoodlesStateChanged = true;
            }
        }
    }
}
