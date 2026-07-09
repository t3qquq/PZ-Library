// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting;

import java.util.ArrayList;

public final class ScriptParser {
    private static StringBuilder stringBuilder = new StringBuilder();

    public static int readBlock(String string0, int int1, ScriptParser.Block block1) {
        int int0;
        for (int0 = int1; int0 < string0.length(); int0++) {
            if (string0.charAt(int0) == '{') {
                ScriptParser.Block block0 = new ScriptParser.Block();
                block1.children.add(block0);
                block1.elements.add(block0);
                String string1 = string0.substring(int1, int0).trim();
                String[] strings = string1.split("\\s+");
                block0.type = strings[0];
                block0.id = strings.length > 1 ? strings[1] : null;
                int0 = readBlock(string0, int0 + 1, block0);
                int1 = int0;
            } else {
                if (string0.charAt(int0) == '}') {
                    return int0 + 1;
                }

                if (string0.charAt(int0) == ',') {
                    ScriptParser.Value value = new ScriptParser.Value();
                    value.string = string0.substring(int1, int0);
                    block1.values.add(value);
                    block1.elements.add(value);
                    int1 = int0 + 1;
                }
            }
        }

        return int0;
    }

    public static ScriptParser.Block parse(String string) {
        ScriptParser.Block block = new ScriptParser.Block();
        readBlock(string, 0, block);
        return block;
    }

    public static String stripComments(String string) {
        stringBuilder.setLength(0);
        stringBuilder.append(string);
        int int0 = stringBuilder.lastIndexOf("*/");

        while (int0 != -1) {
            int int1 = stringBuilder.lastIndexOf("/*", int0 - 1);
            if (int1 == -1) {
                break;
            }

            int int2 = stringBuilder.lastIndexOf("*/", int0 - 1);

            while (int2 > int1) {
                int int3 = int1;
                int1 = stringBuilder.lastIndexOf("/*", int1 - 2);
                if (int1 == -1) {
                    break;
                }

                int2 = stringBuilder.lastIndexOf("*/", int3 - 2);
            }

            if (int1 == -1) {
                break;
            }

            stringBuilder.replace(int1, int0 + 2, "");
            int0 = stringBuilder.lastIndexOf("*/", int1);
        }

        string = stringBuilder.toString();
        stringBuilder.setLength(0);
        return string;
    }

    public static ArrayList<String> parseTokens(String string) {
        ArrayList arrayList = new ArrayList();

        while (true) {
            int int0 = 0;
            int int1 = 0;
            int int2 = 0;
            if (string.indexOf("}", int1 + 1) == -1) {
                if (string.trim().length() > 0) {
                    arrayList.add(string.trim());
                }

                return arrayList;
            }

            do {
                int1 = string.indexOf("{", int1 + 1);
                int2 = string.indexOf("}", int2 + 1);
                if ((int2 >= int1 || int2 == -1) && int1 != -1) {
                    int2 = int1;
                    int0++;
                } else {
                    int1 = int2;
                    int0--;
                }
            } while (int0 > 0);

            arrayList.add(string.substring(0, int1 + 1).trim());
            string = string.substring(int1 + 1);
        }
    }

    public static class Block implements ScriptParser.BlockElement {
        public String type;
        public String id;
        public final ArrayList<ScriptParser.BlockElement> elements = new ArrayList<>();
        public final ArrayList<ScriptParser.Value> values = new ArrayList<>();
        public final ArrayList<ScriptParser.Block> children = new ArrayList<>();

        @Override
        public ScriptParser.Block asBlock() {
            return this;
        }

        @Override
        public ScriptParser.Value asValue() {
            return null;
        }

        public boolean isEmpty() {
            return this.elements.isEmpty();
        }

        @Override
        public void prettyPrint(int indent, StringBuilder sb, String eol) {
            for (int int0 = 0; int0 < indent; int0++) {
                sb.append('\t');
            }

            sb.append(this.type);
            if (this.id != null) {
                sb.append(" ");
                sb.append(this.id);
            }

            sb.append(eol);

            for (int int1 = 0; int1 < indent; int1++) {
                sb.append('\t');
            }

            sb.append('{');
            sb.append(eol);
            this.prettyPrintElements(indent + 1, sb, eol);

            for (int int2 = 0; int2 < indent; int2++) {
                sb.append('\t');
            }

            sb.append('}');
            sb.append(eol);
        }

        public void prettyPrintElements(int indent, StringBuilder sb, String eol) {
            ScriptParser.BlockElement blockElement0 = null;

            for (ScriptParser.BlockElement blockElement1 : this.elements) {
                if (blockElement1.asBlock() != null && blockElement0 != null) {
                    sb.append(eol);
                }

                if (blockElement1.asValue() != null && blockElement0 instanceof ScriptParser.Block) {
                    sb.append(eol);
                }

                blockElement1.prettyPrint(indent, sb, eol);
                blockElement0 = blockElement1;
            }
        }

        public ScriptParser.Block addBlock(String _type, String _id) {
            ScriptParser.Block block = new ScriptParser.Block();
            block.type = _type;
            block.id = _id;
            this.elements.add(block);
            this.children.add(block);
            return block;
        }

        public ScriptParser.Block getBlock(String _type, String _id) {
            for (ScriptParser.Block block : this.children) {
                if (block.type.equals(_type) && (block.id != null && block.id.equals(_id) || block.id == null && _id == null)) {
                    return block;
                }
            }

            return null;
        }

        public ScriptParser.Value getValue(String key) {
            for (ScriptParser.Value value : this.values) {
                int int0 = value.string.indexOf(61);
                if (int0 > 0 && value.getKey().trim().equals(key)) {
                    return value;
                }
            }

            return null;
        }

        public void setValue(String key, String value) {
            ScriptParser.Value _value = this.getValue(key);
            if (_value == null) {
                this.addValue(key, value);
            } else {
                _value.string = key + " = " + value;
            }
        }

        public ScriptParser.Value addValue(String key, String value) {
            ScriptParser.Value _value = new ScriptParser.Value();
            _value.string = key + " = " + value;
            this.elements.add(_value);
            this.values.add(_value);
            return _value;
        }

        public void moveValueAfter(String keyMove, String keyAfter) {
            ScriptParser.Value value0 = this.getValue(keyMove);
            ScriptParser.Value value1 = this.getValue(keyAfter);
            if (value0 != null && value1 != null) {
                this.elements.remove(value0);
                this.values.remove(value0);
                this.elements.add(this.elements.indexOf(value1) + 1, value0);
                this.values.add(this.values.indexOf(value1) + 1, value0);
            }
        }
    }

    public interface BlockElement {
        ScriptParser.Block asBlock();

        ScriptParser.Value asValue();

        void prettyPrint(int var1, StringBuilder var2, String var3);
    }

    public static class Value implements ScriptParser.BlockElement {
        public String string;

        @Override
        public ScriptParser.Block asBlock() {
            return null;
        }

        @Override
        public ScriptParser.Value asValue() {
            return this;
        }

        @Override
        public void prettyPrint(int indent, StringBuilder sb, String eol) {
            for (int int0 = 0; int0 < indent; int0++) {
                sb.append('\t');
            }

            sb.append(this.string.trim());
            sb.append(',');
            sb.append(eol);
        }

        public String getKey() {
            int int0 = this.string.indexOf(61);
            return int0 == -1 ? this.string : this.string.substring(0, int0);
        }

        public String getValue() {
            int int0 = this.string.indexOf(61);
            return int0 == -1 ? "" : this.string.substring(int0 + 1);
        }
    }
}
