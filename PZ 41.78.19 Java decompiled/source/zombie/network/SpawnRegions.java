// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import se.krka.kahlua.vm.LuaClosure;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.debug.DebugLog;

public class SpawnRegions {
    private SpawnRegions.Region parseRegionTable(KahluaTable table) {
        Object object0 = table.rawget("name");
        Object object1 = table.rawget("file");
        Object object2 = table.rawget("serverfile");
        if (object0 instanceof String && object1 instanceof String) {
            SpawnRegions.Region region0 = new SpawnRegions.Region();
            region0.name = (String)object0;
            region0.file = (String)object1;
            return region0;
        } else if (object0 instanceof String && object2 instanceof String) {
            SpawnRegions.Region region1 = new SpawnRegions.Region();
            region1.name = (String)object0;
            region1.serverfile = (String)object2;
            return region1;
        } else {
            return null;
        }
    }

    private ArrayList<SpawnRegions.Profession> parseProfessionsTable(KahluaTable table) {
        ArrayList arrayList0 = null;
        KahluaTableIterator kahluaTableIterator = table.iterator();

        while (kahluaTableIterator.advance()) {
            Object object0 = kahluaTableIterator.getKey();
            Object object1 = kahluaTableIterator.getValue();
            if (object0 instanceof String && object1 instanceof KahluaTable) {
                ArrayList arrayList1 = this.parsePointsTable((KahluaTable)object1);
                if (arrayList1 != null) {
                    SpawnRegions.Profession profession = new SpawnRegions.Profession();
                    profession.name = (String)object0;
                    profession.points = arrayList1;
                    if (arrayList0 == null) {
                        arrayList0 = new ArrayList();
                    }

                    arrayList0.add(profession);
                }
            }
        }

        return arrayList0;
    }

