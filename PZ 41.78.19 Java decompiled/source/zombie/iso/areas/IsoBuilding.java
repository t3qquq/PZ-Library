// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.core.Rand;
import zombie.core.opengl.RenderSettings;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemType;
import zombie.iso.BuildingDef;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LotHeader;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoWindow;

public final class IsoBuilding extends IsoArea {
    public Rectangle bounds;
    public final Vector<IsoRoomExit> Exits = new Vector<>();
    public boolean IsResidence = true;
    public final ArrayList<ItemContainer> container = new ArrayList<>();
    public final Vector<IsoRoom> Rooms = new Vector<>();
    public final Vector<IsoWindow> Windows = new Vector<>();
    public int ID = 0;
    public static int IDMax = 0;
    public int safety = 0;
    public int transparentWalls = 0;
    private boolean isToxic = false;
    public static float PoorBuildingScore = 10.0F;
    public static float GoodBuildingScore = 100.0F;
    public int scoreUpdate = -1;
    public BuildingDef def;
    public boolean bSeenInside = false;
    public ArrayList<IsoLightSource> lights = new ArrayList<>();
    static ArrayList<IsoRoom> tempo = new ArrayList<>();
    static ArrayList<ItemContainer> tempContainer = new ArrayList<>();
    static ArrayList<String> RandomContainerChoices = new ArrayList<>();
    static ArrayList<IsoWindow> windowchoices = new ArrayList<>();

    public int getRoomsNumber() {
        return this.Rooms.size();
    }

    public IsoBuilding() {
        this.ID = IDMax++;
        this.scoreUpdate = -120 + Rand.Next(120);
    }

    public int getID() {
        return this.ID;
    }

    public void TriggerAlarm() {
    }

    public IsoBuilding(IsoCell cell) {
        this.ID = IDMax++;
        this.scoreUpdate = -120 + Rand.Next(120);
    }

    public boolean ContainsAllItems(Stack<String> items) {
        return false;
    }

    public float ScoreBuildingPersonSpecific(SurvivorDesc desc, boolean bFarGood) {
        float float0 = 0.0F;
        float0 += this.Rooms.size() * 5;
        float0 += this.Exits.size() * 15;
        float0 -= this.transparentWalls * 10;

        for (int int0 = 0; int0 < this.container.size(); int0++) {
            ItemContainer containerx = this.container.get(int0);
            float0 += containerx.Items.size() * 3;
        }

        if (!IsoWorld.instance.CurrentCell.getBuildingScores().containsKey(this.ID)) {
            BuildingScore buildingScore0 = new BuildingScore(this);
            buildingScore0.building = this;
            IsoWorld.instance.CurrentCell.getBuildingScores().put(this.ID, buildingScore0);
            this.ScoreBuildingGeneral(buildingScore0);
        }

        BuildingScore buildingScore1 = IsoWorld.instance.CurrentCell.getBuildingScores().get(this.ID);
        float0 += (buildingScore1.defense + buildingScore1.food + buildingScore1.size + buildingScore1.weapons + buildingScore1.wood) * 10.0F;
        int int1 = -10000;
        int int2 = -10000;
        if (!this.Exits.isEmpty()) {
            IsoRoomExit roomExit = this.Exits.get(0);
            int1 = roomExit.x;
            int2 = roomExit.y;
        }

        float float1 = IsoUtils.DistanceManhatten(desc.getInstance().getX(), desc.getInstance().getY(), int1, int2);
        if (float1 > 0.0F) {
            if (bFarGood) {
                float0 *= float1 * 0.5F;
            } else {
                float0 /= float1 * 0.5F;
            }
        }

        return float0;
    }

    public BuildingDef getDef() {
        return this.def;
    }

