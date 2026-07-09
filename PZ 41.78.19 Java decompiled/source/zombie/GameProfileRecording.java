// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.core.skinnedmodel.animation.debug.GenericNameValueRecordingFrame;
import zombie.util.IPooledObject;
import zombie.util.Pool;
import zombie.util.PooledObject;
import zombie.util.list.PZArrayUtil;

public final class GameProfileRecording extends GenericNameValueRecordingFrame {
    private long m_startTime;
    private final GameProfileRecording.Row m_rootRow = new GameProfileRecording.Row();
    private final HashMap<String, Integer> m_keyValueTable = new HashMap<>();
    protected PrintStream m_outSegment = null;
    private long m_firstFrameNo = -1L;
    private final List<String> m_segmentFilePaths = new ArrayList<>();
    private int m_numFramesPerFile = 60;
    private int m_currentSegmentFrameCount = 0;

    public GameProfileRecording(String string) {
        super(string, "_times");
        this.addColumnInternal("StartTime");
        this.addColumnInternal("EndTime");
        this.addColumnInternal("SegmentNo");
        this.addColumnInternal("Spans");
        this.addColumnInternal("key");
        this.addColumnInternal("Depth");
        this.addColumnInternal("StartTime");
        this.addColumnInternal("EndTime");
        this.addColumnInternal("Time Format");
        this.addColumnInternal("x * 100ns");
    }

    public void setNumFramesPerSegment(int int0) {
        this.m_numFramesPerFile = int0;
    }

    public void setStartTime(long long0) {
        this.m_startTime = long0;
    }

    public void logTimeSpan(GameProfiler.ProfileArea profileArea) {
        if (this.m_firstFrameNo == -1L) {
            this.m_firstFrameNo = this.m_frameNumber;
        }

        GameProfileRecording.Span span = this.allocSpan(profileArea);
        GameProfileRecording.Row row = this.m_rootRow;
        if (row.Spans.isEmpty()) {
            row.StartTime = span.StartTime;
        }

        row.EndTime = span.EndTime;
        row.Spans.add(span);
    }

    protected GameProfileRecording.Span allocSpan(GameProfiler.ProfileArea profileArea0) {
        int int0 = this.getOrCreateKey(profileArea0.Key);
        long long0 = profileArea0.StartTime - this.m_startTime;
        long long1 = profileArea0.EndTime - this.m_startTime;
        GameProfileRecording.Span span0 = GameProfileRecording.Span.alloc();
        span0.key = int0;
        span0.Depth = profileArea0.Depth;
        span0.StartTime = long0;
        span0.EndTime = long1;
        int int1 = 0;

        for (int int2 = profileArea0.Children.size(); int1 < int2; int1++) {
            GameProfiler.ProfileArea profileArea1 = profileArea0.Children.get(int1);
            GameProfileRecording.Span span1 = this.allocSpan(profileArea1);
            span0.Children.add(span1);
        }

        return span0;
    }

    private int getOrCreateKey(String string) {
        Integer integer = this.m_keyValueTable.get(string);
        if (integer == null) {
            integer = this.m_keyValueTable.size();
            this.m_keyValueTable.put(string, integer);
            this.m_headerDirty = true;
        }

        return integer;
    }

    @Override
    public String getValueAt(int var1) {
        throw new RuntimeException("Not implemented. Use getValueAt(row, col)");
    }

    @Override
    protected void onColumnAdded() {
    }

    @Override
    public void reset() {
        this.m_rootRow.reset();
    }

    protected void openSegmentFile(boolean boolean0) {
        if (this.m_outSegment != null) {
            this.m_outSegment.flush();
            this.m_outSegment.close();
        }

        String string = String.format("%s%s_%04d", this.m_fileKey, this.m_valuesFileNameSuffix, this.m_segmentFilePaths.size());
        this.m_outSegment = AnimationPlayerRecorder.openFileStream(string, boolean0, this.m_segmentFilePaths::add);
        this.m_currentSegmentFrameCount = 0;
        this.m_headerDirty = true;
    }

    @Override
    public void close() {
        if (this.m_outSegment != null) {
            this.m_outSegment.close();
            this.m_outSegment = null;
        }
    }

