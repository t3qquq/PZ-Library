// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.world;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import zombie.GameWindow;
import zombie.core.utils.Bits;
import zombie.debug.DebugLog;
import zombie.scripting.objects.Item;

public class ItemInfo {
    protected String itemName;
    protected String moduleName;
    protected String fullType;
    protected short registryID;
    protected boolean existsAsVanilla = false;
    protected boolean isModded = false;
    protected String modID;
    protected boolean obsolete = false;
    protected boolean removed = false;
    protected boolean isLoaded = false;
    protected List<String> modOverrides;
    protected Item scriptItem;

    public String getFullType() {
        return this.fullType;
    }

    public short getRegistryID() {
        return this.registryID;
    }

    public boolean isExistsAsVanilla() {
        return this.existsAsVanilla;
    }

    public boolean isModded() {
        return this.isModded;
    }

    public String getModID() {
        return this.modID;
    }

    public boolean isObsolete() {
        return this.obsolete;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public boolean isLoaded() {
        return this.isLoaded;
    }

    public Item getScriptItem() {
        return this.scriptItem;
    }

    public List<String> getModOverrides() {
        return this.modOverrides;
    }

    public ItemInfo copy() {
        ItemInfo itemInfo0 = new ItemInfo();
        itemInfo0.fullType = this.fullType;
        itemInfo0.registryID = this.registryID;
        itemInfo0.existsAsVanilla = this.existsAsVanilla;
        itemInfo0.isModded = this.isModded;
        itemInfo0.modID = this.modID;
        itemInfo0.obsolete = this.obsolete;
        itemInfo0.removed = this.removed;
        itemInfo0.isLoaded = this.isLoaded;
        itemInfo0.scriptItem = this.scriptItem;
        if (this.modOverrides != null) {
            itemInfo0.modOverrides = new ArrayList<>();
            itemInfo0.modOverrides.addAll(this.modOverrides);
        }

        return itemInfo0;
    }

    public boolean isValid() {
        return !this.obsolete && !this.removed && this.isLoaded;
    }

    public void DebugPrint() {
        DebugLog.log(this.GetDebugString());
    }

    public String GetDebugString() {
        String string = "=== Dictionary Item Debug Print ===\nregistryID = "
            + this.registryID
            + ",\nfulltype = \""
            + this.fullType
            + "\",\nmodID = \""
            + this.modID
            + "\",\nexistsAsVanilla = "
            + this.existsAsVanilla
            + ",\nisModded = "
            + this.isModded
            + ",\nobsolete = "
            + this.obsolete
            + ",\nremoved = "
            + this.removed
            + ",\nisModdedOverride = "
            + (this.modOverrides != null ? this.modOverrides.size() : 0)
            + ",\n";
        if (this.modOverrides != null) {
            string = string + "modOverrides = { ";
            if (this.existsAsVanilla) {
                string = string + "PZ-Vanilla, ";
            }

            for (int int0 = 0; int0 < this.modOverrides.size(); int0++) {
                string = string + "\"" + this.modOverrides.get(int0) + "\"";
                if (int0 < this.modOverrides.size() - 1) {
                    string = string + ", ";
                }
            }

            string = string + " },\n";
        }

        return string + "===================================\n";
    }

    public String ToString() {
        return "registryID = "
            + this.registryID
            + ",fulltype = \""
            + this.fullType
            + "\",modID = \""
            + this.modID
            + "\",existsAsVanilla = "
            + this.existsAsVanilla
            + ",isModded = "
            + this.isModded
            + ",obsolete = "
            + this.obsolete
            + ",removed = "
            + this.removed
            + ",modOverrides = "
            + (this.modOverrides != null ? this.modOverrides.size() : 0)
            + ",";
    }

    protected void saveAsText(FileWriter fileWriter, String string0) throws IOException {
        fileWriter.write(string0 + "registryID = " + this.registryID + "," + System.lineSeparator());
        fileWriter.write(string0 + "fulltype = \"" + this.fullType + "\"," + System.lineSeparator());
        fileWriter.write(string0 + "modID = \"" + this.modID + "\"," + System.lineSeparator());
        fileWriter.write(string0 + "existsAsVanilla = " + this.existsAsVanilla + "," + System.lineSeparator());
        fileWriter.write(string0 + "isModded = " + this.isModded + "," + System.lineSeparator());
        fileWriter.write(string0 + "obsolete = " + this.obsolete + "," + System.lineSeparator());
        fileWriter.write(string0 + "removed = " + this.removed + "," + System.lineSeparator());
        if (this.modOverrides != null) {
            String string1 = "modOverrides = { ";

            for (int int0 = 0; int0 < this.modOverrides.size(); int0++) {
                string1 = string1 + "\"" + this.modOverrides.get(int0) + "\"";
                if (int0 < this.modOverrides.size() - 1) {
                    string1 = string1 + ", ";
                }
            }

            string1 = string1 + " },";
            fileWriter.write(string0 + string1 + System.lineSeparator());
        }
    }

    protected void save(ByteBuffer byteBuffer, List<String> list1, List<String> list0) {
        byteBuffer.putShort(this.registryID);
        if (list0.size() > 127) {
            byteBuffer.putShort((short)list0.indexOf(this.moduleName));
        } else {
            byteBuffer.put((byte)list0.indexOf(this.moduleName));
        }

        GameWindow.WriteString(byteBuffer, this.itemName);
        byte byte0 = 0;
        int int0 = byteBuffer.position();
        byteBuffer.put((byte)0);
        if (this.isModded) {
            byte0 = Bits.addFlags(byte0, 1);
            if (list1.size() > 127) {
                byteBuffer.putShort((short)list1.indexOf(this.modID));
            } else {
                byteBuffer.put((byte)list1.indexOf(this.modID));
            }
        }

        if (this.existsAsVanilla) {
            byte0 = Bits.addFlags(byte0, 2);
        }

        if (this.obsolete) {
            byte0 = Bits.addFlags(byte0, 4);
        }

        if (this.removed) {
            byte0 = Bits.addFlags(byte0, 8);
        }

        if (this.modOverrides != null) {
            byte0 = Bits.addFlags(byte0, 16);
            if (this.modOverrides.size() == 1) {
                if (list1.size() > 127) {
                    byteBuffer.putShort((short)list1.indexOf(this.modOverrides.get(0)));
                } else {
                    byteBuffer.put((byte)list1.indexOf(this.modOverrides.get(0)));
                }
            } else {
                byte0 = Bits.addFlags(byte0, 32);
                byteBuffer.put((byte)this.modOverrides.size());

                for (int int1 = 0; int1 < this.modOverrides.size(); int1++) {
                    if (list1.size() > 127) {
                        byteBuffer.putShort((short)list1.indexOf(this.modOverrides.get(int1)));
                    } else {
                        byteBuffer.put((byte)list1.indexOf(this.modOverrides.get(int1)));
                    }
                }
            }
        }

        int int2 = byteBuffer.position();
        byteBuffer.position(int0);
        byteBuffer.put(byte0);
        byteBuffer.position(int2);
    }

    protected void load(ByteBuffer byteBuffer, int var2, List<String> list1, List<String> list0) {
        this.registryID = byteBuffer.getShort();
        this.moduleName = (String)list0.get(list0.size() > 127 ? byteBuffer.getShort() : byteBuffer.get());
        this.itemName = GameWindow.ReadString(byteBuffer);
        this.fullType = this.moduleName + "." + this.itemName;
        byte byte0 = byteBuffer.get();
        if (Bits.hasFlags(byte0, 1)) {
            this.modID = (String)list1.get(list1.size() > 127 ? byteBuffer.getShort() : byteBuffer.get());
            this.isModded = true;
        } else {
            this.modID = "pz-vanilla";
            this.isModded = false;
        }

        this.existsAsVanilla = Bits.hasFlags(byte0, 2);
        this.obsolete = Bits.hasFlags(byte0, 4);
        this.removed = Bits.hasFlags(byte0, 8);
        if (Bits.hasFlags(byte0, 16)) {
            if (this.modOverrides == null) {
                this.modOverrides = new ArrayList<>();
            }

            this.modOverrides.clear();
            if (!Bits.hasFlags(byte0, 32)) {
                this.modOverrides.add((String)list1.get(list1.size() > 127 ? byteBuffer.getShort() : byteBuffer.get()));
            } else {
                byte byte1 = byteBuffer.get();

                for (int int0 = 0; int0 < byte1; int0++) {
                    this.modOverrides.add((String)list1.get(list1.size() > 127 ? byteBuffer.getShort() : byteBuffer.get()));
                }
            }
        }
    }
}
