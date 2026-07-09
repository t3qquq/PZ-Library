// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import zombie.VirtualZombieManager;
import zombie.core.Rand;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoRoomLight;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.objects.IsoLightSwitch;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameServer;

public final class IsoRoom {
    private static final ArrayList<IsoGridSquare> tempSquares = new ArrayList<>();
    public final Vector<IsoGridSquare> Beds = new Vector<>();
    public Rectangle bounds;
    public IsoBuilding building = null;
    public final ArrayList<ItemContainer> Containers = new ArrayList<>();
    public final ArrayList<IsoWindow> Windows = new ArrayList<>();
    public final Vector<IsoRoomExit> Exits = new Vector<>();
    public int layer;
    public String RoomDef = "none";
    public final Vector<IsoGridSquare> TileList = new Vector<>();
    public int transparentWalls = 0;
    public final ArrayList<IsoLightSwitch> lightSwitches = new ArrayList<>();
    public final ArrayList<IsoRoomLight> roomLights = new ArrayList<>();
    public final ArrayList<IsoObject> WaterSources = new ArrayList<>();
    public int seen = 1000000000;
    public int visited = 1000000000;
    public RoomDef def;
    public final ArrayList<RoomDef.RoomRect> rects = new ArrayList<>(1);
    public final ArrayList<IsoGridSquare> Squares = new ArrayList<>();

    public IsoBuilding getBuilding() {
        return this.building;
    }

    public String getName() {
        return this.RoomDef;
    }

    public IsoBuilding CreateBuilding(IsoCell cell) {
        IsoBuilding buildingx = new IsoBuilding(cell);
        this.AddToBuilding(buildingx);
        return buildingx;
    }

    public boolean isInside(int x, int y, int z) {
        for (int int0 = 0; int0 < this.rects.size(); int0++) {
            int int1 = this.rects.get(int0).x;
            int int2 = this.rects.get(int0).y;
            int int3 = this.rects.get(int0).getX2();
            int int4 = this.rects.get(int0).getY2();
            if (x >= int1 && y >= int2 && x < int3 && y < int4 && z == this.layer) {
                return true;
            }
        }

        return false;
    }

    public IsoGridSquare getFreeTile() {
        boolean boolean0 = false;
        IsoGridSquare square = null;
        int int0 = 100;

        while (!boolean0 && int0 > 0) {
            int0--;
            boolean0 = true;
            if (this.TileList.isEmpty()) {
                return null;
            }

            square = this.TileList.get(Rand.Next(this.TileList.size()));

            for (int int1 = 0; int1 < this.Exits.size(); int1++) {
                if (square.getX() == this.Exits.get(int1).x && square.getY() == this.Exits.get(int1).y) {
                    boolean0 = false;
                }
            }

            if (boolean0 && !square.isFree(true)) {
                boolean0 = false;
            }
        }

        return int0 < 0 ? null : square;
    }

    void AddToBuilding(IsoBuilding buildingx) {
        this.building = buildingx;
        buildingx.AddRoom(this);

        for (IsoRoomExit roomExit : this.Exits) {
            if (roomExit.To.From != null && roomExit.To.From.building == null) {
                roomExit.To.From.AddToBuilding(buildingx);
            }
        }
    }

    /**
     * @return the WaterSources
     */
    public ArrayList<IsoObject> getWaterSources() {
        return this.WaterSources;
    }

    /**
     * 
     * @param _WaterSources the WaterSources to set
     */
    public void setWaterSources(ArrayList<IsoObject> _WaterSources) {
        this.WaterSources.clear();
        this.WaterSources.addAll(_WaterSources);
    }

    public boolean hasWater() {
        if (this.WaterSources.isEmpty()) {
            return false;
        } else {
            Iterator iterator = this.WaterSources.iterator();

            while (iterator != null && iterator.hasNext()) {
                IsoObject object = (IsoObject)iterator.next();
                if (object.hasWater()) {
                    return true;
                }
            }

            return false;
        }
    }

    public void useWater() {
        if (!this.WaterSources.isEmpty()) {
            Iterator iterator = this.WaterSources.iterator();

            while (iterator != null && iterator.hasNext()) {
                IsoObject object = (IsoObject)iterator.next();
                if (object.hasWater()) {
                    object.useWater(1);
                    break;
                }
            }
        }
    }

    public ArrayList<IsoWindow> getWindows() {
        return this.Windows;
    }

    public void addSquare(IsoGridSquare sq) {
        if (!this.Squares.contains(sq)) {
            this.Squares.add(sq);
        }
    }

    public void refreshSquares() {
        this.Windows.clear();
        this.Containers.clear();
        this.WaterSources.clear();
        this.Exits.clear();
        tempSquares.clear();
        tempSquares.addAll(this.Squares);
        this.Squares.clear();

        for (int int0 = 0; int0 < tempSquares.size(); int0++) {
            this.addSquare(tempSquares.get(int0));
        }
    }

