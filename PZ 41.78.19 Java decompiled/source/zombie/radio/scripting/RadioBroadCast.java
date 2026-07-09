// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.scripting;

import java.util.ArrayList;

/**
 * Turbo
 */
public final class RadioBroadCast {
    private static final RadioLine pauseLine = new RadioLine("~", 0.5F, 0.5F, 0.5F);
    private final ArrayList<RadioLine> lines = new ArrayList<>();
    private String ID = "";
    private int startStamp = 0;
    private int endStamp = 0;
    private int lineCount = 0;
    private RadioBroadCast preSegment = null;
    private RadioBroadCast postSegment = null;
    private boolean hasDonePreSegment = false;
    private boolean hasDonePostSegment = false;
    private boolean hasDonePostPause = false;

    public RadioBroadCast(String id, int startstamp, int endstamp) {
        this.ID = id;
        this.startStamp = startstamp;
        this.endStamp = endstamp;
    }

    public String getID() {
        return this.ID;
    }

    public int getStartStamp() {
        return this.startStamp;
    }

    public int getEndStamp() {
        return this.endStamp;
    }

    public void resetLineCounter() {
        this.resetLineCounter(true);
    }

    public void resetLineCounter(boolean doChildren) {
        this.lineCount = 0;
        if (doChildren) {
            if (this.preSegment != null) {
                this.preSegment.resetLineCounter(false);
            }

            if (this.postSegment != null) {
                this.postSegment.resetLineCounter(false);
            }
        }
    }

    public void setPreSegment(RadioBroadCast broadCast) {
        this.preSegment = broadCast;
    }

    public void setPostSegment(RadioBroadCast broadCast) {
        this.postSegment = broadCast;
    }

    public RadioLine getNextLine() {
        return this.getNextLine(true);
    }

    public RadioLine getNextLine(boolean doChildren) {
        RadioLine radioLine = null;
        if (doChildren && !this.hasDonePreSegment && this.lineCount == 0 && this.preSegment != null) {
            radioLine = this.preSegment.getNextLine();
            if (radioLine != null) {
                return radioLine;
            } else {
                this.hasDonePreSegment = true;
                return pauseLine;
            }
        } else {
            if (this.lineCount >= 0 && this.lineCount < this.lines.size()) {
                radioLine = this.lines.get(this.lineCount);
            }

            if (!doChildren || radioLine != null || this.postSegment == null) {
                this.lineCount++;
                return radioLine;
            } else if (!this.hasDonePostPause) {
                this.hasDonePostPause = true;
                return pauseLine;
            } else {
                return this.postSegment.getNextLine();
            }
        }
    }

    public int getCurrentLineNumber() {
        return this.lineCount;
    }

    public void setCurrentLineNumber(int n) {
        this.lineCount = n;
        if (this.lineCount < 0) {
            this.lineCount = 0;
        }
    }

    public RadioLine getCurrentLine() {
        return this.lineCount >= 0 && this.lineCount < this.lines.size() ? this.lines.get(this.lineCount) : null;
    }

    public String PeekNextLineText() {
        if (this.lineCount >= 0 && this.lineCount < this.lines.size()) {
            return this.lines.get(this.lineCount) != null && this.lines.get(this.lineCount).getText() != null
                ? this.lines.get(this.lineCount).getText()
                : "Error";
        } else {
            return "None";
        }
    }

    public void AddRadioLine(RadioLine radioLine) {
        if (radioLine != null) {
            this.lines.add(radioLine);
        }
    }

    public ArrayList<RadioLine> getLines() {
        return this.lines;
    }
}
