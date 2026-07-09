// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.world.logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import zombie.world.ItemInfo;

public class Log {
    public abstract static class BaseItemLog extends Log.BaseLog {
        protected final ItemInfo itemInfo;

        public BaseItemLog(ItemInfo itemInfox) {
            this.itemInfo = itemInfox;
        }

        @Override
        abstract void saveAsText(FileWriter var1, String var2) throws IOException;

        protected String getItemString() {
            return "fulltype = \""
                + this.itemInfo.getFullType()
                + "\", registeryID = "
                + this.itemInfo.getRegistryID()
                + ", existsVanilla = "
                + this.itemInfo.isExistsAsVanilla()
                + ", isModded = "
                + this.itemInfo.isModded()
                + ", modID = \""
                + this.itemInfo.getModID()
                + "\", obsolete = "
                + this.itemInfo.isObsolete()
                + ", removed = "
                + this.itemInfo.isRemoved()
                + ", isLoaded = "
                + this.itemInfo.isLoaded();
        }
    }

    public abstract static class BaseLog {
        protected boolean ignoreSaveCheck = false;

        public boolean isIgnoreSaveCheck() {
            return this.ignoreSaveCheck;
        }

        abstract void saveAsText(FileWriter var1, String var2) throws IOException;
    }

    public static class Comment extends Log.BaseLog {
        protected String txt;

        public Comment(String string) {
            this.ignoreSaveCheck = true;
            this.txt = string;
        }

        @Override
        public void saveAsText(FileWriter fileWriter, String string) throws IOException {
            fileWriter.write(string + "-- " + this.txt + System.lineSeparator());
        }
    }

    public static class Info extends Log.BaseLog {
        protected final List<String> mods;
        protected final String timeStamp;
        protected final String saveWorld;
        protected final int worldVersion;
        public boolean HasErrored = false;

        public Info(String string0, String string1, int int0, List<String> list) {
            this.ignoreSaveCheck = true;
            this.timeStamp = string0;
            this.saveWorld = string1;
            this.worldVersion = int0;
            this.mods = list;
        }

        @Override
        public void saveAsText(FileWriter fileWriter, String string) throws IOException {
            fileWriter.write(string + "{" + System.lineSeparator());
            fileWriter.write(string + "\ttype = \"info\"," + System.lineSeparator());
            fileWriter.write(string + "\ttimeStamp = \"" + this.timeStamp + "\"," + System.lineSeparator());
            fileWriter.write(string + "\tsaveWorld = \"" + this.saveWorld + "\"," + System.lineSeparator());
            fileWriter.write(string + "\tworldVersion = " + this.worldVersion + "," + System.lineSeparator());
            fileWriter.write(string + "\thasErrored = " + this.HasErrored + "," + System.lineSeparator());
            fileWriter.write(string + "\titemMods = {" + System.lineSeparator());

            for (int int0 = 0; int0 < this.mods.size(); int0++) {
                fileWriter.write(string + "\t\t\"" + this.mods.get(int0) + "\"," + System.lineSeparator());
            }

            fileWriter.write(string + "\t}," + System.lineSeparator());
            fileWriter.write(string + "}," + System.lineSeparator());
        }
    }

    public static class ModIDChangedItem extends Log.BaseItemLog {
        protected final String oldModID;
        protected final String newModID;

        public ModIDChangedItem(ItemInfo itemInfo, String string0, String string1) {
            super(itemInfo);
            this.oldModID = string0;
            this.newModID = string1;
        }

        @Override
        public void saveAsText(FileWriter fileWriter, String string) throws IOException {
            fileWriter.write(
                string + "{ type = \"modchange_item\", oldModID = \"" + this.oldModID + "\", " + this.getItemString() + " }" + System.lineSeparator()
            );
        }
    }

    public static class ObsoleteItem extends Log.BaseItemLog {
        public ObsoleteItem(ItemInfo itemInfo) {
            super(itemInfo);
        }

        @Override
        public void saveAsText(FileWriter fileWriter, String string) throws IOException {
            fileWriter.write(string + "{ type = \"obsolete_item\", " + this.getItemString() + " }" + System.lineSeparator());
        }
    }

    public static class RegisterItem extends Log.BaseItemLog {
        public RegisterItem(ItemInfo itemInfo) {
            super(itemInfo);
        }

        @Override
        public void saveAsText(FileWriter fileWriter, String string) throws IOException {
            fileWriter.write(string + "{ type = \"reg_item\", " + this.getItemString() + " }" + System.lineSeparator());
        }
    }

    public static class RegisterObject extends Log.BaseLog {
        protected final String objectName;
        protected final int ID;

        public RegisterObject(String string, int int0) {
            this.objectName = string;
            this.ID = int0;
        }

        @Override
        public void saveAsText(FileWriter fileWriter, String string) throws IOException {
            fileWriter.write(string + "{ type = \"reg_obj\", id = " + this.ID + ", obj = \"" + this.objectName + "\" }" + System.lineSeparator());
        }
    }

    public static class ReinstateItem extends Log.BaseItemLog {
        public ReinstateItem(ItemInfo itemInfo) {
            super(itemInfo);
        }

        @Override
        public void saveAsText(FileWriter fileWriter, String string) throws IOException {
            fileWriter.write(string + "{ type = \"reinstate_item\", " + this.getItemString() + " }" + System.lineSeparator());
        }
    }

    public static class RemovedItem extends Log.BaseItemLog {
        protected final boolean isScriptMissing;

        public RemovedItem(ItemInfo itemInfo, boolean boolean0) {
            super(itemInfo);
            this.isScriptMissing = boolean0;
        }

        @Override
        public void saveAsText(FileWriter fileWriter, String string) throws IOException {
            fileWriter.write(
                string + "{ type = \"removed_item\", scriptMissing = " + this.isScriptMissing + ", " + this.getItemString() + " }" + System.lineSeparator()
            );
        }
    }
}
