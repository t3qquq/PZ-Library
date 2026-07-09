// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;
import zombie.util.StringUtils;

public final class ClothingItemsDotTxt {
    public static final ClothingItemsDotTxt instance = new ClothingItemsDotTxt();
    private final StringBuilder buf = new StringBuilder();

    private int readBlock(String string0, int int1, ClothingItemsDotTxt.Block block1) {
        int int0;
        for (int0 = int1; int0 < string0.length(); int0++) {
            if (string0.charAt(int0) == '{') {
                ClothingItemsDotTxt.Block block0 = new ClothingItemsDotTxt.Block();
                block1.children.add(block0);
                block1.elements.add(block0);
                String string1 = string0.substring(int1, int0).trim();
                int int2 = string1.indexOf(32);
                int int3 = string1.indexOf(9);
                int int4 = Math.max(int2, int3);
                if (int4 == -1) {
                    block0.type = string1;
                } else {
                    block0.type = string1.substring(0, int4);
                    block0.id = string1.substring(int4).trim();
                }

                int0 = this.readBlock(string0, int0 + 1, block0);
                int1 = int0;
            } else {
                if (string0.charAt(int0) == '}') {
                    String string2 = string0.substring(int1, int0).trim();
                    if (!string2.isEmpty()) {
                        ClothingItemsDotTxt.Value value0 = new ClothingItemsDotTxt.Value();
                        value0.string = string0.substring(int1, int0).trim();
                        block1.values.add(value0.string);
                        block1.elements.add(value0);
                    }

                    return int0 + 1;
                }

                if (string0.charAt(int0) == ',') {
                    ClothingItemsDotTxt.Value value1 = new ClothingItemsDotTxt.Value();
                    value1.string = string0.substring(int1, int0).trim();
                    block1.values.add(value1.string);
                    block1.elements.add(value1);
                    int1 = int0 + 1;
                }
            }
        }

        return int0;
    }

    public void LoadFile() {
        String string0 = ZomboidFileSystem.instance.getString("media/scripts/clothingItems.txt");
        File file = new File(string0);
        if (file.exists()) {
            try (
                FileReader fileReader = new FileReader(string0);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
            ) {
                this.buf.setLength(0);

                String string1;
                while ((string1 = bufferedReader.readLine()) != null) {
                    this.buf.append(string1);
                }
            } catch (Throwable throwable0) {
                ExceptionLogger.logException(throwable0);
                return;
            }

            int int0 = this.buf.lastIndexOf("*/");

            while (int0 != -1) {
                int int1 = this.buf.lastIndexOf("/*", int0 - 1);
                if (int1 == -1) {
                    break;
                }

                int int2 = this.buf.lastIndexOf("*/", int0 - 1);

                while (int2 > int1) {
                    int int3 = int1;
                    String string2 = this.buf.substring(int1, int2 + 2);
                    int1 = this.buf.lastIndexOf("/*", int1 - 2);
                    if (int1 == -1) {
                        break;
                    }

                    int2 = this.buf.lastIndexOf("*/", int3 - 2);
                }

                if (int1 == -1) {
                    break;
                }

                String string3 = this.buf.substring(int1, int0 + 2);
                this.buf.replace(int1, int0 + 2, "");
                int0 = this.buf.lastIndexOf("*/", int1);
            }

            ClothingItemsDotTxt.Block block = new ClothingItemsDotTxt.Block();
            this.readBlock(this.buf.toString(), 0, block);
            Path path0 = FileSystems.getDefault().getPath("media/clothing/clothingItems");

            try (DirectoryStream directoryStream = Files.newDirectoryStream(path0)) {
                for (Path path1 : directoryStream) {
                    if (!Files.isDirectory(path1)) {
                        String string4 = path1.getFileName().toString();
                        if (string4.endsWith(".xml")) {
                            String string5 = StringUtils.trimSuffix(string4, ".xml");
                            System.out.println(string4 + " -> " + string5);
                            this.addClothingItem(string5, block.children.get(0));
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(block.children.get(0).toString());
            } catch (Throwable throwable1) {
                ExceptionLogger.logException(throwable1);
            }

            System.out.println(block.children.get(0));
        }
    }

    private void addClothingItem(String string, ClothingItemsDotTxt.Block block1) {
        if (!string.startsWith("FemaleHair_")) {
            if (!string.startsWith("MaleBeard_")) {
                if (!string.startsWith("MaleHair_")) {
                    if (!string.startsWith("ZedDmg_")) {
                        if (!string.startsWith("Bandage_")) {
                            if (!string.startsWith("Zed_Skin")) {
                                for (ClothingItemsDotTxt.Block block0 : block1.children) {
                                    if ("item".equals(block0.type) && string.equals(block0.id)) {
                                        return;
                                    }
                                }

                                ClothingItemsDotTxt.Block block2 = new ClothingItemsDotTxt.Block();
                                block2.type = "item";
                                block2.id = string;
                                ClothingItemsDotTxt.Value value = new ClothingItemsDotTxt.Value();
                                value.string = "Type = Clothing";
                                block2.elements.add(value);
                                block2.values.add(value.string);
                                value = new ClothingItemsDotTxt.Value();
                                value.string = "DisplayName = " + string;
                                block2.elements.add(value);
                                block2.values.add(value.string);
                                value = new ClothingItemsDotTxt.Value();
                                value.string = "ClothingItem = " + string;
                                block2.elements.add(value);
                                block2.values.add(value.string);
                                block1.elements.add(block2);
                                block1.children.add(block2);
                            }
                        }
                    }
                }
            }
        }
    }

    private static class Block implements ClothingItemsDotTxt.BlockElement {
        public String type;
        public String id;
        public ArrayList<ClothingItemsDotTxt.BlockElement> elements = new ArrayList<>();
        public ArrayList<String> values = new ArrayList<>();
        public ArrayList<ClothingItemsDotTxt.Block> children = new ArrayList<>();

        @Override
        public ClothingItemsDotTxt.Block asBlock() {
            return this;
        }

        @Override
        public ClothingItemsDotTxt.Value asValue() {
            return null;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.type + (this.id == null ? "" : " " + this.id) + "\n");
            stringBuilder.append("{\n");

            for (ClothingItemsDotTxt.BlockElement blockElement : this.elements) {
                String string0 = blockElement.toString();

                for (String string1 : string0.split("\n")) {
                    stringBuilder.append("\t" + string1 + "\n");
                }
            }

            stringBuilder.append("}\n");
            return stringBuilder.toString();
        }

        @Override
        public String toXML() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<Block type=\"" + this.type + "\" id=\"" + this.id + "\">\n");

            for (ClothingItemsDotTxt.BlockElement blockElement : this.elements) {
                String string0 = blockElement.toXML();

                for (String string1 : string0.split("\n")) {
                    stringBuilder.append("    " + string1 + "\n");
                }
            }

            stringBuilder.append("</Block>\n");
            return stringBuilder.toString();
        }
    }

    private interface BlockElement {
        ClothingItemsDotTxt.Block asBlock();

        ClothingItemsDotTxt.Value asValue();

        String toXML();
    }

    private static class Value implements ClothingItemsDotTxt.BlockElement {
        String string;

        @Override
        public ClothingItemsDotTxt.Block asBlock() {
            return null;
        }

        @Override
        public ClothingItemsDotTxt.Value asValue() {
            return this;
        }

        @Override
        public String toString() {
            return this.string + ",\n";
        }

        @Override
        public String toXML() {
            return "<Value>" + this.string + "</Value>\n";
        }
    }
}
