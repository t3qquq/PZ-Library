// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.core.logger.ExceptionLogger;

public final class DebugFileWatcher {
    private final HashMap<Path, String> m_watchedFiles = new HashMap<>();
    private final HashMap<WatchKey, Path> m_watchkeyMapping = new HashMap<>();
    private final ArrayList<PredicatedFileWatcher> m_predicateWatchers = new ArrayList<>();
    private final ArrayList<PredicatedFileWatcher> m_predicateWatchersInvoking = new ArrayList<>();
    private final FileSystem m_fs = FileSystems.getDefault();
    private WatchService m_watcher;
    private boolean m_predicateWatchersInvokingDirty = true;
    private long m_modificationTime = -1L;
    private final ArrayList<String> m_modifiedFiles = new ArrayList<>();
    public static final DebugFileWatcher instance = new DebugFileWatcher();

    private DebugFileWatcher() {
    }

    public void init() {
        try {
            this.m_watcher = this.m_fs.newWatchService();
            this.registerDirRecursive(this.m_fs.getPath(ZomboidFileSystem.instance.getMediaRootPath()));
            this.registerDirRecursive(this.m_fs.getPath(ZomboidFileSystem.instance.getMessagingDir()));
        } catch (IOException iOException) {
            this.m_watcher = null;
        }
    }

    private void registerDirRecursive(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes var2) {
                    DebugFileWatcher.this.registerDir(path);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException iOException) {
            ExceptionLogger.logException(iOException);
            this.m_watcher = null;
        }
    }

    private void registerDir(Path path) {
        try {
            WatchKey watchKey = path.register(this.m_watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
            this.m_watchkeyMapping.put(watchKey, path);
        } catch (IOException iOException) {
            ExceptionLogger.logException(iOException);
            this.m_watcher = null;
        }
    }

    private void addWatchedFile(String string) {
        if (string != null) {
            this.m_watchedFiles.put(this.m_fs.getPath(string), string);
        }
    }

    public void add(PredicatedFileWatcher predicatedFileWatcher) {
        if (!this.m_predicateWatchers.contains(predicatedFileWatcher)) {
            this.addWatchedFile(predicatedFileWatcher.getPath());
            this.m_predicateWatchers.add(predicatedFileWatcher);
            this.m_predicateWatchersInvokingDirty = true;
        }
    }

    public void addDirectory(String string) {
        if (string != null) {
            this.registerDir(this.m_fs.getPath(string));
        }
    }

    public void addDirectoryRecurse(String string) {
        if (string != null) {
            this.registerDirRecursive(this.m_fs.getPath(string));
        }
    }

    public void remove(PredicatedFileWatcher predicatedFileWatcher) {
        this.m_predicateWatchers.remove(predicatedFileWatcher);
    }

    public void update() {
        if (this.m_watcher != null) {
            for (WatchKey watchKey = this.m_watcher.poll(); watchKey != null; watchKey = this.m_watcher.poll()) {
                try {
                    Path path0 = this.m_watchkeyMapping.getOrDefault(watchKey, null);

                    for (WatchEvent watchEvent : watchKey.pollEvents()) {
                        if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            Path path1 = (Path)watchEvent.context();
                            Path path2 = path0.resolve(path1);
                            String string0 = this.m_watchedFiles.getOrDefault(path2, path2.toString());
                            this.m_modificationTime = System.currentTimeMillis();
                            if (!this.m_modifiedFiles.contains(string0)) {
                                this.m_modifiedFiles.add(string0);
                            }
                        } else if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            Path path3 = (Path)watchEvent.context();
                            Path path4 = path0.resolve(path3);
                            if (Files.isDirectory(path4)) {
                                this.registerDirRecursive(path4);
                            } else {
                                String string1 = this.m_watchedFiles.getOrDefault(path4, path4.toString());
                                this.m_modificationTime = System.currentTimeMillis();
                                if (!this.m_modifiedFiles.contains(string1)) {
                                    this.m_modifiedFiles.add(string1);
                                }
                            }
                        }
                    }
                } finally {
                    if (!watchKey.reset()) {
                        this.m_watchkeyMapping.remove(watchKey);
                    }
                }
            }

            if (!this.m_modifiedFiles.isEmpty()) {
                if (this.m_modificationTime + 2000L <= System.currentTimeMillis()) {
                    for (int int0 = this.m_modifiedFiles.size() - 1; int0 >= 0; int0--) {
                        String string2 = this.m_modifiedFiles.remove(int0);
                        this.swapWatcherArrays();

                        for (PredicatedFileWatcher predicatedFileWatcher : this.m_predicateWatchersInvoking) {
                            predicatedFileWatcher.onModified(string2);
                        }
                    }
                }
            }
        }
    }

    private void swapWatcherArrays() {
        if (this.m_predicateWatchersInvokingDirty) {
            this.m_predicateWatchersInvoking.clear();
            this.m_predicateWatchersInvoking.addAll(this.m_predicateWatchers);
            this.m_predicateWatchersInvokingDirty = false;
        }
    }
}