    @Override
    public void closeAndDiscard() {
        super.closeAndDiscard();
        PZArrayUtil.forEach(this.m_segmentFilePaths, ZomboidFileSystem.instance::tryDeleteFile);
        this.m_segmentFilePaths.clear();
    }

    @Override
    protected void writeData() {
        if (this.m_outValues == null) {
            this.openValuesFile(false);
        }

        StringBuilder stringBuilder = this.m_lineBuffer;
        stringBuilder.setLength(0);
        this.m_currentSegmentFrameCount++;
        if (this.m_outSegment == null || this.m_currentSegmentFrameCount >= this.m_numFramesPerFile) {
            this.openSegmentFile(false);
        }

        this.writeDataRow(stringBuilder, this.m_rootRow);
        this.m_outSegment.print(this.m_frameNumber);
        this.m_outSegment.println(stringBuilder);
        stringBuilder = this.m_lineBuffer;
        stringBuilder.setLength(0);
        this.writeFrameTimeRow(stringBuilder, this.m_rootRow, this.m_segmentFilePaths.size() - 1);
        this.m_outValues.print(this.m_frameNumber);
        this.m_outValues.println(stringBuilder);
    }

    private void writeDataRow(StringBuilder stringBuilder, GameProfileRecording.Row row) {
        int int0 = 0;

        for (int int1 = row.Spans.size(); int0 < int1; int0++) {
            GameProfileRecording.Span span = row.Spans.get(int0);
            this.writeSpan(stringBuilder, row, span);
        }
    }

    private void writeFrameTimeRow(StringBuilder stringBuilder, GameProfileRecording.Row row, int int0) {
        appendCell(stringBuilder, row.StartTime / 100L);
        appendCell(stringBuilder, row.EndTime / 100L);
        appendCell(stringBuilder, int0);
    }

    private void writeSpan(StringBuilder stringBuilder, GameProfileRecording.Row row, GameProfileRecording.Span span0) {
        long long0 = (span0.StartTime - row.StartTime) / 100L;
        long long1 = (span0.EndTime - span0.StartTime) / 100L;
        appendCell(stringBuilder, span0.key);
        appendCell(stringBuilder, span0.Depth);
        appendCell(stringBuilder, long0);
        appendCell(stringBuilder, long1);
        int int0 = 0;

        for (int int1 = span0.Children.size(); int0 < int1; int0++) {
            GameProfileRecording.Span span1 = span0.Children.get(int0);
            this.writeSpan(stringBuilder, row, span1);
        }
    }

    @Override
    protected void writeHeader() {
        super.writeHeader();
        this.m_outHeader.println();
        this.m_outHeader.println("Segmentation Info");
        this.m_outHeader.println("FirstFrame," + this.m_firstFrameNo);
        this.m_outHeader.println("NumFramesPerFile," + this.m_numFramesPerFile);
        this.m_outHeader.println("NumFiles," + this.m_segmentFilePaths.size());
        this.m_outHeader.println();
        this.m_outHeader.println("KeyNamesTable");
        this.m_outHeader.println("Index,Name");
        StringBuilder stringBuilder = new StringBuilder();

        for (Entry entry : this.m_keyValueTable.entrySet()) {
            stringBuilder.setLength(0);
            stringBuilder.append(entry.getValue());
            stringBuilder.append(",");
            stringBuilder.append((String)entry.getKey());
            this.m_outHeader.println(stringBuilder);
        }
    }

    public static class Row {
        long StartTime;
        long EndTime;
        final List<GameProfileRecording.Span> Spans = new ArrayList<>();

        public void reset() {
            IPooledObject.release(this.Spans);
        }
    }

    public static class Span extends PooledObject {
        int key;
        int Depth;
        long StartTime;
        long EndTime;
        final List<GameProfileRecording.Span> Children = new ArrayList<>();
        private static final Pool<GameProfileRecording.Span> s_pool = new Pool<>(GameProfileRecording.Span::new);

        @Override
        public void onReleased() {
            super.onReleased();
            IPooledObject.release(this.Children);
        }

        public static GameProfileRecording.Span alloc() {
            return s_pool.alloc();
        }
    }
}
