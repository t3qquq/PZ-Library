// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.vm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class Prototype {
    public int[] code;
    public Object[] constants;
    public Prototype[] prototypes;
    public int numParams;
    public boolean isVararg;
    public String name;
    public int[] lines;
    public int numUpvalues;
    public int maxStacksize;
    public String file;
    public String filename;
    public String[] locvars;
    public int[] locvarlines;

    public Prototype() {
    }

    public Prototype(DataInputStream arg0, boolean arg1, String arg2, int arg3) throws IOException {
        this.name = readLuaString(arg0, arg3, arg1);
        if (this.name == null) {
            this.name = arg2;
        }

        arg0.readInt();
        arg0.readInt();
        this.numUpvalues = arg0.read();
        this.numParams = arg0.read();
        int int0 = arg0.read();
        this.isVararg = (int0 & 2) != 0;
        this.maxStacksize = arg0.read();
        int int1 = toInt(arg0.readInt(), arg1);
        this.code = new int[int1];

        for (int int2 = 0; int2 < int1; int2++) {
            int int3 = toInt(arg0.readInt(), arg1);
            this.code[int2] = int3;
        }

        int int4 = toInt(arg0.readInt(), arg1);
        this.constants = new Object[int4];

        for (int int5 = 0; int5 < int4; int5++) {
            Object object = null;
            int int6 = arg0.read();

            this.constants[int5] = switch (int6) {
                case 0 -> {
                }
                case 1 -> {
                    int int7 = arg0.read();
                    yield int7 == 0 ? Boolean.FALSE : Boolean.TRUE;
                }
                default -> throw new IOException("unknown constant type: " + int6);
                case 3 -> {
                    long long0 = arg0.readLong();
                    if (arg1) {
                        long0 = rev(long0);
                    }

                    yield KahluaUtil.toDouble(Double.longBitsToDouble(long0));
                }
                case 4 -> readLuaString(arg0, arg3, arg1);
            };
        }

        int int8 = toInt(arg0.readInt(), arg1);
        this.prototypes = new Prototype[int8];

        for (int int9 = 0; int9 < int8; int9++) {
            this.prototypes[int9] = new Prototype(arg0, arg1, this.name, arg3);
        }

        int int10 = toInt(arg0.readInt(), arg1);
        this.lines = new int[int10];

        for (int int11 = 0; int11 < int10; int11++) {
            int int12 = toInt(arg0.readInt(), arg1);
            this.lines[int11] = int12;
        }

        int10 = toInt(arg0.readInt(), arg1);

        for (int int13 = 0; int13 < int10; int13++) {
            readLuaString(arg0, arg3, arg1);
            arg0.readInt();
            arg0.readInt();
        }

        int10 = toInt(arg0.readInt(), arg1);

        for (int int14 = 0; int14 < int10; int14++) {
            readLuaString(arg0, arg3, arg1);
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    private static String readLuaString(DataInputStream dataInputStream0, int int0, boolean boolean0) throws IOException {
        long long0 = 0L;
        if (int0 == 4) {
            int int1 = dataInputStream0.readInt();
            long0 = toInt(int1, boolean0);
        } else if (int0 == 8) {
            long0 = toLong(dataInputStream0.readLong(), boolean0);
        } else {
            loadAssert(false, "Bad string size");
        }

        if (long0 == 0L) {
            return null;
        } else {
            long0--;
            loadAssert(long0 < 65536L, "Too long string:" + long0);
            int int2 = (int)long0;
            byte[] bytes = new byte[3 + int2];
            bytes[0] = (byte)(int2 >> 8 & 0xFF);
            bytes[1] = (byte)(int2 & 0xFF);
            dataInputStream0.readFully(bytes, 2, int2 + 1);
            loadAssert(bytes[2 + int2] == 0, "String loading");
            DataInputStream dataInputStream1 = new DataInputStream(new ByteArrayInputStream(bytes));
            String string = dataInputStream1.readUTF();
            dataInputStream1.close();
            return string;
        }
    }

    public static int rev(int arg0) {
        int int0 = arg0 >>> 24 & 0xFF;
        int int1 = arg0 >>> 16 & 0xFF;
        int int2 = arg0 >>> 8 & 0xFF;
        int int3 = arg0 & 0xFF;
        return int3 << 24 | int2 << 16 | int1 << 8 | int0;
    }

    public static long rev(long arg0) {
        long long0 = arg0 >>> 56 & 255L;
        long long1 = arg0 >>> 48 & 255L;
        long long2 = arg0 >>> 40 & 255L;
        long long3 = arg0 >>> 32 & 255L;
        long long4 = arg0 >>> 24 & 255L;
        long long5 = arg0 >>> 16 & 255L;
        long long6 = arg0 >>> 8 & 255L;
        long long7 = arg0 & 255L;
        return long7 << 56 | long6 << 48 | long5 << 40 | long4 << 32 | long3 << 24 | long2 << 16 | long1 << 8 | long0;
    }

    public static int toInt(int arg0, boolean arg1) {
        return arg1 ? rev(arg0) : arg0;
    }

    public static long toLong(long arg0, boolean arg1) {
        return arg1 ? rev(arg0) : arg0;
    }

    public static LuaClosure loadByteCode(DataInputStream arg0, KahluaTable arg1) throws IOException {
        int int0 = arg0.read();
        loadAssert(int0 == 27, "Signature 1");
        int0 = arg0.read();
        loadAssert(int0 == 76, "Signature 2");
        int0 = arg0.read();
        loadAssert(int0 == 117, "Signature 3");
        int0 = arg0.read();
        loadAssert(int0 == 97, "Signature 4");
        int0 = arg0.read();
        loadAssert(int0 == 81, "Version");
        int0 = arg0.read();
        loadAssert(int0 == 0, "Format");
        boolean boolean0 = arg0.read() == 1;
        int0 = arg0.read();
        loadAssert(int0 == 4, "Size int");
        int int1 = arg0.read();
        loadAssert(int1 == 4 || int1 == 8, "Size t");
        int0 = arg0.read();
        loadAssert(int0 == 4, "Size instr");
        int0 = arg0.read();
        loadAssert(int0 == 8, "Size number");
        int0 = arg0.read();
        loadAssert(int0 == 0, "Integral");
        Prototype prototype = new Prototype(arg0, boolean0, null, int1);
        return new LuaClosure(prototype, arg1);
    }

    private static void loadAssert(boolean boolean0, String string) throws IOException {
        if (!boolean0) {
            throw new IOException("Could not load bytecode:" + string);
        }
    }

    public static LuaClosure loadByteCode(InputStream arg0, KahluaTable arg1) throws IOException {
        if (!(arg0 instanceof DataInputStream)) {
            arg0 = new DataInputStream((InputStream)arg0);
        }

        return loadByteCode((DataInputStream)arg0, arg1);
    }

    public void dump(OutputStream arg0) throws IOException {
        if (!(arg0 instanceof DataOutputStream dataOutputStream)) {
            dataOutputStream = new DataOutputStream(arg0);
        }

        dataOutputStream.write(27);
        dataOutputStream.write(76);
        dataOutputStream.write(117);
        dataOutputStream.write(97);
        dataOutputStream.write(81);
        dataOutputStream.write(0);
        dataOutputStream.write(0);
        dataOutputStream.write(4);
        dataOutputStream.write(4);
        dataOutputStream.write(4);
        dataOutputStream.write(8);
        dataOutputStream.write(0);
        this.dumpPrototype(dataOutputStream);
    }

    private void dumpPrototype(DataOutputStream dataOutputStream) throws IOException {
        dumpString(this.name, dataOutputStream);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.write(this.numUpvalues);
        dataOutputStream.write(this.numParams);
        dataOutputStream.write(this.isVararg ? 2 : 0);
        dataOutputStream.write(this.maxStacksize);
        int int0 = this.code.length;
        dataOutputStream.writeInt(int0);

        for (int int1 = 0; int1 < int0; int1++) {
            dataOutputStream.writeInt(this.code[int1]);
        }

        int int2 = this.constants.length;
        dataOutputStream.writeInt(int2);

        for (int int3 = 0; int3 < int2; int3++) {
            Object object = this.constants[int3];
            if (object == null) {
                dataOutputStream.write(0);
            } else if (object instanceof Boolean) {
                dataOutputStream.write(1);
                dataOutputStream.write((Boolean)object ? 1 : 0);
            } else if (object instanceof Double) {
                dataOutputStream.write(3);
                Double double0 = (Double)object;
                dataOutputStream.writeLong(Double.doubleToLongBits(double0));
            } else {
                if (!(object instanceof String)) {
                    throw new RuntimeException("Bad type in constant pool");
                }

                dataOutputStream.write(4);
                dumpString((String)object, dataOutputStream);
            }
        }

        int int4 = this.prototypes.length;
        dataOutputStream.writeInt(int4);

        for (int int5 = 0; int5 < int4; int5++) {
            this.prototypes[int5].dumpPrototype(dataOutputStream);
        }

        int int6 = this.lines.length;
        dataOutputStream.writeInt(int6);

        for (int int7 = 0; int7 < int6; int7++) {
            dataOutputStream.writeInt(this.lines[int7]);
        }

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
    }

    private static void dumpString(String string, DataOutputStream dataOutputStream) throws IOException {
        if (string == null) {
            dataOutputStream.writeShort(0);
        } else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new DataOutputStream(byteArrayOutputStream).writeUTF(string);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            int int0 = bytes.length - 2;
            dataOutputStream.writeInt(int0 + 1);
            dataOutputStream.write(bytes, 2, int0);
            dataOutputStream.write(0);
        }
    }
}
