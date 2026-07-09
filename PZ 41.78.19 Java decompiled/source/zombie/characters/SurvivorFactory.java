// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.IsoCell;

public final class SurvivorFactory {
    public static final ArrayList<String> FemaleForenames = new ArrayList<>();
    public static final ArrayList<String> MaleForenames = new ArrayList<>();
    public static final ArrayList<String> Surnames = new ArrayList<>();

    public static void Reset() {
        FemaleForenames.clear();
        MaleForenames.clear();
        Surnames.clear();
        SurvivorDesc.HairCommonColors.clear();
        SurvivorDesc.TrouserCommonColors.clear();
    }

    public static SurvivorDesc[] CreateFamily(int nCount) {
        SurvivorDesc[] survivorDescs = new SurvivorDesc[nCount];

        for (int int0 = 0; int0 < nCount; int0++) {
            survivorDescs[int0] = CreateSurvivor();
            if (int0 > 0) {
                survivorDescs[int0].surname = survivorDescs[0].surname;
            }
        }

        return survivorDescs;
    }

    public static SurvivorDesc CreateSurvivor() {
        switch (Rand.Next(3)) {
            case 0:
                return CreateSurvivor(SurvivorFactory.SurvivorType.Friendly);
            case 1:
                return CreateSurvivor(SurvivorFactory.SurvivorType.Neutral);
            case 2:
                return CreateSurvivor(SurvivorFactory.SurvivorType.Aggressive);
            default:
                return null;
        }
    }

    public static SurvivorDesc CreateSurvivor(SurvivorFactory.SurvivorType survivorType, boolean bFemale) {
        SurvivorDesc survivorDesc = new SurvivorDesc();
        survivorDesc.setType(survivorType);
        IsoGameCharacter.getSurvivorMap().put(survivorDesc.ID, survivorDesc);
        survivorDesc.setFemale(bFemale);
        randomName(survivorDesc);
        if (survivorDesc.isFemale()) {
            setTorso(survivorDesc);
        } else {
            setTorso(survivorDesc);
        }

        return survivorDesc;
    }

    public static void setTorso(SurvivorDesc survivor) {
        if (survivor.isFemale()) {
            survivor.torso = "Kate";
        } else {
            survivor.torso = "Male";
        }
    }

    public static SurvivorDesc CreateSurvivor(SurvivorFactory.SurvivorType survivorType) {
        return CreateSurvivor(survivorType, Rand.Next(2) == 0);
    }

    public static SurvivorDesc[] CreateSurvivorGroup(int nCount) {
        SurvivorDesc[] survivorDescs = new SurvivorDesc[nCount];

        for (int int0 = 0; int0 < nCount; int0++) {
            survivorDescs[int0] = CreateSurvivor();
        }

        return survivorDescs;
    }

    public static IsoSurvivor InstansiateInCell(SurvivorDesc desc, IsoCell cell, int x, int y, int z) {
        desc.Instance = new IsoSurvivor(desc, cell, x, y, z);
        return (IsoSurvivor)desc.Instance;
    }

    public static void randomName(SurvivorDesc desc) {
        if (desc.isFemale()) {
            desc.forename = FemaleForenames.get(Rand.Next(FemaleForenames.size()));
        } else {
            desc.forename = MaleForenames.get(Rand.Next(MaleForenames.size()));
        }

        desc.surname = Surnames.get(Rand.Next(Surnames.size()));
    }

    public static void addSurname(String surName) {
        Surnames.add(surName);
    }

    public static void addFemaleForename(String forename) {
        FemaleForenames.add(forename);
    }

    public static void addMaleForename(String forename) {
        MaleForenames.add(forename);
    }

    public static String getRandomSurname() {
        return Surnames.get(Rand.Next(Surnames.size()));
    }

    public static String getRandomForename(boolean bFemale) {
        return bFemale ? FemaleForenames.get(Rand.Next(FemaleForenames.size())) : MaleForenames.get(Rand.Next(MaleForenames.size()));
    }

    public static enum SurvivorType {
        Friendly,
        Neutral,
        Aggressive;
    }
}
