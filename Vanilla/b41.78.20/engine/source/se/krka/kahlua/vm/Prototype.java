/*
 * Decompiled with CFR 0.152.
 */
package se.krka.kahlua.vm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaClosure;

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

    public Prototype(DataInputStream dataInputStream, boolean bl, String string, int n) throws IOException {
        int n2;
        int n3;
        int n4;
        int n5;
        this.name = Prototype.readLuaString(dataInputStream, n, bl);
        if (this.name == null) {
            this.name = string;
        }
        dataInputStream.readInt();
        dataInputStream.readInt();
        this.numUpvalues = dataInputStream.read();
        this.numParams = dataInputStream.read();
        int n6 = dataInputStream.read();
        this.isVararg = (n6 & 2) != 0;
        this.maxStacksize = dataInputStream.read();
        int n7 = Prototype.toInt(dataInputStream.readInt(), bl);
        this.code = new int[n7];
        for (n5 = 0; n5 < n7; ++n5) {
            this.code[n5] = n4 = Prototype.toInt(dataInputStream.readInt(), bl);
        }
        n5 = Prototype.toInt(dataInputStream.readInt(), bl);
        this.constants = new Object[n5];
        for (n4 = 0; n4 < n5; ++n4) {
            Object object = null;
            n3 = dataInputStream.read();
            switch (n3) {
                case 0: {
                    break;
                }
                case 1: {
                    int n8 = dataInputStream.read();
                    object = n8 == 0 ? Boolean.FALSE : Boolean.TRUE;
                    break;
                }
                case 3: {
                    long l = dataInputStream.readLong();
                    if (bl) {
                        l = Prototype.rev(l);
                    }
                    object = KahluaUtil.toDouble(Double.longBitsToDouble(l));
                    break;
                }
                case 4: {
                    object = Prototype.readLuaString(dataInputStream, n, bl);
                    break;
                }
                default: {
                    throw new IOException("unknown constant type: " + n3);
                }
            }
            this.constants[n4] = object;
        }
        n4 = Prototype.toInt(dataInputStream.readInt(), bl);
        this.prototypes = new Prototype[n4];
        for (n2 = 0; n2 < n4; ++n2) {
            this.prototypes[n2] = new Prototype(dataInputStream, bl, this.name, n);
        }
        int n9 = Prototype.toInt(dataInputStream.readInt(), bl);
        this.lines = new int[n9];
        for (n2 = 0; n2 < n9; ++n2) {
            this.lines[n2] = n3 = Prototype.toInt(dataInputStream.readInt(), bl);
        }
        n9 = Prototype.toInt(dataInputStream.readInt(), bl);
        for (n2 = 0; n2 < n9; ++n2) {
            Prototype.readLuaString(dataInputStream, n, bl);
            dataInputStream.readInt();
            dataInputStream.readInt();
        }
        n9 = Prototype.toInt(dataInputStream.readInt(), bl);
        for (n2 = 0; n2 < n9; ++n2) {
            Prototype.readLuaString(dataInputStream, n, bl);
        }
    }

    public String toString() {
        return this.name;
    }

    private static String readLuaString(DataInputStream dataInputStream, int n, boolean bl) throws IOException {
        int n2;
        long l = 0L;
        if (n == 4) {
            n2 = dataInputStream.readInt();
            l = Prototype.toInt(n2, bl);
        } else if (n == 8) {
            l = Prototype.toLong(dataInputStream.readLong(), bl);
        } else {
            Prototype.loadAssert(false, "Bad string size");
        }
        if (l == 0L) {
            return null;
        }
        Prototype.loadAssert(--l < 65536L, "Too long string:" + l);
        n2 = (int)l;
        byte[] byArray = new byte[3 + n2];
        byArray[0] = (byte)(n2 >> 8 & 0xFF);
        byArray[1] = (byte)(n2 & 0xFF);
        dataInputStream.readFully(byArray, 2, n2 + 1);
        Prototype.loadAssert(byArray[2 + n2] == 0, "String loading");
        DataInputStream dataInputStream2 = new DataInputStream(new ByteArrayInputStream(byArray));
        String string = dataInputStream2.readUTF();
        dataInputStream2.close();
        return string;
    }

    public static int rev(int n) {
        int n2 = n >>> 24 & 0xFF;
        int n3 = n >>> 16 & 0xFF;
        int n4 = n >>> 8 & 0xFF;
        int n5 = n & 0xFF;
        return n5 << 24 | n4 << 16 | n3 << 8 | n2;
    }

    public static long rev(long l) {
        long l2 = l >>> 56 & 0xFFL;
        long l3 = l >>> 48 & 0xFFL;
        long l4 = l >>> 40 & 0xFFL;
        long l5 = l >>> 32 & 0xFFL;
        long l6 = l >>> 24 & 0xFFL;
        long l7 = l >>> 16 & 0xFFL;
        long l8 = l >>> 8 & 0xFFL;
        long l9 = l & 0xFFL;
        return l9 << 56 | l8 << 48 | l7 << 40 | l6 << 32 | l5 << 24 | l4 << 16 | l3 << 8 | l2;
    }

    public static int toInt(int n, boolean bl) {
        return bl ? Prototype.rev(n) : n;
    }

    public static long toLong(long l, boolean bl) {
        return bl ? Prototype.rev(l) : l;
    }

    public static LuaClosure loadByteCode(DataInputStream dataInputStream, KahluaTable kahluaTable) throws IOException {
        int n = dataInputStream.read();
        Prototype.loadAssert(n == 27, "Signature 1");
        n = dataInputStream.read();
        Prototype.loadAssert(n == 76, "Signature 2");
        n = dataInputStream.read();
        Prototype.loadAssert(n == 117, "Signature 3");
        n = dataInputStream.read();
        Prototype.loadAssert(n == 97, "Signature 4");
        n = dataInputStream.read();
        Prototype.loadAssert(n == 81, "Version");
        n = dataInputStream.read();
        Prototype.loadAssert(n == 0, "Format");
        boolean bl = dataInputStream.read() == 1;
        n = dataInputStream.read();
        Prototype.loadAssert(n == 4, "Size int");
        int n2 = dataInputStream.read();
        Prototype.loadAssert(n2 == 4 || n2 == 8, "Size t");
        n = dataInputStream.read();
        Prototype.loadAssert(n == 4, "Size instr");
        n = dataInputStream.read();
        Prototype.loadAssert(n == 8, "Size number");
        n = dataInputStream.read();
        Prototype.loadAssert(n == 0, "Integral");
        Prototype prototype = new Prototype(dataInputStream, bl, null, n2);
        LuaClosure luaClosure = new LuaClosure(prototype, kahluaTable);
        return luaClosure;
    }

    private static void loadAssert(boolean bl, String string) throws IOException {
        if (!bl) {
            throw new IOException("Could not load bytecode:" + string);
        }
    }

    public static LuaClosure loadByteCode(InputStream inputStream, KahluaTable kahluaTable) throws IOException {
        if (!(inputStream instanceof DataInputStream)) {
            inputStream = new DataInputStream(inputStream);
        }
        return Prototype.loadByteCode((DataInputStream)inputStream, kahluaTable);
    }

    public void dump(OutputStream outputStream) throws IOException {
        DataOutputStream dataOutputStream = outputStream instanceof DataOutputStream ? (DataOutputStream)outputStream : new DataOutputStream(outputStream);
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
        int n;
        int n2;
        int n3;
        Prototype.dumpString(this.name, dataOutputStream);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.write(this.numUpvalues);
        dataOutputStream.write(this.numParams);
        dataOutputStream.write(this.isVararg ? 2 : 0);
        dataOutputStream.write(this.maxStacksize);
        int n4 = this.code.length;
        dataOutputStream.writeInt(n4);
        for (n3 = 0; n3 < n4; ++n3) {
            dataOutputStream.writeInt(this.code[n3]);
        }
        n3 = this.constants.length;
        dataOutputStream.writeInt(n3);
        for (n2 = 0; n2 < n3; ++n2) {
            Object object = this.constants[n2];
            if (object == null) {
                dataOutputStream.write(0);
                continue;
            }
            if (object instanceof Boolean) {
                dataOutputStream.write(1);
                dataOutputStream.write((Boolean)object != false ? 1 : 0);
                continue;
            }
            if (object instanceof Double) {
                dataOutputStream.write(3);
                Double d = (Double)object;
                dataOutputStream.writeLong(Double.doubleToLongBits(d));
                continue;
            }
            if (object instanceof String) {
                dataOutputStream.write(4);
                Prototype.dumpString((String)object, dataOutputStream);
                continue;
            }
            throw new RuntimeException("Bad type in constant pool");
        }
        n2 = this.prototypes.length;
        dataOutputStream.writeInt(n2);
        for (n = 0; n < n2; ++n) {
            this.prototypes[n].dumpPrototype(dataOutputStream);
        }
        n = this.lines.length;
        dataOutputStream.writeInt(n);
        for (int i = 0; i < n; ++i) {
            dataOutputStream.writeInt(this.lines[i]);
        }
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
    }

    private static void dumpString(String string, DataOutputStream dataOutputStream) throws IOException {
        if (string == null) {
            dataOutputStream.writeShort(0);
            return;
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new DataOutputStream(byteArrayOutputStream).writeUTF(string);
        byte[] byArray = byteArrayOutputStream.toByteArray();
        int n = byArray.length - 2;
        dataOutputStream.writeInt(n + 1);
        dataOutputStream.write(byArray, 2, n);
        dataOutputStream.write(0);
    }
}

