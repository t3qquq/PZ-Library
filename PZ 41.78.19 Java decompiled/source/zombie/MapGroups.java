// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.IsoWorld;
import zombie.modding.ActiveMods;

public final class MapGroups {
    private final ArrayList<MapGroups.MapGroup> groups = new ArrayList<>();
    private final ArrayList<MapGroups.MapDirectory> realDirectories = new ArrayList<>();

    private static ArrayList<String> getVanillaMapDirectories(boolean boolean0) {
        ArrayList arrayList = new ArrayList();
        File file = ZomboidFileSystem.instance.getMediaFile("maps");
        String[] strings = file.list();
        if (strings != null) {
            for (int int0 = 0; int0 < strings.length; int0++) {
                String string = strings[int0];
                if (string.equalsIgnoreCase("challengemaps")) {
                    if (boolean0) {
                        try (DirectoryStream directoryStream = Files.newDirectoryStream(
                                Paths.get(file.getPath(), string), pathx -> Files.isDirectory(pathx) && Files.exists(pathx.resolve("map.info"))
                            )) {
                            for (Path path : directoryStream) {
                                arrayList.add(string + "/" + path.getFileName().toString());
                            }
                        } catch (Exception exception) {
                        }
                    }
                } else {
                    arrayList.add(string);
                }
            }
        }

        return arrayList;
    }

    public static String addMissingVanillaDirectories(String mapName) {
        ArrayList arrayList0 = getVanillaMapDirectories(false);
        boolean boolean0 = false;
        String[] strings = mapName.split(";");

        for (String string0 : strings) {
            string0 = string0.trim();
            if (!string0.isEmpty() && arrayList0.contains(string0)) {
                boolean0 = true;
                break;
            }
        }

        if (!boolean0) {
            return mapName;
        } else {
            ArrayList arrayList1 = new ArrayList();

            for (String string1 : strings) {
                string1 = string1.trim();
                if (!string1.isEmpty()) {
                    arrayList1.add(string1);
                }
            }

            for (String string2 : arrayList0) {
                if (!arrayList1.contains(string2)) {
                    arrayList1.add(string2);
                }
            }

            String string3 = "";

            for (String string4 : arrayList1) {
                if (!string3.isEmpty()) {
                    string3 = string3 + ";";
                }

                string3 = string3 + string4;
            }

            return string3;
        }
    }

    public void createGroups() {
        this.createGroups(ActiveMods.getById("currentGame"), true);
    }

    public void createGroups(ActiveMods activeMods, boolean includeVanilla) {
        this.createGroups(activeMods, includeVanilla, false);
    }

    public void createGroups(ActiveMods activeMods, boolean includeVanilla, boolean includeChallenges) {
        this.groups.clear();
        this.realDirectories.clear();

        for (String string0 : activeMods.getMods()) {
            ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string0);
            if (mod != null) {
                File file = new File(mod.getDir() + "/media/maps/");
                if (file.exists()) {
                    String[] strings = file.list();
                    if (strings != null) {
                        for (int int0 = 0; int0 < strings.length; int0++) {
                            String string1 = strings[int0];
                            if (string1.equalsIgnoreCase("challengemaps")) {
                                if (includeChallenges) {
                                }
                            } else {
                                this.handleMapDirectory(string1, mod.getDir() + "/media/maps/" + string1);
                            }
                        }
                    }
                }
            }
        }

        if (includeVanilla) {
            ArrayList arrayList0 = getVanillaMapDirectories(includeChallenges);
            String string2 = ZomboidFileSystem.instance.getMediaPath("maps");

            for (String string3 : arrayList0) {
                this.handleMapDirectory(string3, string2 + File.separator + string3);
            }
        }

        for (MapGroups.MapDirectory mapDirectory0 : this.realDirectories) {
            ArrayList arrayList1 = new ArrayList();
            this.getDirsRecursively(mapDirectory0, arrayList1);
            MapGroups.MapGroup mapGroup0 = this.findGroupWithAnyOfTheseDirectories(arrayList1);
            if (mapGroup0 == null) {
                mapGroup0 = new MapGroups.MapGroup();
                this.groups.add(mapGroup0);
            }

            for (MapGroups.MapDirectory mapDirectory1 : arrayList1) {
                if (!mapGroup0.hasDirectory(mapDirectory1.name)) {
                    mapGroup0.addDirectory(mapDirectory1);
                }
            }
        }

        for (MapGroups.MapGroup mapGroup1 : this.groups) {
            mapGroup1.setPriority();
        }

        for (MapGroups.MapGroup mapGroup2 : this.groups) {
            mapGroup2.setOrder(activeMods);
        }

