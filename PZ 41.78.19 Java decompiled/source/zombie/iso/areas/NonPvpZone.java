// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameWindow;
import zombie.network.GameClient;

public final class NonPvpZone {
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int size;
    private String title;
    public static final ArrayList<NonPvpZone> nonPvpZoneList = new ArrayList<>();

    public NonPvpZone() {
    }

    public NonPvpZone(String _title, int _x, int _y, int _x2, int _y2) {
        if (_x > _x2) {
            int int0 = _x2;
            _x2 = _x;
            _x = int0;
        }

        if (_y > _y2) {
            int int1 = _y2;
            _y2 = _y;
            _y = int1;
        }

        this.setX(_x);
        this.setX2(_x2);
        this.setY(_y);
        this.setY2(_y2);
        this.title = _title;
        this.size = Math.abs(_x - _x2 + (_y - _y2));
    }

    public static NonPvpZone addNonPvpZone(String _title, int _x, int _y, int _x2, int _y2) {
        NonPvpZone nonPvpZone = new NonPvpZone(_title, _x, _y, _x2, _y2);
        nonPvpZoneList.add(nonPvpZone);
        nonPvpZone.syncNonPvpZone(false);
        return nonPvpZone;
    }

    public static void removeNonPvpZone(String _title) {
        NonPvpZone nonPvpZone = getZoneByTitle(_title);
        if (nonPvpZone != null) {
            nonPvpZoneList.remove(nonPvpZone);
            nonPvpZone.syncNonPvpZone(true);
        }
    }

    public static NonPvpZone getZoneByTitle(String _title) {
        for (int int0 = 0; int0 < nonPvpZoneList.size(); int0++) {
            NonPvpZone nonPvpZone = nonPvpZoneList.get(int0);
            if (nonPvpZone.getTitle().equals(_title)) {
                return nonPvpZone;
            }
        }

        return null;
    }

    public static NonPvpZone getNonPvpZone(int _x, int _y) {
        for (int int0 = 0; int0 < nonPvpZoneList.size(); int0++) {
            NonPvpZone nonPvpZone = nonPvpZoneList.get(int0);
            if (_x >= nonPvpZone.getX() && _x < nonPvpZone.getX2() && _y >= nonPvpZone.getY() && _y < nonPvpZone.getY2()) {
                return nonPvpZone;
            }
        }

        return null;
    }

    public static ArrayList<NonPvpZone> getAllZones() {
        return nonPvpZoneList;
    }

    public void syncNonPvpZone(boolean remove) {
        if (GameClient.bClient) {
            GameClient.sendNonPvpZone(this, remove);
        }
    }

    public void save(ByteBuffer output) {
        output.putInt(this.getX());
        output.putInt(this.getY());
        output.putInt(this.getX2());
        output.putInt(this.getY2());
        output.putInt(this.getSize());
        GameWindow.WriteString(output, this.getTitle());
    }

    public void load(ByteBuffer input, int WorldVersion) {
        this.setX(input.getInt());
        this.setY(input.getInt());
        this.setX2(input.getInt());
        this.setY2(input.getInt());
        this.setSize(input.getInt());
        this.setTitle(GameWindow.ReadString(input));
    }

    public int getX() {
        return this.x;
    }

    public void setX(int _x) {
        this.x = _x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int _y) {
        this.y = _y;
    }

    public int getX2() {
        return this.x2;
    }

    public void setX2(int _x2) {
        this.x2 = _x2;
    }

    public int getY2() {
        return this.y2;
    }

    public void setY2(int _y2) {
        this.y2 = _y2;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String _title) {
        this.title = _title;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int _size) {
        this.size = _size;
    }
}