    private ArrayList<SpawnRegions.Point> parsePointsTable(KahluaTable table) {
        ArrayList arrayList = null;
        KahluaTableIterator kahluaTableIterator = table.iterator();

        while (kahluaTableIterator.advance()) {
            Object object = kahluaTableIterator.getValue();
            if (object instanceof KahluaTable) {
                SpawnRegions.Point point = this.parsePointTable((KahluaTable)object);
                if (point != null) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }

                    arrayList.add(point);
                }
            }
        }

        return arrayList;
    }

    private SpawnRegions.Point parsePointTable(KahluaTable table) {
        Object object0 = table.rawget("worldX");
        Object object1 = table.rawget("worldY");
        Object object2 = table.rawget("posX");
        Object object3 = table.rawget("posY");
        Object object4 = table.rawget("posZ");
        if (object0 instanceof Double && object1 instanceof Double && object2 instanceof Double && object3 instanceof Double) {
            SpawnRegions.Point point = new SpawnRegions.Point();
            point.worldX = ((Double)object0).intValue();
            point.worldY = ((Double)object1).intValue();
            point.posX = ((Double)object2).intValue();
            point.posY = ((Double)object3).intValue();
            point.posZ = object4 instanceof Double ? ((Double)object4).intValue() : 0;
            return point;
        } else {
            return null;
        }
    }

    public ArrayList<SpawnRegions.Region> loadRegionsFile(String string) {
        File file = new File(string);
        if (!file.exists()) {
            return null;
        } else {
            try {
                LuaManager.env.rawset("SpawnRegions", null);
                LuaManager.loaded.remove(file.getAbsolutePath().replace("\\", "/"));
                LuaManager.RunLua(file.getAbsolutePath());
                Object object0 = LuaManager.env.rawget("SpawnRegions");
                if (object0 instanceof LuaClosure) {
                    Object[] objects = LuaManager.caller.pcall(LuaManager.thread, object0);
                    if (objects.length > 1 && objects[1] instanceof KahluaTable) {
                        ArrayList arrayList = new ArrayList();
                        KahluaTableIterator kahluaTableIterator = ((KahluaTable)objects[1]).iterator();

                        while (kahluaTableIterator.advance()) {
                            Object object1 = kahluaTableIterator.getValue();
                            if (object1 instanceof KahluaTable) {
                                SpawnRegions.Region region = this.parseRegionTable((KahluaTable)object1);
                                if (region != null) {
                                    arrayList.add(region);
                                }
                            }
                        }

                        return arrayList;
                    }
                }

                return null;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    private String fmtKey(String string) {
        if (string.contains("\\")) {
            string = string.replace("\\", "\\\\");
        }

        if (string.contains("\"")) {
            string = string.replace("\"", "\\\"");
        }

        if (string.contains(" ") || string.contains("\\")) {
            string = "\"" + string + "\"";
        }

        return string.startsWith("\"") ? "[" + string + "]" : string;
    }

    private String fmtValue(String string) {
        if (string.contains("\\")) {
            string = string.replace("\\", "\\\\");
        }

        if (string.contains("\"")) {
            string = string.replace("\"", "\\\"");
        }

        return "\"" + string + "\"";
    }

    public boolean saveRegionsFile(String string0, ArrayList<SpawnRegions.Region> arrayList) {
        File file = new File(string0);
        DebugLog.log("writing " + string0);

        try {
            boolean boolean0;
            try (FileWriter fileWriter = new FileWriter(file)) {
                String string1 = System.lineSeparator();
                fileWriter.write("function SpawnRegions()" + string1);
                fileWriter.write("\treturn {" + string1);

                for (SpawnRegions.Region region : arrayList) {
                    if (region.file != null) {
                        fileWriter.write("\t\t{ name = " + this.fmtValue(region.name) + ", file = " + this.fmtValue(region.file) + " }," + string1);
                    } else if (region.serverfile != null) {
                        fileWriter.write("\t\t{ name = " + this.fmtValue(region.name) + ", serverfile = " + this.fmtValue(region.serverfile) + " }," + string1);
                    } else if (region.professions != null) {
                        fileWriter.write("\t\t{ name = " + this.fmtValue(region.name) + "," + string1);
                        fileWriter.write("\t\t\tpoints = {" + string1);

                        for (SpawnRegions.Profession profession : region.professions) {
                            fileWriter.write("\t\t\t\t" + this.fmtKey(profession.name) + " = {" + string1);

                            for (SpawnRegions.Point point : profession.points) {
                                fileWriter.write(
                                    "\t\t\t\t\t{ worldX = "
                                        + point.worldX
                                        + ", worldY = "
                                        + point.worldY
                                        + ", posX = "
                                        + point.posX
                                        + ", posY = "
                                        + point.posY
                                        + ", posZ = "
                                        + point.posZ
                                        + " },"
                                        + string1
                                );
                            }

                            fileWriter.write("\t\t\t\t}," + string1);
                        }

                        fileWriter.write("\t\t\t}" + string1);
                        fileWriter.write("\t\t}," + string1);
                    }
                }

                fileWriter.write("\t}" + string1);
                fileWriter.write("end" + System.lineSeparator());
                boolean0 = true;
            }

            return boolean0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public ArrayList<SpawnRegions.Profession> loadPointsFile(String string) {
        File file = new File(string);
        if (!file.exists()) {
            return null;
        } else {
            try {
                LuaManager.env.rawset("SpawnPoints", null);
                LuaManager.loaded.remove(file.getAbsolutePath().replace("\\", "/"));
                LuaManager.RunLua(file.getAbsolutePath());
                Object object = LuaManager.env.rawget("SpawnPoints");
                if (object instanceof LuaClosure) {
                    Object[] objects = LuaManager.caller.pcall(LuaManager.thread, object);
                    if (objects.length > 1 && objects[1] instanceof KahluaTable) {
                        return this.parseProfessionsTable((KahluaTable)objects[1]);
                    }
                }

                return null;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    public boolean savePointsFile(String string0, ArrayList<SpawnRegions.Profession> arrayList) {
        File file = new File(string0);
        DebugLog.log("writing " + string0);

        try {
            boolean boolean0;
            try (FileWriter fileWriter = new FileWriter(file)) {
                String string1 = System.lineSeparator();
                fileWriter.write("function SpawnPoints()" + string1);
                fileWriter.write("\treturn {" + string1);

                for (SpawnRegions.Profession profession : arrayList) {
                    fileWriter.write("\t\t" + this.fmtKey(profession.name) + " = {" + string1);

                    for (SpawnRegions.Point point : profession.points) {
                        fileWriter.write(
                            "\t\t\t{ worldX = "
                                + point.worldX
                                + ", worldY = "
                                + point.worldY
                                + ", posX = "
                                + point.posX
                                + ", posY = "
                                + point.posY
                                + ", posZ = "
                                + point.posZ
                                + " },"
                                + string1
                        );
                    }

                    fileWriter.write("\t\t}," + string1);
                }

                fileWriter.write("\t}" + string1);
                fileWriter.write("end" + System.lineSeparator());
                boolean0 = true;
            }

            return boolean0;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public KahluaTable loadPointsTable(String string) {
        ArrayList arrayList = this.loadPointsFile(string);
        if (arrayList == null) {
            return null;
        } else {
            KahluaTable table0 = LuaManager.platform.newTable();

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                SpawnRegions.Profession profession = (SpawnRegions.Profession)arrayList.get(int0);
                KahluaTable table1 = LuaManager.platform.newTable();

                for (int int1 = 0; int1 < profession.points.size(); int1++) {
                    SpawnRegions.Point point = profession.points.get(int1);
                    KahluaTable table2 = LuaManager.platform.newTable();
                    table2.rawset("worldX", (double)point.worldX);
                    table2.rawset("worldY", (double)point.worldY);
                    table2.rawset("posX", (double)point.posX);
                    table2.rawset("posY", (double)point.posY);
                    table2.rawset("posZ", (double)point.posZ);
                    table1.rawset(int1 + 1, table2);
                }

                table0.rawset(profession.name, table1);
            }

            return table0;
        }
    }

    public boolean savePointsTable(String string, KahluaTable table) {
        ArrayList arrayList = this.parseProfessionsTable(table);
        return arrayList != null ? this.savePointsFile(string, arrayList) : false;
    }

    public ArrayList<SpawnRegions.Region> getDefaultServerRegions() {
        ArrayList arrayList = new ArrayList();
        Filter filter = new Filter<Path>() {
            public boolean accept(Path path) throws IOException {
                return Files.isDirectory(path) && Files.exists(path.resolve("spawnpoints.lua"));
            }
        };
        String string = ZomboidFileSystem.instance.getMediaPath("maps");
        Path path0 = FileSystems.getDefault().getPath(string);
        if (!Files.exists(path0)) {
            return arrayList;
        } else {
            try (DirectoryStream directoryStream = Files.newDirectoryStream(path0, filter)) {
                for (Path path1 : directoryStream) {
                    SpawnRegions.Region region = new SpawnRegions.Region();
                    region.name = path1.getFileName().toString();
                    region.file = "media/maps/" + region.name + "/spawnpoints.lua";
                    arrayList.add(region);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return arrayList;
        }
    }

    public ArrayList<SpawnRegions.Profession> getDefaultServerPoints() {
        ArrayList arrayList = new ArrayList();
        SpawnRegions.Profession profession = new SpawnRegions.Profession();
        profession.name = "unemployed";
        profession.points = new ArrayList<>();
        arrayList.add(profession);
        SpawnRegions.Point point = new SpawnRegions.Point();
        point.worldX = 40;
        point.worldY = 22;
        point.posX = 67;
        point.posY = 201;
        point.posZ = 0;
        profession.points.add(point);
        return arrayList;
    }

    public static class Point {
        public int worldX;
        public int worldY;
        public int posX;
        public int posY;
        public int posZ;
    }

    public static class Profession {
        public String name;
        public ArrayList<SpawnRegions.Point> points;
    }

    public static class Region {
        public String name;
        public String file;
        public String serverfile;
        public ArrayList<SpawnRegions.Profession> professions;
    }
}
