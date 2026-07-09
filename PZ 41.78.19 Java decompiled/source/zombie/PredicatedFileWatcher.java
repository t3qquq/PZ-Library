// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.io.File;
import java.util.function.Predicate;
import zombie.debug.DebugLog;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;

/**
 * PredicatedFileWatcher  An advanced form of the regular DebugFileWatcher
 */
public final class PredicatedFileWatcher {
    private final String m_path;
    private final Predicate<String> m_predicate;
    private final PredicatedFileWatcher.IPredicatedFileWatcherCallback m_callback;

    public PredicatedFileWatcher(Predicate<String> predicate, PredicatedFileWatcher.IPredicatedFileWatcherCallback callback) {
        this(null, predicate, callback);
    }

    public PredicatedFileWatcher(String path, PredicatedFileWatcher.IPredicatedFileWatcherCallback callback) {
        this(path, null, callback);
    }

    public <T> PredicatedFileWatcher(String path, Class<T> clazz, PredicatedFileWatcher.IPredicatedDataPacketFileWatcherCallback<T> callback) {
        this(path, null, new PredicatedFileWatcher.GenericPredicatedFileWatcherCallback(clazz, callback));
    }

    public PredicatedFileWatcher(String path, Predicate<String> predicate, PredicatedFileWatcher.IPredicatedFileWatcherCallback callback) {
        this.m_path = this.processPath(path);
        this.m_predicate = predicate != null ? predicate : this::pathsEqual;
        this.m_callback = callback;
    }

    public String getPath() {
        return this.m_path;
    }

    private String processPath(String string) {
        return string != null ? ZomboidFileSystem.processFilePath(string, File.separatorChar) : null;
    }

    private boolean pathsEqual(String string) {
        return string.equals(this.m_path);
    }

    public void onModified(String entryKey) {
        if (this.m_predicate.test(entryKey)) {
            this.m_callback.call(entryKey);
        }
    }

    public static class GenericPredicatedFileWatcherCallback<T> implements PredicatedFileWatcher.IPredicatedFileWatcherCallback {
        private final Class<T> m_class;
        private final PredicatedFileWatcher.IPredicatedDataPacketFileWatcherCallback<T> m_callback;

        public GenericPredicatedFileWatcherCallback(
            Class<T> clazz, PredicatedFileWatcher.IPredicatedDataPacketFileWatcherCallback<T> iPredicatedDataPacketFileWatcherCallback
        ) {
            this.m_class = clazz;
            this.m_callback = iPredicatedDataPacketFileWatcherCallback;
        }

        @Override
        public void call(String string) {
            Object object;
            try {
                object = PZXmlUtil.parse(this.m_class, string);
            } catch (PZXmlParserException pZXmlParserException) {
                DebugLog.General.error("Exception thrown. " + pZXmlParserException);
                return;
            }

            this.m_callback.call((T)object);
        }
    }

    public interface IPredicatedDataPacketFileWatcherCallback<T> {
        void call(T var1);
    }

    public interface IPredicatedFileWatcherCallback {
        void call(String entryKey);
    }
}