        if (Core.bDebug) {
            int int1 = 1;

            for (MapGroups.MapGroup mapGroup3 : this.groups) {
                DebugLog.log("MapGroup " + int1 + "/" + this.groups.size());

                for (MapGroups.MapDirectory mapDirectory2 : mapGroup3.directories) {
                    DebugLog.log("  " + mapDirectory2.name);
                }

                int1++;
            }

            DebugLog.log("-----");
        }
    }

    private void getDirsRecursively(MapGroups.MapDirectory mapDirectory0, ArrayList<MapGroups.MapDirectory> arrayList) {
        if (!arrayList.contains(mapDirectory0)) {
            arrayList.add(mapDirectory0);

            for (String string : mapDirectory0.lotDirs) {
                for (MapGroups.MapDirectory mapDirectory1 : this.realDirectories) {
                    if (mapDirectory1.name.equals(string)) {
                        this.getDirsRecursively(mapDirectory1, arrayList);
                        break;
                    }
                }
            }
        }
    }

    public int getNumberOfGroups() {
        return this.groups.size();
    }

    public ArrayList<String> getMapDirectoriesInGroup(int groupIndex) {
        if (groupIndex >= 0 && groupIndex < this.groups.size()) {
            ArrayList arrayList = new ArrayList();

            for (MapGroups.MapDirectory mapDirectory : this.groups.get(groupIndex).directories) {
                arrayList.add(mapDirectory.name);
            }

            return arrayList;
        } else {
            throw new RuntimeException("invalid MapGroups index " + groupIndex);
        }
    }

    public void setWorld(int groupIndex) {
        ArrayList arrayList = this.getMapDirectoriesInGroup(groupIndex);
        String string = "";

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            string = string + (String)arrayList.get(int0);
            if (int0 < arrayList.size() - 1) {
                string = string + ";";
            }
        }

        IsoWorld.instance.setMap(string);
    }

    private void handleMapDirectory(String string1, String string0) {
        ArrayList arrayList = this.getLotDirectories(string0);
        if (arrayList != null) {
            MapGroups.MapDirectory mapDirectory = new MapGroups.MapDirectory(string1, string0, arrayList);
            this.realDirectories.add(mapDirectory);
        }
    }

    private ArrayList<String> getLotDirectories(String string0) {
        File file = new File(string0 + "/map.info");
        if (!file.exists()) {
            return null;
        } else {
            ArrayList arrayList = new ArrayList();

            try {
                String string1;
                try (
                    FileReader fileReader = new FileReader(file.getAbsolutePath());
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                ) {
                    while ((string1 = bufferedReader.readLine()) != null) {
                        string1 = string1.trim();
                        if (string1.startsWith("lots=")) {
                            arrayList.add(string1.replace("lots=", "").trim());
                        }
                    }
                }

                return arrayList;
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                return null;
            }
        }
    }

    private MapGroups.MapGroup findGroupWithAnyOfTheseDirectories(ArrayList<MapGroups.MapDirectory> arrayList) {
        for (MapGroups.MapGroup mapGroup : this.groups) {
            if (mapGroup.hasAnyOfTheseDirectories(arrayList)) {
                return mapGroup;
            }
        }

        return null;
    }

    public ArrayList<String> getAllMapsInOrder() {
        ArrayList arrayList = new ArrayList();

        for (MapGroups.MapGroup mapGroup : this.groups) {
            for (MapGroups.MapDirectory mapDirectory : mapGroup.directories) {
                arrayList.add(mapDirectory.name);
            }
        }

        return arrayList;
    }

    public boolean checkMapConflicts() {
        boolean boolean0 = false;

        for (MapGroups.MapGroup mapGroup : this.groups) {
            boolean0 |= mapGroup.checkMapConflicts();
        }

        return boolean0;
    }

    public ArrayList<String> getMapConflicts(String mapName) {
        for (MapGroups.MapGroup mapGroup : this.groups) {
            MapGroups.MapDirectory mapDirectory = mapGroup.getDirectoryByName(mapName);
            if (mapDirectory != null) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(mapDirectory.conflicts);
                return arrayList;
            }
        }

        return null;
    }

    private class MapDirectory {
        String name;
        String path;
        ArrayList<String> lotDirs = new ArrayList<>();
        ArrayList<String> conflicts = new ArrayList<>();

        public MapDirectory(String string0, String string1) {
            this.name = string0;
            this.path = string1;
        }

        public MapDirectory(String string0, String string1, ArrayList<String> arrayList) {
            this.name = string0;
            this.path = string1;
            this.lotDirs.addAll(arrayList);
        }

        public void getLotHeaders(ArrayList<String> arrayList) {
            File file = new File(this.path);
            if (file.isDirectory()) {
                String[] strings = file.list();
                if (strings != null) {
                    for (int int0 = 0; int0 < strings.length; int0++) {
                        if (strings[int0].endsWith(".lotheader")) {
                            arrayList.add(strings[int0]);
                        }
                    }
                }
            }
        }
    }

    private class MapGroup {
        private LinkedList<MapGroups.MapDirectory> directories = new LinkedList<>();

        void addDirectory(String string0, String string1) {
            assert !this.hasDirectory(string0);

            MapGroups.MapDirectory mapDirectory = MapGroups.this.new MapDirectory(string0, string1);
            this.directories.add(mapDirectory);
        }

        void addDirectory(String string0, String string1, ArrayList<String> arrayList) {
            assert !this.hasDirectory(string0);

            MapGroups.MapDirectory mapDirectory = MapGroups.this.new MapDirectory(string0, string1, arrayList);
            this.directories.add(mapDirectory);
        }

        void addDirectory(MapGroups.MapDirectory mapDirectory) {
            assert !this.hasDirectory(mapDirectory.name);

            this.directories.add(mapDirectory);
        }

        MapGroups.MapDirectory getDirectoryByName(String string) {
            for (MapGroups.MapDirectory mapDirectory : this.directories) {
                if (mapDirectory.name.equals(string)) {
                    return mapDirectory;
                }
            }

            return null;
        }

        boolean hasDirectory(String string) {
            return this.getDirectoryByName(string) != null;
        }

        boolean hasAnyOfTheseDirectories(ArrayList<MapGroups.MapDirectory> arrayList) {
            for (MapGroups.MapDirectory mapDirectory : arrayList) {
                if (this.directories.contains(mapDirectory)) {
                    return true;
                }
            }

            return false;
        }

        boolean isReferencedByOtherMaps(MapGroups.MapDirectory mapDirectory1) {
            for (MapGroups.MapDirectory mapDirectory0 : this.directories) {
                if (mapDirectory1 != mapDirectory0 && mapDirectory0.lotDirs.contains(mapDirectory1.name)) {
                    return true;
                }
            }

            return false;
        }

        void getDirsRecursively(MapGroups.MapDirectory mapDirectory0, ArrayList<String> arrayList) {
            if (!arrayList.contains(mapDirectory0.name)) {
                arrayList.add(mapDirectory0.name);

                for (String string : mapDirectory0.lotDirs) {
                    MapGroups.MapDirectory mapDirectory1 = this.getDirectoryByName(string);
                    if (mapDirectory1 != null) {
                        this.getDirsRecursively(mapDirectory1, arrayList);
                    }
                }
            }
        }

        void setPriority() {
            for (MapGroups.MapDirectory mapDirectory : new ArrayList<>(this.directories)) {
                if (!this.isReferencedByOtherMaps(mapDirectory)) {
                    ArrayList arrayList = new ArrayList();
                    this.getDirsRecursively(mapDirectory, arrayList);
                    this.setPriority(arrayList);
                }
            }
        }

        void setPriority(List<String> list) {
            ArrayList arrayList = new ArrayList(list.size());

            for (String string : list) {
                if (this.hasDirectory(string)) {
                    arrayList.add(this.getDirectoryByName(string));
                }
            }

            for (int int0 = 0; int0 < this.directories.size(); int0++) {
                MapGroups.MapDirectory mapDirectory = this.directories.get(int0);
                if (list.contains(mapDirectory.name)) {
                    this.directories.set(int0, (MapGroups.MapDirectory)arrayList.remove(0));
                }
            }
        }

        void setOrder(ActiveMods activeMods) {
            if (!activeMods.getMapOrder().isEmpty()) {
                this.setPriority(activeMods.getMapOrder());
            }
        }

        boolean checkMapConflicts() {
            HashMap hashMap = new HashMap();
            ArrayList arrayList0 = new ArrayList();

            for (MapGroups.MapDirectory mapDirectory0 : this.directories) {
                mapDirectory0.conflicts.clear();
                arrayList0.clear();
                mapDirectory0.getLotHeaders(arrayList0);

                for (String string0 : arrayList0) {
                    if (!hashMap.containsKey(string0)) {
                        hashMap.put(string0, new ArrayList());
                    }

                    ((ArrayList)hashMap.get(string0)).add(mapDirectory0.name);
                }
            }

            boolean boolean0 = false;

            for (String string1 : hashMap.keySet()) {
                ArrayList arrayList1 = (ArrayList)hashMap.get(string1);
                if (arrayList1.size() > 1) {
                    for (int int0 = 0; int0 < arrayList1.size(); int0++) {
                        MapGroups.MapDirectory mapDirectory1 = this.getDirectoryByName((String)arrayList1.get(int0));

                        for (int int1 = 0; int1 < arrayList1.size(); int1++) {
                            if (int0 != int1) {
                                String string2 = Translator.getText("UI_MapConflict", mapDirectory1.name, arrayList1.get(int1), string1);
                                mapDirectory1.conflicts.add(string2);
                                boolean0 = true;
                            }
                        }
                    }
                }
            }

            return boolean0;
        }
    }
}
