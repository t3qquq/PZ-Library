// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedZoneStory;

import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;

public class RZSMusicFestStage extends RandomizedZoneStoryBase {
    public RZSMusicFestStage() {
        this.name = "Music Festival Stage";
        this.chance = 100;
        this.zoneType.add(RandomizedZoneStoryBase.ZoneType.MusicFestStage.toString());
        this.alwaysDo = true;
    }

    @Override
    public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
        for (int int0 = 0; int0 < 2; int0++) {
            int int1 = Rand.Next(0, 4);
            switch (int1) {
                case 0:
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarAcoustic");
                    break;
                case 1:
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricBlack");
                    break;
                case 2:
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricBlue");
                    break;
                case 3:
                    this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricRed");
            }
        }

        int int2 = Rand.Next(0, 3);
        switch (int2) {
            case 0:
                this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricBassBlack");
                break;
            case 1:
                this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricBassBlue");
                break;
            case 2:
                this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.GuitarElectricBassRed");
        }

        if (Rand.NextBool(6)) {
            this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.Keytar");
        }

        this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.Speaker");
        this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.Speaker");
        this.addItemOnGround(this.getRandomFreeSquareFullZone(this, zone), "Base.Drumstick");
        this.addZombiesOnSquare(1, "Punk", 0, this.getRandomFreeSquareFullZone(this, zone));
        this.addZombiesOnSquare(1, "Punk", 0, this.getRandomFreeSquareFullZone(this, zone));
        this.addZombiesOnSquare(1, "Punk", 0, this.getRandomFreeSquareFullZone(this, zone));
        this.addZombiesOnSquare(1, "Punk", 0, this.getRandomFreeSquareFullZone(this, zone));
        this.addZombiesOnSquare(1, "Punk", 100, this.getRandomFreeSquareFullZone(this, zone));
    }
}
