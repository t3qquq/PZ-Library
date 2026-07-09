// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation.debug;

import java.io.PrintStream;
import java.util.HashMap;
import zombie.ZomboidFileSystem;
import zombie.util.list.PZArrayUtil;

public abstract class GenericNameValueRecordingFrame {
    protected String[] m_columnNames = new String[0];
    protected final HashMap<String, Integer> m_nameIndices = new HashMap<>();
    protected boolean m_headerDirty = false;
    protected final String m_fileKey;
    protected PrintStream m_outHeader = null;
    protected PrintStream m_outValues = null;
    private String m_headerFilePath = null;
    private String m_valuesFilePath = null;
    protected int m_frameNumber = -1;
    protected static final String delim = ",";
    protected final String m_valuesFileNameSuffix;
    private String m_previousLine = null;
    private int m_previousFrameNo = -1;
    protected final StringBuilder m_lineBuffer = new StringBuilder();

    public GenericNameValueRecordingFrame(String string0, String string1) {
        this.m_fileKey = string0;
        this.m_valuesFileNameSuffix = string1;
    }

    protected int addColumnInternal(String string) {
        int int0 = this.m_columnNames.length;
        this.m_columnNames = PZArrayUtil.add(this.m_columnNames, string);
        this.m_nameIndices.put(string, int0);
        this.m_headerDirty = true;
        this.onColumnAdded();
        return int0;
    }

    public int getOrCreateColumn(String string) {
        return this.m_nameIndices.containsKey(string) ? this.m_nameIndices.get(string) : this.addColumnInternal(string);
    }

    public void setFrameNumber(int int0) {
        this.m_frameNumber = int0;
    }

    public int getColumnCount() {
        return this.m_columnNames.length;
    }

    public String getNameAt(int int0) {
        return this.m_columnNames[int0];
    }

    public abstract String getValueAt(int var1);

    protected void openHeader(boolean boolean0) {
        this.m_outHeader = AnimationPlayerRecorder.openFileStream(this.m_fileKey + "_header", boolean0, string -> this.m_headerFilePath = string);
    }

    protected void openValuesFile(boolean boolean0) {
        this.m_outValues = AnimationPlayerRecorder.openFileStream(
            this.m_fileKey + this.m_valuesFileNameSuffix, boolean0, string -> this.m_valuesFilePath = string
        );
    }

    public void writeLine() {
        if (this.m_headerDirty || this.m_outHeader == null) {
            this.m_headerDirty = false;
            this.writeHeader();
        }

        this.writeData();
    }

    public void close() {
        if (this.m_outHeader != null) {
            this.m_outHeader.close();
            this.m_outHeader = null;
        }

        if (this.m_outValues != null) {
            this.m_outValues.close();
            this.m_outValues = null;
        }
    }

    public void closeAndDiscard() {
        this.close();
        ZomboidFileSystem.instance.tryDeleteFile(this.m_headerFilePath);
        this.m_headerFilePath = null;
        ZomboidFileSystem.instance.tryDeleteFile(this.m_valuesFilePath);
        this.m_valuesFilePath = null;
    }

    protected abstract void onColumnAdded();

    public abstract void reset();

    protected void writeHeader() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("frameNo");
        this.writeHeader(stringBuilder);
        this.openHeader(false);
        this.m_outHeader.println(stringBuilder);
    }

    protected void writeHeader(StringBuilder stringBuilder) {
        int int0 = 0;

        for (int int1 = this.getColumnCount(); int0 < int1; int0++) {
            appendCell(stringBuilder, this.getNameAt(int0));
        }
    }

    protected void writeData() {
        if (this.m_outValues == null) {
            this.openValuesFile(false);
        }

        StringBuilder stringBuilder = this.m_lineBuffer;
        stringBuilder.setLength(0);
        this.writeData(stringBuilder);
        if (this.m_previousLine == null || !this.m_previousLine.contentEquals(stringBuilder)) {
            this.m_outValues.print(this.m_frameNumber);
            this.m_outValues.println(stringBuilder);
            this.m_previousLine = stringBuilder.toString();
            this.m_previousFrameNo = this.m_frameNumber;
        }
    }

    protected void writeData(StringBuilder stringBuilder) {
        int int0 = 0;

        for (int int1 = this.getColumnCount(); int0 < int1; int0++) {
            appendCell(stringBuilder, this.getValueAt(int0));
        }
    }

    public static StringBuilder appendCell(StringBuilder stringBuilder) {
        return stringBuilder.append(",");
    }

    public static StringBuilder appendCell(StringBuilder stringBuilder, String string) {
        return stringBuilder.append(",").append(string);
    }

    public static StringBuilder appendCell(StringBuilder stringBuilder, float float0) {
        return stringBuilder.append(",").append(float0);
    }

    public static StringBuilder appendCell(StringBuilder stringBuilder, int int0) {
        return stringBuilder.append(",").append(int0);
    }

    public static StringBuilder appendCell(StringBuilder stringBuilder, long long0) {
        return stringBuilder.append(",").append(long0);
    }

    public static StringBuilder appendCellQuot(StringBuilder stringBuilder, String string) {
        return stringBuilder.append(",").append('"').append(string).append('"');
    }
}