    public void update() {
        if (!this.Exits.isEmpty()) {
            int int0 = 0;
            int int1 = 0;

            for (int int2 = 0; int2 < this.Rooms.size(); int2++) {
                IsoRoom room = this.Rooms.get(int2);
                if (room.layer == 0) {
                    for (int int3 = 0; int3 < room.TileList.size(); int3++) {
                        int1++;
                        IsoGridSquare square = room.TileList.get(int3);
                    }
                }
            }

            if (int1 == 0) {
                int1++;
            }

            int0 = (int)((float)int0 / int1);
            this.scoreUpdate--;
            if (this.scoreUpdate <= 0) {
                this.scoreUpdate += 120;
                BuildingScore buildingScore = null;
                if (IsoWorld.instance.CurrentCell.getBuildingScores().containsKey(this.ID)) {
                    buildingScore = IsoWorld.instance.CurrentCell.getBuildingScores().get(this.ID);
                } else {
                    buildingScore = new BuildingScore(this);
                    buildingScore.building = this;
                }

                buildingScore = this.ScoreBuildingGeneral(buildingScore);
                buildingScore.defense += int0 * 10;
                this.safety = int0;
                IsoWorld.instance.CurrentCell.getBuildingScores().put(this.ID, buildingScore);
            }
        }
    }

    public void AddRoom(IsoRoom room) {
        this.Rooms.add(room);
        if (this.bounds == null) {
            this.bounds = (Rectangle)room.bounds.clone();
        }

        if (room != null && room.bounds != null) {
            this.bounds.add(room.bounds);
        }
    }

    public void CalculateExits() {
        for (IsoRoom room : this.Rooms) {
            for (IsoRoomExit roomExit : room.Exits) {
                if (roomExit.To.From == null && room.layer == 0) {
                    this.Exits.add(roomExit);
                }
            }
        }
    }

    public void CalculateWindows() {
        for (IsoRoom room : this.Rooms) {
            for (IsoGridSquare square0 : room.TileList) {
                IsoGridSquare square1 = square0.getCell().getGridSquare(square0.getX(), square0.getY() + 1, square0.getZ());
                IsoGridSquare square2 = square0.getCell().getGridSquare(square0.getX() + 1, square0.getY(), square0.getZ());
                if (square0.getProperties().Is(IsoFlagType.collideN) && square0.getProperties().Is(IsoFlagType.transparentN)) {
                    room.transparentWalls++;
                    this.transparentWalls++;
                }

                if (square0.getProperties().Is(IsoFlagType.collideW) && square0.getProperties().Is(IsoFlagType.transparentW)) {
                    room.transparentWalls++;
                    this.transparentWalls++;
                }

                if (square1 != null) {
                    boolean boolean0 = square1.getRoom() != null;
                    if (square1.getRoom() != null && square1.getRoom().building != room.building) {
                        boolean0 = false;
                    }

                    if (square1.getProperties().Is(IsoFlagType.collideN) && square1.getProperties().Is(IsoFlagType.transparentN) && !boolean0) {
                        room.transparentWalls++;
                        this.transparentWalls++;
                    }
                }

                if (square2 != null) {
                    boolean boolean1 = square2.getRoom() != null;
                    if (square2.getRoom() != null && square2.getRoom().building != room.building) {
                        boolean1 = false;
                    }

                    if (square2.getProperties().Is(IsoFlagType.collideW) && square2.getProperties().Is(IsoFlagType.transparentW) && !boolean1) {
                        room.transparentWalls++;
                        this.transparentWalls++;
                    }
                }

                for (int int0 = 0; int0 < square0.getSpecialObjects().size(); int0++) {
                    IsoObject object0 = square0.getSpecialObjects().get(int0);
                    if (object0 instanceof IsoWindow) {
                        this.Windows.add((IsoWindow)object0);
                    }
                }

                if (square1 != null) {
                    for (int int1 = 0; int1 < square1.getSpecialObjects().size(); int1++) {
                        IsoObject object1 = square1.getSpecialObjects().get(int1);
                        if (object1 instanceof IsoWindow) {
                            this.Windows.add((IsoWindow)object1);
                        }
                    }
                }

                if (square2 != null) {
                    for (int int2 = 0; int2 < square2.getSpecialObjects().size(); int2++) {
                        IsoObject object2 = square2.getSpecialObjects().get(int2);
                        if (object2 instanceof IsoWindow) {
                            this.Windows.add((IsoWindow)object2);
                        }
                    }
                }
            }
        }
    }

