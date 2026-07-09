// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.fileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import zombie.GameWindow;
import zombie.core.logger.ExceptionLogger;
import zombie.gameStates.GameLoadingState;

public final class FileSystemImpl extends FileSystem {
    private final ArrayList<DeviceList> m_devices = new ArrayList<>();
    private final ArrayList<FileSystemImpl.AsyncItem> m_in_progress = new ArrayList<>();
    private final ArrayList<FileSystemImpl.AsyncItem> m_pending = new ArrayList<>();
    private int m_last_id = 0;
    private DiskFileDevice m_disk_device;
    private MemoryFileDevice m_memory_device;
    private final HashMap<String, TexturePackDevice> m_texturepack_devices = new HashMap<>();
    private final HashMap<String, DeviceList> m_texturepack_devicelists = new HashMap<>();
    private DeviceList m_default_device;
    private final ExecutorService executor;
    private final AtomicBoolean lock = new AtomicBoolean(false);
    private final ArrayList<FileSystemImpl.AsyncItem> m_added = new ArrayList<>();
    public static final HashMap<String, Boolean> TexturePackCompression = new HashMap<>();

    public FileSystemImpl() {
        this.m_disk_device = new DiskFileDevice("disk");
        this.m_memory_device = new MemoryFileDevice();
        this.m_default_device = new DeviceList();
        this.m_default_device.add(this.m_disk_device);
        this.m_default_device.add(this.m_memory_device);
        int int0 = Runtime.getRuntime().availableProcessors() <= 4 ? 2 : 4;
        this.executor = Executors.newFixedThreadPool(int0);
    }

    @Override
    public boolean mount(IFileDevice var1) {
        return true;
    }

    @Override
    public boolean unMount(IFileDevice iFileDevice) {
        return this.m_devices.remove(iFileDevice);
    }