    private void addExitTo(IsoGridSquare square0, IsoGridSquare square1) {
        IsoRoom room0 = null;
        IsoRoom room1 = null;
        if (square0 != null) {
            room0 = square0.getRoom();
        }

        if (square1 != null) {
            room1 = square1.getRoom();
        }

        if (room0 != null || room1 != null) {
            IsoRoom room2 = room0;
            if (room0 == null) {
                room2 = room1;
            }

            IsoRoomExit roomExit0 = new IsoRoomExit(room2, square0.getX(), square0.getY(), square0.getZ());
            roomExit0.type = IsoRoomExit.ExitType.Door;
            if (room2 == room0) {
                if (room1 != null) {
                    IsoRoomExit roomExit1 = room1.getExitAt(square1.getX(), square1.getY(), square1.getZ());
                    if (roomExit1 == null) {
                        roomExit1 = new IsoRoomExit(room1, square1.getX(), square1.getY(), square1.getZ());
                        room1.Exits.add(roomExit1);
                    }

                    roomExit0.To = roomExit1;
                } else {
                    room0.building.Exits.add(roomExit0);
                    if (square1 != null) {
                        roomExit0.To = new IsoRoomExit(roomExit0, square1.getX(), square1.getY(), square1.getZ());
                    }
                }

                room0.Exits.add(roomExit0);
            } else {
                room1.building.Exits.add(roomExit0);
                if (square1 != null) {
                    roomExit0.To = new IsoRoomExit(roomExit0, square1.getX(), square1.getY(), square1.getZ());
                }

                room1.Exits.add(roomExit0);
            }
        }
    }

    private IsoRoomExit getExitAt(int int3, int int2, int int1) {
        for (int int0 = 0; int0 < this.Exits.size(); int0++) {
            IsoRoomExit roomExit = this.Exits.get(int0);
            if (roomExit.x == int3 && roomExit.y == int2 && roomExit.layer == int1) {
                return roomExit;
            }
        }

        return null;
    }

    public void removeSquare(IsoGridSquare sq) {
        this.Squares.remove(sq);
        IsoRoomExit roomExit = this.getExitAt(sq.getX(), sq.getY(), sq.getZ());
        if (roomExit != null) {
            this.Exits.remove(roomExit);
            if (roomExit.To != null) {
                roomExit.From = null;
            }

            if (this.building.Exits.contains(roomExit)) {
                this.building.Exits.remove(roomExit);
            }
        }

        for (int int0 = 0; int0 < sq.getObjects().size(); int0++) {
            IsoObject object = sq.getObjects().get(int0);
            if (object instanceof IsoLightSwitch) {
                this.lightSwitches.remove(object);
            }
        }
    }

    public void spawnZombies() {
        VirtualZombieManager.instance.addZombiesToMap(1, this.def, false);
    }

    public void onSee() {
        for (int int0 = 0; int0 < this.getBuilding().Rooms.size(); int0++) {
            IsoRoom room1 = this.getBuilding().Rooms.elementAt(int0);
            if (room1 != null && !room1.def.bExplored) {
                room1.def.bExplored = true;
            }

            IsoWorld.instance.getCell().roomSpotted(room1);
        }
    }

    public Vector<IsoGridSquare> getTileList() {
        return this.TileList;
    }

    public ArrayList<IsoGridSquare> getSquares() {
        return this.Squares;
    }

    public ArrayList<ItemContainer> getContainer() {
        return this.Containers;
    }

    public IsoGridSquare getRandomSquare() {
        return this.Squares.isEmpty() ? null : this.Squares.get(Rand.Next(this.Squares.size()));
    }

    public IsoGridSquare getRandomFreeSquare() {
        int int0 = 100;
        IsoGridSquare square = null;
        if (GameServer.bServer) {
            while (int0 > 0) {
                square = IsoWorld.instance
                    .CurrentCell
                    .getGridSquare(this.def.getX() + Rand.Next(this.def.getW()), this.def.getY() + Rand.Next(this.def.getH()), this.def.level);
                if (square != null && square.getRoom() == this && square.isFree(true)) {
                    return square;
                }

                int0--;
            }

            return null;
        } else if (this.Squares.isEmpty()) {
            return null;
        } else {
            while (int0 > 0) {
                square = this.Squares.get(Rand.Next(this.Squares.size()));
                if (square.isFree(true)) {
                    return square;
                }

                int0--;
            }

            return null;
        }
    }

    public boolean hasLightSwitches() {
        if (!this.lightSwitches.isEmpty()) {
            return true;
        } else {
            for (int int0 = 0; int0 < this.def.objects.size(); int0++) {
                if (this.def.objects.get(int0).getType() == 7) {
                    return true;
                }
            }

            return false;
        }
    }

    public void createLights(boolean active) {
        if (this.roomLights.isEmpty()) {
            for (int int0 = 0; int0 < this.def.rects.size(); int0++) {
                RoomDef.RoomRect roomRect = this.def.rects.get(int0);
                IsoRoomLight roomLight = new IsoRoomLight(this, roomRect.x, roomRect.y, this.def.level, roomRect.w, roomRect.h);
                this.roomLights.add(roomLight);
            }
        }
    }

    public RoomDef getRoomDef() {
        return this.def;
    }

    public ArrayList<IsoLightSwitch> getLightSwitches() {
        return this.lightSwitches;
    }
}