    public void FillContainers() {
        boolean boolean0 = false;

        for (IsoRoom room : this.Rooms) {
            if (room.RoomDef != null && room.RoomDef.contains("tutorial")) {
                boolean0 = true;
            }

            if (!room.TileList.isEmpty()) {
                IsoGridSquare square0 = room.TileList.get(0);
                if (square0.getX() < 74 && square0.getY() < 32) {
                    boolean0 = true;
                }
            }

            if (room.RoomDef.contains("shop")) {
                this.IsResidence = false;
            }

            for (IsoGridSquare square1 : room.TileList) {
                for (int int0 = 0; int0 < square1.getObjects().size(); int0++) {
                    IsoObject object = square1.getObjects().get(int0);
                    if (object.hasWater()) {
                        room.getWaterSources().add(object);
                    }

                    if (object.container != null) {
                        this.container.add(object.container);
                        room.Containers.add(object.container);
                    }
                }

                if (square1.getProperties().Is(IsoFlagType.bed)) {
                    room.Beds.add(square1);
                }
            }
        }
    }

    public ItemContainer getContainerWith(ItemType itemType) {
        for (IsoRoom room : this.Rooms) {
            for (ItemContainer containerx : room.Containers) {
                if (containerx.HasType(itemType)) {
                    return containerx;
                }
            }
        }

        return null;
    }

    public IsoRoom getRandomRoom() {
        return this.Rooms.size() == 0 ? null : this.Rooms.get(Rand.Next(this.Rooms.size()));
    }

    private BuildingScore ScoreBuildingGeneral(BuildingScore buildingScore) {
        buildingScore.food = 0.0F;
        buildingScore.defense = 0.0F;
        buildingScore.weapons = 0.0F;
        buildingScore.wood = 0.0F;
        buildingScore.building = this;
        buildingScore.size = 0;
        buildingScore.defense = buildingScore.defense + (this.Exits.size() - 1) * 140;
        buildingScore.defense = buildingScore.defense - this.transparentWalls * 40;
        buildingScore.size = this.Rooms.size() * 10;
        buildingScore.size = buildingScore.size + this.container.size() * 10;
        return buildingScore;
    }

    public IsoGridSquare getFreeTile() {
        IsoGridSquare square = null;

        do {
            IsoRoom room = this.Rooms.get(Rand.Next(this.Rooms.size()));
            square = room.getFreeTile();
        } while (square == null);

        return square;
    }

    public boolean hasWater() {
        Iterator iterator = this.Rooms.iterator();

        while (iterator != null && iterator.hasNext()) {
            IsoRoom room = (IsoRoom)iterator.next();
            if (!room.WaterSources.isEmpty()) {
                IsoObject object = null;
                int int0 = 0;

                while (true) {
                    if (int0 < room.WaterSources.size()) {
                        if (!room.WaterSources.get(int0).hasWater()) {
                            int0++;
                            continue;
                        }

                        object = room.WaterSources.get(int0);
                    }

                    if (object != null) {
                        return true;
                    }
                    break;
                }
            }
        }

        return false;
    }

    public void CreateFrom(BuildingDef building, LotHeader info) {
        for (int int0 = 0; int0 < building.rooms.size(); int0++) {
            IsoRoom room = info.getRoom(building.rooms.get(int0).ID);
            room.building = this;
            this.Rooms.add(room);
        }
    }