    @Override
    public IFile open(DeviceList deviceList, String string, int int0) {
        IFile iFile = deviceList.createFile();
        if (iFile != null) {
            if (iFile.open(string, int0)) {
                return iFile;
            } else {
                iFile.release();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void close(IFile iFile) {
        iFile.close();
        iFile.release();
    }

    @Override
    public int openAsync(DeviceList deviceList, String string, int int0, IFileTask2Callback iFileTask2Callback) {
        IFile iFile = deviceList.createFile();
        if (iFile != null) {
            FileSystemImpl.OpenTask openTask = new FileSystemImpl.OpenTask(this);
            openTask.m_file = iFile;
            openTask.m_path = string;
            openTask.m_mode = int0;
            openTask.m_cb = iFileTask2Callback;
            return this.runAsync(openTask);
        } else {
            return -1;
        }
    }

    @Override
    public void closeAsync(IFile iFile, IFileTask2Callback iFileTask2Callback) {
        FileSystemImpl.CloseTask closeTask = new FileSystemImpl.CloseTask(this);
        closeTask.m_file = iFile;
        closeTask.m_cb = iFileTask2Callback;
        this.runAsync(closeTask);
    }

    @Override
    public void cancelAsync(int int0) {
        if (int0 != -1) {
            for (int int1 = 0; int1 < this.m_pending.size(); int1++) {
                FileSystemImpl.AsyncItem asyncItem0 = this.m_pending.get(int1);
                if (asyncItem0.m_id == int0) {
                    asyncItem0.m_future.cancel(false);
                    return;
                }
            }

            for (int int2 = 0; int2 < this.m_in_progress.size(); int2++) {
                FileSystemImpl.AsyncItem asyncItem1 = this.m_in_progress.get(int2);
                if (asyncItem1.m_id == int0) {
                    asyncItem1.m_future.cancel(false);
                    return;
                }
            }

            while (!this.lock.compareAndSet(false, true)) {
                Thread.onSpinWait();
            }

            for (int int3 = 0; int3 < this.m_added.size(); int3++) {
                FileSystemImpl.AsyncItem asyncItem2 = this.m_added.get(int3);
                if (asyncItem2.m_id == int0) {
                    asyncItem2.m_future.cancel(false);
                    break;
                }
            }

            this.lock.set(false);
        }
    }

    @Override
    public InputStream openStream(DeviceList deviceList, String string) throws IOException {
        return deviceList.createStream(string);
    }

    @Override
    public void closeStream(InputStream var1) {
    }

    private int runAsync(FileSystemImpl.AsyncItem asyncItem) {
        Thread thread = Thread.currentThread();
        if (thread != GameWindow.GameThread && thread != GameLoadingState.loader) {
            boolean boolean0 = true;
        }

        while (!this.lock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }

        asyncItem.m_id = this.m_last_id++;
        if (this.m_last_id < 0) {
            this.m_last_id = 0;
        }

        this.m_added.add(asyncItem);
        this.lock.set(false);
        return asyncItem.m_id;
    }

    @Override
    public int runAsync(FileTask fileTask) {
        FileSystemImpl.AsyncItem asyncItem = new FileSystemImpl.AsyncItem();
        asyncItem.m_task = fileTask;
        asyncItem.m_future = new FutureTask<>(fileTask);
        return this.runAsync(asyncItem);
    }

    @Override
    public void updateAsyncTransactions() {
        int int0 = Math.min(this.m_in_progress.size(), 16);

        for (int int1 = 0; int1 < int0; int1++) {
            FileSystemImpl.AsyncItem asyncItem0 = this.m_in_progress.get(int1);
            if (asyncItem0.m_future.isDone()) {
                this.m_in_progress.remove(int1--);
                int0--;
                if (asyncItem0.m_future.isCancelled()) {
                    boolean boolean0 = true;
                } else {
                    Object object = null;

                    try {
                        object = asyncItem0.m_future.get();
                    } catch (Throwable throwable) {
                        ExceptionLogger.logException(throwable, asyncItem0.m_task.getErrorMessage());
                    }

                    asyncItem0.m_task.handleResult(object);
                }

                asyncItem0.m_task.done();
                asyncItem0.m_task = null;
                asyncItem0.m_future = null;
            }
        }

        while (!this.lock.compareAndSet(false, true)) {
            Thread.onSpinWait();
        }

        int int2 = 1;
        if (int2) {
            for (int int3 = 0; int3 < this.m_added.size(); int3++) {
                FileSystemImpl.AsyncItem asyncItem1 = this.m_added.get(int3);
                int int4 = this.m_pending.size();

                for (int int5 = 0; int5 < this.m_pending.size(); int5++) {
                    FileSystemImpl.AsyncItem asyncItem2 = this.m_pending.get(int5);
                    if (asyncItem1.m_task.m_priority > asyncItem2.m_task.m_priority) {
                        int4 = int5;
                        break;
                    }
                }

                this.m_pending.add(int4, asyncItem1);
            }
        } else {
            this.m_pending.addAll(this.m_added);
        }

        this.m_added.clear();
        this.lock.set(false);
        int2 = 16 - this.m_in_progress.size();

        while (int2 > 0 && !this.m_pending.isEmpty()) {
            FileSystemImpl.AsyncItem asyncItem3 = this.m_pending.remove(0);
            if (!asyncItem3.m_future.isCancelled()) {
                this.m_in_progress.add(asyncItem3);
                this.executor.submit(asyncItem3.m_future);
                int2--;
            }
        }
    }

    @Override
    public boolean hasWork() {
        if (this.m_pending.isEmpty() && this.m_in_progress.isEmpty()) {
            while (!this.lock.compareAndSet(false, true)) {
                Thread.onSpinWait();
            }

            boolean boolean0 = !this.m_added.isEmpty();
            this.lock.set(false);
            return boolean0;
        } else {
            return true;
        }
    }

    @Override
    public DeviceList getDefaultDevice() {
        return this.m_default_device;
    }

    @Override
    public void mountTexturePack(String string, FileSystem.TexturePackTextures texturePackTextures, int int0) {
        TexturePackDevice texturePackDevice = new TexturePackDevice(string, int0);
        if (texturePackTextures != null) {
            try {
                texturePackDevice.getSubTextureInfo(texturePackTextures);
            } catch (IOException iOException) {
                ExceptionLogger.logException(iOException);
            }
        }

        this.m_texturepack_devices.put(string, texturePackDevice);
        DeviceList deviceList = new DeviceList();
        deviceList.add(texturePackDevice);
        this.m_texturepack_devicelists.put(texturePackDevice.name(), deviceList);
    }

    @Override
    public DeviceList getTexturePackDevice(String string) {
        return this.m_texturepack_devicelists.get(string);
    }

    @Override
    public int getTexturePackFlags(String string) {
        return this.m_texturepack_devices.get(string).getTextureFlags();
    }

    @Override
    public boolean getTexturePackAlpha(String string1, String string0) {
        return this.m_texturepack_devices.get(string1).isAlpha(string0);
    }

    private static final class AsyncItem {
        int m_id;
        FileTask m_task;
        FutureTask<Object> m_future;
    }

    private static final class CloseTask extends FileTask {
        IFile m_file;
        IFileTask2Callback m_cb;

        CloseTask(FileSystem fileSystem) {
            super(fileSystem);
        }

        @Override
        public Object call() throws Exception {
            this.m_file.close();
            this.m_file.release();
            return null;
        }

        @Override
        public void handleResult(Object object) {
            if (this.m_cb != null) {
                this.m_cb.onFileTaskFinished(this.m_file, object);
            }
        }

        @Override
        public void done() {
            this.m_file = null;
            this.m_cb = null;
        }
    }

    private static final class OpenTask extends FileTask {
        IFile m_file;
        String m_path;
        int m_mode;
        IFileTask2Callback m_cb;

        OpenTask(FileSystem fileSystem) {
            super(fileSystem);
        }

        @Override
        public Object call() throws Exception {
            return this.m_file.open(this.m_path, this.m_mode);
        }

        @Override
        public void handleResult(Object object) {
            if (this.m_cb != null) {
                this.m_cb.onFileTaskFinished(this.m_file, object);
            }
        }

        @Override
        public void done() {
            if ((this.m_mode & 5) == 5) {
                this.m_file_system.closeAsync(this.m_file, null);
            }

            this.m_file = null;
            this.m_path = null;
            this.m_cb = null;
        }
    }
}