    public void setAllExplored(boolean b) {
        this.def.bAlarmed = false;

        for (int int0 = 0; int0 < this.Rooms.size(); int0++) {
            IsoRoom room = this.Rooms.get(int0);
            room.def.setExplored(b);

            for (int int1 = room.def.getX(); int1 <= room.def.getX2(); int1++) {
                for (int int2 = room.def.getY(); int2 <= room.def.getY2(); int2++) {
                    IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, room.def.level);
                    if (square != null) {
                        square.setHourSeenToCurrent();
                    }
                }
            }
        }
    }

    public boolean isAllExplored() {
        for (int int0 = 0; int0 < this.Rooms.size(); int0++) {
            IsoRoom room = this.Rooms.get(int0);
            if (!room.def.bExplored) {
                return false;
            }
        }

        return true;
    }

    public void addWindow(IsoWindow obj, boolean bOtherTile, IsoGridSquare from, IsoBuilding building) {
        this.Windows.add(obj);
        IsoGridSquare square = null;
        if (bOtherTile) {
            square = obj.square;
        } else {
            square = from;
        }

        if (square != null) {
            if (square.getRoom() == null) {
                float float0 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
                float float1 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
                float float2 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
                byte byte0 = 7;
                IsoLightSource lightSource = new IsoLightSource(square.getX(), square.getY(), square.getZ(), float0, float1, float2, byte0, building);
                this.lights.add(lightSource);
                IsoWorld.instance.CurrentCell.getLamppostPositions().add(lightSource);
            }
        }
    }

    public void addWindow(IsoWindow obj, boolean bOtherTile) {
        this.addWindow(obj, bOtherTile, obj.square, null);
    }

    public void addDoor(IsoDoor obj, boolean bOtherTile, IsoGridSquare from, IsoBuilding building) {
        IsoGridSquare square = null;
        if (bOtherTile) {
            square = obj.square;
        } else {
            square = from;
        }

        if (square != null) {
            if (square.getRoom() == null) {
                float float0 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
                float float1 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
                float float2 = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
                byte byte0 = 7;
                IsoLightSource lightSource = new IsoLightSource(square.getX(), square.getY(), square.getZ(), float0, float1, float2, byte0, building);
                this.lights.add(lightSource);
                IsoWorld.instance.CurrentCell.getLamppostPositions().add(lightSource);
            }
        }
    }

    public void addDoor(IsoDoor obj, boolean bOtherTile) {
        this.addDoor(obj, bOtherTile, obj.square, null);
    }

    public boolean isResidential() {
        return this.containsRoom("bedroom");
    }

    public boolean containsRoom(String room) {
        for (int int0 = 0; int0 < this.Rooms.size(); int0++) {
            if (room.equals(this.Rooms.get(int0).getName())) {
                return true;
            }
        }

        return false;
    }

    public IsoRoom getRandomRoom(String room) {
        tempo.clear();

        for (int int0 = 0; int0 < this.Rooms.size(); int0++) {
            if (room.equals(this.Rooms.get(int0).getName())) {
                tempo.add(this.Rooms.get(int0));
            }
        }

        return tempo.isEmpty() ? null : tempo.get(Rand.Next(tempo.size()));
    }

    public ItemContainer getRandomContainer(String type) {
        RandomContainerChoices.clear();
        String[] strings = null;
        if (type != null) {
            strings = type.split(",");
        }

        if (strings != null) {
            for (int int0 = 0; int0 < strings.length; int0++) {
                RandomContainerChoices.add(strings[int0]);
            }
        }

        tempContainer.clear();

        for (int int1 = 0; int1 < this.Rooms.size(); int1++) {
            IsoRoom room = this.Rooms.get(int1);

            for (int int2 = 0; int2 < room.Containers.size(); int2++) {
                ItemContainer containerx = room.Containers.get(int2);
                if (type == null || RandomContainerChoices.contains(containerx.getType())) {
                    tempContainer.add(containerx);
                }
            }
        }

        return tempContainer.isEmpty() ? null : tempContainer.get(Rand.Next(tempContainer.size()));
    }

    public IsoWindow getRandomFirstFloorWindow() {
        windowchoices.clear();
        windowchoices.addAll(this.Windows);

        for (int int0 = 0; int0 < windowchoices.size(); int0++) {
            if (windowchoices.get(int0).getZ() > 0.0F) {
                windowchoices.remove(int0);
            }
        }

        return !windowchoices.isEmpty() ? windowchoices.get(Rand.Next(windowchoices.size())) : null;
    }

    public boolean isToxic() {
        return this.isToxic;
    }

    public void setToxic(boolean _isToxic) {
        this.isToxic = _isToxic;
    }

    /**
     * Check for player inside the house and awake them all
     */
    public void forceAwake() {
        for (int int0 = this.def.getX(); int0 <= this.def.getX2(); int0++) {
            for (int int1 = this.def.getY(); int1 <= this.def.getY2(); int1++) {
                for (int int2 = 0; int2 <= 4; int2++) {
                    IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
                    if (square != null) {
                        for (int int3 = 0; int3 < square.getMovingObjects().size(); int3++) {
                            if (square.getMovingObjects().get(int3) instanceof IsoGameCharacter) {
                                ((IsoGameCharacter)square.getMovingObjects().get(int3)).forceAwake();
                            }
                        }
                    }
                }
            }
        }
    }
}
